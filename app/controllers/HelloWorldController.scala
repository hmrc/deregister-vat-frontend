/*
 * Copyright 2017 HM Revenue & Customs
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

import javax.inject.{Inject, Singleton}

import config.AppConfig
import config.features.SimpleAuthFeature
import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import uk.gov.hmrc.auth.core.retrieve.Retrievals
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.play.bootstrap.controller.FrontendController

import scala.concurrent.Future

@Singleton
class HelloWorldController @Inject()(val messagesApi: MessagesApi,
                                     val auth: AuthorisedFunctions,
                                     val simpleAuthFeature: SimpleAuthFeature,
                                     implicit val appConfig: AppConfig)
  extends FrontendController with I18nSupport {

  def helloWorld(): Action[AnyContent] = authorisedAction { implicit request => vrn =>
      Logger.warn(s"THE USERS VRN IS: $vrn")
      Future.successful(Ok(views.html.helloworld.hello_world()))
  }

  // TODO: Move into either a base controller (AuthorisedController?) or trait so it can be used by other controllers
  private def authorisedAction(block: Request[AnyContent] => String => Future[Result]): Action[AnyContent] = Action.async { implicit request =>
    auth.authorised(Enrolment("HMRC-MTD-VAT")).retrieve(Retrievals.authorisedEnrolments) {
      enrolments => {
        val vrn = enrolments.enrolments.collectFirst {
          case Enrolment("HMRC-MTD-VAT", EnrolmentIdentifier(_, value) :: _, _, _) => value
        }.getOrElse("")

        block(request)(vrn)
      }
    } recoverWith {
      case _: NoActiveSession => Future.successful(Redirect(controllers.routes.ErrorsController.sessionTimeout()))
      case _: AuthorisationException => Future.successful(Redirect(controllers.routes.ErrorsController.unauthorised()))
    }
  }

}
