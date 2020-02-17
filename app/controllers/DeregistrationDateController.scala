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
import forms.DeregistrationDateForm
import javax.inject.{Inject, Singleton}
import models.{DeregistrationDateModel, User, YesNo}
import play.api.Logger
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import services.{DeregDateAnswerService, OutstandingInvoicesAnswerService}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController

@Singleton
class DeregistrationDateController @Inject()(val messagesApi: MessagesApi,
                                             val authenticate: AuthPredicate,
                                             val pendingDeregCheck: PendingChangesPredicate,
                                             val deregDateAnswerService: DeregDateAnswerService,
                                             val outstandingInvoicesAnswerService: OutstandingInvoicesAnswerService,
                                             val serviceErrorHandler: ServiceErrorHandler,
                                             implicit val appConfig: AppConfig) extends FrontendController with I18nSupport {

  val form: Form[DeregistrationDateModel] = DeregistrationDateForm.deregistrationDateForm("deregistrationDate.error.mandatoryRadioOption")

  private def renderView(outstanding: Option[YesNo], form: Form[DeregistrationDateModel])
                        (implicit user: User[_]) = views.html.deregistrationDate(outstanding,form)

  val show: Action[AnyContent] = (authenticate andThen pendingDeregCheck).async { implicit user =>
    for {
      deregDateResult <- deregDateAnswerService.getAnswer
      outstandingInvoicesResult <- outstandingInvoicesAnswerService.getAnswer
    } yield (outstandingInvoicesResult, deregDateResult) match {
      case (Right(outstandingInvoices), Right(Some(deregDate))) =>
        Ok(renderView(outstandingInvoices,form.fill(deregDate)))
      case (Right(outstanding), Right(None)) =>
        Ok(renderView(outstanding,form))
      case (_,_) =>
        Logger.warn("[DeregistrationDateController][show] - storedAnswerService returned an error retrieving answers")
        serviceErrorHandler.showInternalServerError
    }
  }

  val submit: Action[AnyContent] = authenticate.async { implicit user =>
    form.bindFromRequest().fold(
      error => outstandingInvoicesAnswerService.getAnswer map {
        case Right(outstandingInvoices) => BadRequest(renderView(outstandingInvoices, error))
        case Left(err) =>
          Logger.warn("[DeregistrationDateController][submit] - storedAnswerService returned an error retrieving answers: " + err.message)
          serviceErrorHandler.showInternalServerError
      },
      data => deregDateAnswerService.storeAnswer(data) map {
        case Right(_) => Redirect(controllers.routes.CheckAnswersController.show())
        case Left(err) =>
          Logger.warn("[DeregistrationDateController][submit] - storedAnswerService returned an error storing answers: " + err.message)
          serviceErrorHandler.showInternalServerError
      }
    )
  }
}
