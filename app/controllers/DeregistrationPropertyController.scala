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

import javax.inject.{Inject, Singleton}

import config.AppConfig
import forms.YesNoForm
import models.{No, Yes}
import play.api.i18n.MessagesApi
import play.api.mvc._
import uk.gov.hmrc.auth.core._

import scala.concurrent.Future

@Singleton
class DeregistrationPropertyController @Inject()(val messagesApi: MessagesApi,
                                                 val auth: AuthorisedFunctions,
                                                 override implicit val appConfig: AppConfig) extends AuthorisedController {

  val show: Action[AnyContent] = authorisedAction { implicit user =>
    Future.successful(Ok(views.html.deregistrationProperty(YesNoForm.yesNoForm)))
  }

  val submit: Action[AnyContent] = authorisedAction { implicit user =>

    YesNoForm.yesNoForm.bindFromRequest().fold(
      error => Future.successful(BadRequest(views.html.deregistrationProperty(error))),
      {
        case Yes => Future.successful(Redirect(controllers.routes.HelloWorldController.helloWorld()))
        case No => Future.successful(Redirect(controllers.routes.HelloWorldController.helloWorld()))
      }
    )
  }
}
