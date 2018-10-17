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

import config.{AppConfig, ServiceErrorHandler}
import controllers.predicates.AuthPredicate
import forms.YesNoAmountForm
import javax.inject.{Inject, Singleton}
import models.{User, YesNoAmountModel}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import services.OptionTaxAnswerService
import uk.gov.hmrc.play.bootstrap.controller.FrontendController

import scala.concurrent.Future

@Singleton
class OptionTaxController @Inject()(val messagesApi: MessagesApi,
                                    val authenticate: AuthPredicate,
                                    val optionTaxAnswerService: OptionTaxAnswerService,
                                    val serviceErrorHandler: ServiceErrorHandler,
                                    implicit val appConfig: AppConfig) extends FrontendController with I18nSupport {

  private def renderView(form: Form[YesNoAmountModel] = YesNoAmountForm.yesNoAmountForm)(implicit user: User[_]) =
    views.html.optionTax(form)

  val show: Action[AnyContent] = authenticate.async { implicit user =>
    optionTaxAnswerService.getAnswer map {
      case Right(Some(data)) => Ok(renderView(YesNoAmountForm.yesNoAmountForm.fill(data)))
      case _ => Ok(renderView())
    }
  }

  val submit: Action[AnyContent] = authenticate.async { implicit user =>
    YesNoAmountForm.yesNoAmountForm.bindFromRequest().fold(
      error => Future.successful(BadRequest(views.html.optionTax(error))),
      data => optionTaxAnswerService.storeAnswer(data) map {
        case Right(_) => Redirect(controllers.routes.OptionStocksToSellController.show())
        case _ => serviceErrorHandler.showInternalServerError
      }
    )
  }
}
