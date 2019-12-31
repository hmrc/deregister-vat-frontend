/*
 * Copyright 2019 HM Revenue & Customs
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

import cats.data.EitherT
import cats.instances.future._
import config.{AppConfig, ServiceErrorHandler}
import controllers.predicates.{AuthPredicate, PendingChangesPredicate}
import forms.NextTaxableTurnoverForm
import javax.inject.{Inject, Singleton}
import models.{BelowThreshold, No, NumberInputModel, User, Yes, YesNo, ZeroRated}
import play.api.Logger
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent}
import services.{BusinessActivityAnswerService, DeregReasonAnswerService, NextTaxableTurnoverAnswerService, TaxableTurnoverAnswerService}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController

@Singleton
class NextTaxableTurnoverController @Inject()(val messagesApi: MessagesApi,
                                              val authenticate: AuthPredicate,
                                              val pendingDeregCheck: PendingChangesPredicate,
                                              val taxableTurnoverAnswerService: TaxableTurnoverAnswerService,
                                              val businessActivityAnswerService: BusinessActivityAnswerService,
                                              val nextTaxableTurnoverAnswerService: NextTaxableTurnoverAnswerService,
                                              val deregReasonAnswerService: DeregReasonAnswerService,
                                              val serviceErrorHandler: ServiceErrorHandler,
                                              implicit val appConfig: AppConfig) extends FrontendController with I18nSupport {

  def backLink(businessActivityAnswer: Option[YesNo]): String = {
    businessActivityAnswer match {
      case Some(Yes) => controllers.zeroRated.routes.SicCodeController.show().url
      case Some(No) => controllers.zeroRated.routes.BusinessActivityController.show().url
      case _ => controllers.routes.TaxableTurnoverController.show().url
    }
  }

  private def renderView(form: Form[NumberInputModel] = NextTaxableTurnoverForm.taxableTurnoverForm, backLink: String)(implicit user: User[_]) =
    views.html.nextTaxableTurnover(form, backLink)

  val show: Action[AnyContent] = (authenticate andThen pendingDeregCheck).async { implicit user =>
    for {
      data <- nextTaxableTurnoverAnswerService.getAnswer
      businessActivity <- businessActivityAnswerService.getAnswer
    } yield (data, businessActivity) match {
      case (Right(Some(nextTaxableTurnoverAnswer)), Right(businessActivityAnswer)) =>
        Ok(renderView(NextTaxableTurnoverForm.taxableTurnoverForm.fill(nextTaxableTurnoverAnswer), backLink(businessActivityAnswer)))
      case (_, Right(businessActivityAnswer)) =>
        Ok(renderView(NextTaxableTurnoverForm.taxableTurnoverForm, backLink(businessActivityAnswer)))
      case _ =>
        Logger.warn("[NextTaxableTurnoverController][show] - storedAnswerService returned an error retrieving answers")
        serviceErrorHandler.showInternalServerError
    }
  }

  val submit: Action[AnyContent] = authenticate.async { implicit user =>
    NextTaxableTurnoverForm.taxableTurnoverForm.bindFromRequest().fold(
      error => for {
        businessActivity <- businessActivityAnswerService.getAnswer
      } yield businessActivity match {
        case Right(businessActivity) =>
          BadRequest(renderView(error, backLink(businessActivity)))
        case Left(err) =>
          Logger.warn("[NextTaxableTurnoverController][submit] - storedAnswerService returned an error retrieving answers: " + err.message)
          serviceErrorHandler.showInternalServerError
      },
      data => (for {
        _ <- EitherT(nextTaxableTurnoverAnswerService.storeAnswer(data))
        taxableTurnover <- EitherT(taxableTurnoverAnswerService.getAnswer)
        deregReason <- EitherT(deregReasonAnswerService.getAnswer)
      } yield (taxableTurnover, deregReason))
        .value.map {
        case Right((_ ,Some(ZeroRated))) => Redirect(controllers.zeroRated.routes.ZeroRatedSuppliesController.show())
        case Right((Some(_), Some(_))) if data.value > appConfig.deregThreshold => Redirect(controllers.routes.CannotDeregisterThresholdController.show())
        case Right((Some(Yes), Some(_))) => Redirect(controllers.routes.VATAccountsController.show())
        case Right((Some(No), Some(_))) => Redirect(controllers.routes.WhyTurnoverBelowController.show())
        case _ =>
          Logger.warn("[NextTaxableTurnoverController][submit] - storedAnswerService returned an error")
          serviceErrorHandler.showInternalServerError
      }
    )
  }

}
