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
import models.User
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import uk.gov.hmrc.auth.core.retrieve.Retrievals
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.play.bootstrap.controller.FrontendController

import scala.concurrent.Future

@Singleton
class AuthorisedController @Inject()(val messagesApi: MessagesApi,
                                     auth: AuthorisedFunctions,
                                     implicit val appConfig: AppConfig)
  extends FrontendController with I18nSupport {

  private val VAT_ENROLMENT_ID = "HMRC-MTD-VAT"

  def authorisedAction(block: Request[AnyContent] => User => Future[Result]): Action[AnyContent] = Action.async { implicit request =>
    auth.authorised(Enrolment(VAT_ENROLMENT_ID)).retrieve(Retrievals.authorisedEnrolments) {
      enrolments => {
        val vrn = enrolments.enrolments.collectFirst {
          case Enrolment(VAT_ENROLMENT_ID, EnrolmentIdentifier(_, vatId) :: _, _, _) => vatId
        }.getOrElse(throw InternalError("VRN Missing"))

        block(request)(User(vrn))
      }
    } recoverWith {
      case _: NoActiveSession => Future.successful(Redirect(controllers.routes.ErrorsController.sessionTimeout()))
      case _: AuthorisationException => Future.successful(Redirect(controllers.routes.ErrorsController.unauthorised()))
    }
  }

}
