/*
 * Copyright 2021 HM Revenue & Customs
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

import common.{Constants, SessionKeys}
import config.{AppConfig, ServiceErrorHandler}
import controllers.predicates.{AuthPredicate, DeniedAccessPredicate}
import models.VatSubscriptionSuccess
import play.api.i18n.I18nSupport
import play.api.mvc._
import services._
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import utils.LoggerUtil.logWarn
import views.html.CheckYourAnswers

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class CheckAnswersController @Inject()(checkYourAnswers: CheckYourAnswers,
                                        val mcc: MessagesControllerComponents,
                                       val authenticate: AuthPredicate,
                                       val regStatusCheck: DeniedAccessPredicate,
                                       checkAnswersService: CheckAnswersService,
                                       updateDeregistrationService: UpdateDeregistrationService,
                                       val serviceErrorHandler: ServiceErrorHandler,
                                       implicit val appConfig: AppConfig,
                                       implicit val ec: ExecutionContext) extends FrontendController(mcc) with I18nSupport {

  val show: Action[AnyContent] = (authenticate andThen regStatusCheck).async { implicit user =>
    checkAnswersService.checkYourAnswersModel() map {
      case Right(answers) =>
        (answers.chooseDeregDate, answers.deregDate) match {
          case (Some(_), Some(_)) => Ok(checkYourAnswers(answers.seqAnswers))
          case (Some(_), _) => Ok(checkYourAnswers(answers.seqAnswers))
          case _ => Ok(checkYourAnswers(answers.seqAnswers))
        }
      case Left(error) =>
        logWarn("[CheckAnswersController][show] - storedAnswerService returned an error retrieving answers: " + error.message)
        serviceErrorHandler.showInternalServerError
    }
  }

  val submit: Action[AnyContent] = authenticate.async { implicit user =>
    updateDeregistrationService.updateDereg.map {
      case Right(VatSubscriptionSuccess) => Redirect(controllers.routes.DeregistrationConfirmationController.show().url)
          .addingToSession(SessionKeys.deregSuccessful -> "true", SessionKeys.registrationStatusKey -> Constants.pending)

      case Left(error) =>
        logWarn("[CheckAnswersController][submit] - error returned from vat subscription when updating dereg: " + error.message)
        serviceErrorHandler.showInternalServerError
    }
  }
}