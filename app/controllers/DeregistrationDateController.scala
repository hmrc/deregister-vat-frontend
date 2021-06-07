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

import config.{AppConfig, ServiceErrorHandler}
import controllers.predicates.{AuthPredicate, DeniedAccessPredicate}
import forms.DeregistrationDateForm
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.DeregDateAnswerService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.DeregistrationDate
import javax.inject.Inject
import utils.LoggerUtil

import scala.concurrent.{ExecutionContext, Future}

class DeregistrationDateController@Inject()(deregistrationDate: DeregistrationDate,
                                             val mcc: MessagesControllerComponents,
                                            authenticate: AuthPredicate,
                                            regStatusCheck: DeniedAccessPredicate,
                                            serviceErrorHandler: ServiceErrorHandler,
                                            answerService: DeregDateAnswerService,
                                            implicit val appConfig: AppConfig,
                                            implicit val ec: ExecutionContext) extends FrontendController(mcc) with I18nSupport with LoggerUtil {

  val show: Action[AnyContent] = (authenticate andThen regStatusCheck).async { implicit request =>
    answerService.getAnswer.map {
      case Right(Some(deregDate)) =>
        Ok(deregistrationDate(DeregistrationDateForm.form.fill(deregDate)))
      case Right(None) =>
        Ok(deregistrationDate(DeregistrationDateForm.form))
      case _ =>
        logger.warn("[DeregistrationDateController][show] - storedAnswerService returned an error retrieving answer")
        serviceErrorHandler.showInternalServerError
    }
  }

  val submit: Action[AnyContent] = (authenticate andThen regStatusCheck).async { implicit request =>
    DeregistrationDateForm.form.bindFromRequest().fold(
      error => Future(BadRequest(deregistrationDate(error))),
      data => answerService.storeAnswer(data) map {
        case Right(_) => Redirect(controllers.routes.CheckAnswersController.show())
        case Left(err) =>
          logger.warn("[DeregistrationDateController][submit] - storedAnswerService returned an error storing answers: " + err.message)
          serviceErrorHandler.showInternalServerError
      }
    )
  }
}