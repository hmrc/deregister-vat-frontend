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
import forms.TaxableTurnoverForm
import javax.inject.{Inject, Singleton}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController

import scala.concurrent.Future

@Singleton
class NextTaxableTurnoverController @Inject()(val messagesApi: MessagesApi,
                                          val authenticate: AuthPredicate,
                                          implicit val appConfig: AppConfig) extends FrontendController with I18nSupport {

  val show: Action[AnyContent] = authenticate.async { implicit user =>
    Future.successful(Ok(views.html.nextTaxableTurnover(TaxableTurnoverForm.taxableTurnoverForm)))
  }

  val submit: Action[AnyContent] = authenticate.async { implicit user =>

    TaxableTurnoverForm.taxableTurnoverForm.bindFromRequest().fold(
      error => Future.successful(BadRequest(views.html.nextTaxableTurnover(error))),
      _ => Future.successful(Redirect(controllers.routes.HelloWorldController.helloWorld()))
    )
  }

}
