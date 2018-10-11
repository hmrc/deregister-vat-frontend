/*
 * Copyright 2018 HM Revenue & Customs
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

import config.AppConfig
import controllers.predicates.AuthPredicate
import javax.inject.{Inject, Singleton}
import models.VatSubscriptionSuccess
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import services._
import uk.gov.hmrc.play.bootstrap.controller.FrontendController

@Singleton
class CheckAnswersController @Inject()(val messagesApi: MessagesApi,
                                       val authenticate: AuthPredicate,
                                       checkAnswersService: CheckAnswersService,
                                       deregDateAnswerService: DeregDateAnswerService,
                                       updateDeregistrationService: UpdateDeregistrationService,
                                       implicit val appConfig: AppConfig) extends FrontendController with I18nSupport {

  val show: Action[AnyContent] = authenticate.async { implicit user =>
    checkAnswersService.checkYourAnswersModel() map {
      case Right(answers) =>
        answers.deregDate match {
          case Some(_) => Ok(views.html.checkYourAnswers(controllers.routes.DeregistrationDateController.show().url, answers.seqAnswers))
          case None => Ok(views.html.checkYourAnswers(controllers.routes.OutstandingInvoicesController.show().url, answers.seqAnswers))
        }
      case Left(_) => InternalServerError
    }
  }

  val submit: Action[AnyContent] = authenticate.async { implicit user =>
    updateDeregistrationService.updateDereg.map {
      case Right(VatSubscriptionSuccess) => Redirect(controllers.routes.DeregistrationConfirmationController.show().url)
      case _ => InternalServerError
    }
  }
}