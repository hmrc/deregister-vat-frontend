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
import controllers.predicates.AuthPredicate
import forms.DeregistrationReasonForm
import models.{BelowThreshold, Ceased, Other}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import uk.gov.hmrc.play.bootstrap.controller.FrontendController

import scala.concurrent.Future

@Singleton
class DeregistrationReasonController @Inject()(val messagesApi: MessagesApi,
                                               val authenticate: AuthPredicate,
                                               implicit val appConfig: AppConfig) extends FrontendController with I18nSupport {

  val show: Action[AnyContent] = authenticate.async { implicit user =>
    Future.successful(Ok(views.html.deregistrationReason(DeregistrationReasonForm.deregistrationReasonForm)))
  }

  val submit: Action[AnyContent] = authenticate.async { implicit user =>

    DeregistrationReasonForm.deregistrationReasonForm.bindFromRequest().fold(
      error => Future.successful(BadRequest(views.html.deregistrationReason(error))),
      {
        case Ceased => Future.successful(Redirect(controllers.routes.CeasedTradingDateController.show()))
        case BelowThreshold => Future.successful(Redirect(controllers.routes.TaxableTurnoverController.show()))
        case Other => Future.successful(Redirect(appConfig.govUkCancelVatRegistration))
      }
    )
  }
}
