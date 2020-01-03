/*
 * Copyright 2020 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers

import config.{AppConfig, ServiceErrorHandler}
import controllers.predicates.{AuthPredicate, PendingChangesPredicate}
import forms.VATAccountsForm
import javax.inject.{Inject, Singleton}
import models._
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent}
import services.{AccountingMethodAnswerService, DeregReasonAnswerService, TaxableTurnoverAnswerService}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import cats.data.EitherT
import cats.instances.future._
import play.api.Logger

@Singleton
class VATAccountsController @Inject()(val messagesApi: MessagesApi,
                                      val authenticate: AuthPredicate,
                                      val pendingDeregCheck: PendingChangesPredicate,
                                      val accountingMethodAnswerService: AccountingMethodAnswerService,
                                      val deregReasonAnswerService: DeregReasonAnswerService,
                                      val taxableTurnoverAnswerService: TaxableTurnoverAnswerService,
                                      val serviceErrorHandler: ServiceErrorHandler,
                                      implicit val appConfig: AppConfig) extends FrontendController with I18nSupport {

  private def renderView(backLink: String, form: Form[VATAccountsModel] = VATAccountsForm.vatAccountsForm)
                        (implicit user: User[_]) = views.html.vatAccounts(backLink, form)

  val show: Action[AnyContent] = (authenticate andThen pendingDeregCheck).async { implicit user =>
    for {
      lastTurnoverBelow <- taxableTurnoverAnswerService.getAnswer
      reasonResult <- deregReasonAnswerService.getAnswer
      accountingResult <- accountingMethodAnswerService.getAnswer
    } yield (lastTurnoverBelow, reasonResult, accountingResult) match {
      case (Right(optionLTB), Right(Some(deregReason)),Right(Some(accountingMethod))) =>
        Ok(renderView(backLink(optionLTB, deregReason), VATAccountsForm.vatAccountsForm.fill(accountingMethod)))
      case (Right(optionLTB), Right(Some(deregReason)),Right(_)) =>
        Ok(renderView(backLink(optionLTB, deregReason)))
      case (_,_,_) =>
        Logger.warn("[VATAccountsController][show] - failed to retrieve one or more answers from answer service")
        serviceErrorHandler.showInternalServerError
    }
  }

  val submit: Action[AnyContent] = authenticate.async { implicit user =>
    VATAccountsForm.vatAccountsForm.bindFromRequest().fold(
      error => (for {
        lastTurnoverBelow <- EitherT(taxableTurnoverAnswerService.getAnswer)
        reason <- EitherT(deregReasonAnswerService.getAnswer)
      } yield (lastTurnoverBelow, reason)).value.map {
        case Right((optionLTB, Some(reason))) =>
          BadRequest(views.html.vatAccounts(backLink(optionLTB, reason), error))
        case _ =>
          Logger.warn("[VATAccountsController][submit] - failed to retrieve one or more answers from answer service")
          serviceErrorHandler.showInternalServerError
      },
      data => accountingMethodAnswerService.storeAnswer(data) map {
        case Right(_) => Redirect(controllers.routes.OptionTaxController.show())
        case Left(error) =>
          Logger.warn("[VATAccountsController][submit] - failed to store accountingMethod in answer service: " + error.message)
          serviceErrorHandler.showInternalServerError
      }
    )
  }

  def backLink(lastTurnoverBelowThreshold: Option[YesNo], deregReason: DeregistrationReason): String = {
    if(deregReason == Ceased){
      controllers.routes.CeasedTradingDateController.show().url
    } else {
      lastTurnoverBelowThreshold match {
        case Some(below) if below.value.equals(true) => controllers.routes.NextTaxableTurnoverController.show().url
        case _ => controllers.routes.WhyTurnoverBelowController.show().url
      }
    }
  }
}
