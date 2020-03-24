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

import common.SessionKeys
import config.{AppConfig, ServiceErrorHandler}
import controllers.predicates.{AuthPredicate, PendingChangesPredicate}
import javax.inject.{Inject, Singleton}
import models.VatSubscriptionSuccess
import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import services._
import uk.gov.hmrc.play.bootstrap.controller.FrontendController

@Singleton
class CheckAnswersController @Inject()(val messagesApi: MessagesApi,
                                       val authenticate: AuthPredicate,
                                       val pendingDeregCheck: PendingChangesPredicate,
                                       checkAnswersService: CheckAnswersService,
                                       deregDateAnswerService: ChooseDeregDateAnswerService,
                                       updateDeregistrationService: UpdateDeregistrationService,
                                       val serviceErrorHandler: ServiceErrorHandler,
                                       implicit val appConfig: AppConfig) extends FrontendController with I18nSupport {

  val show: Action[AnyContent] = (authenticate andThen pendingDeregCheck).async { implicit user =>
    checkAnswersService.checkYourAnswersModel() map {
      case Right(answers) =>
        answers.deregDate match {
          case Some(_) => Ok(views.html.checkYourAnswers(controllers.routes.ChooseDeregistrationDateController.show().url, answers.seqAnswers))
          case None => Ok(views.html.checkYourAnswers(controllers.routes.OutstandingInvoicesController.show().url, answers.seqAnswers))
        }
      case Left(error) =>
        Logger.warn("[CheckAnswersController][show] - storedAnswerService returned an error retrieving answers: " + error.message)
        serviceErrorHandler.showInternalServerError
    }
  }

  val submit: Action[AnyContent] = authenticate.async { implicit user =>
    updateDeregistrationService.updateDereg.map {
      case Right(VatSubscriptionSuccess) => Redirect(controllers.routes.DeregistrationConfirmationController.show().url)
          .addingToSession(SessionKeys.deregSuccessful -> "true", SessionKeys.pendingDeregKey -> "true")

      case Left(error) =>
        Logger.warn("[CheckAnswersController][submit] - error returned from vat subscription when updating dereg: " + error.message)
        serviceErrorHandler.showInternalServerError
    }
  }
}