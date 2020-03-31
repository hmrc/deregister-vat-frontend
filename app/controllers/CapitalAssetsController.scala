/*
 * Copyright 2020 HM Revenue & Customs
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

import cats.data.EitherT
import cats.instances.future._
import javax.inject.{Inject, Singleton}
import config.{AppConfig, ServiceErrorHandler}
import controllers.predicates.{AuthPredicate, RegistrationStatusPredicate}
import forms.YesNoAmountForm
import models.{User, YesNoAmountModel}
import play.api.Logger
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import services.{CapitalAssetsAnswerService, WipeRedundantDataService}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController

import scala.concurrent.Future

@Singleton
class  CapitalAssetsController @Inject()(val messagesApi: MessagesApi,
                                         val authentication: AuthPredicate,
                                         val regStatusCheck: RegistrationStatusPredicate,
                                         val capitalAssetsAnswerService: CapitalAssetsAnswerService,
                                         val wipeRedundantDataService: WipeRedundantDataService,
                                         val serviceErrorHandler: ServiceErrorHandler,
                                         implicit val appConfig: AppConfig) extends FrontendController with I18nSupport {

  val form: Form[YesNoAmountModel] = YesNoAmountForm.yesNoAmountForm("capitalAssets.error.mandatoryRadioOption","capitalAssets.error.amount.noEntry")

  private def renderView(data: Form[YesNoAmountModel])(implicit user: User[_]) =
    views.html.capitalAssets(data)

  val show: Action[AnyContent] = (authentication andThen regStatusCheck).async { implicit user =>
    capitalAssetsAnswerService.getAnswer.map {
      case Right(Some(data)) => Ok(renderView(form.fill(data)))
      case _ => Ok(renderView(form))
    }
  }

  val submit: Action[AnyContent] = authentication.async { implicit user =>
    form.bindFromRequest().fold(
      error => Future.successful(BadRequest(renderView(error))),
      data => (for {
        _ <- EitherT(capitalAssetsAnswerService.storeAnswer(data))
        result <- EitherT(wipeRedundantDataService.wipeRedundantData)
      } yield result).value.map {
        case Right(_) => Redirect(controllers.routes.OptionStocksToSellController.show())
        case Left(error) =>
          Logger.warn("[CapitalAssetsController][submit] - storedAnswerService returned an error: " + error.message)
          serviceErrorHandler.showInternalServerError
      }
    )
  }
}
