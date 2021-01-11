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
import forms.YesNoAmountForm
import javax.inject.Inject
import models.{User, YesNoAmountModel}
import play.api.Logger
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.StocksAnswerService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.OptionStocksToSell

import scala.concurrent.{ExecutionContext, Future}

class OptionStocksToSellController @Inject()(optionStocksToSell: OptionStocksToSell,
                                             val mcc: MessagesControllerComponents,
                                             val authenticate: AuthPredicate,
                                             val regStatusCheck: DeniedAccessPredicate,
                                             val stocksAnswerService: StocksAnswerService,
                                             val serviceErrorHandler: ServiceErrorHandler,
                                             implicit val ec: ExecutionContext,
                                             implicit val appConfig: AppConfig) extends FrontendController(mcc) with I18nSupport {

  val form: Form[YesNoAmountModel] = YesNoAmountForm.yesNoAmountForm(
    "optionOwnsStockToSell.error.mandatoryRadioOption","optionOwnsStockToSell.error.amount.noEntry")

  private def renderView(form: Form[YesNoAmountModel])(implicit user: User[_]) =
    optionStocksToSell(form)

  val show: Action[AnyContent] = (authenticate andThen regStatusCheck).async { implicit user =>
    stocksAnswerService.getAnswer map {
      case Right(Some(data)) => Ok(renderView(form.fill(data)))
      case _ => Ok(renderView(form))
    }
  }

  val submit: Action[AnyContent] = authenticate.async { implicit user =>
    form.bindFromRequest().fold(
      error => Future.successful(BadRequest(optionStocksToSell(error))),
      data => stocksAnswerService.storeAnswer(data) map {
        case Right(_) => Redirect(controllers.routes.IssueNewInvoicesController.show())
        case Left(error) =>
          Logger.warn("[OptionStocksToSellController][submit] - storedAnswerService returned an error storing answer: " + error.message)
          serviceErrorHandler.showInternalServerError
      }
    )
  }
}
