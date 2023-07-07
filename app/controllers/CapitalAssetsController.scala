/*
 * Copyright 2023 HM Revenue & Customs
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
import config.{AppConfig, ServiceErrorHandler}
import controllers.predicates.{AuthPredicate, DeniedAccessPredicate}
import forms.YesNoAmountForm
import models.{User, YesNoAmountModel}
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc._
import services.{CapitalAssetsAnswerService, WipeRedundantDataService}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import utils.LoggingUtil
import views.html.CapitalAssets

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class  CapitalAssetsController @Inject()(capitalAssets: CapitalAssets,
                                          val mcc: MessagesControllerComponents,
                                         val authentication: AuthPredicate,
                                         val regStatusCheck: DeniedAccessPredicate,
                                         val capitalAssetsAnswerService: CapitalAssetsAnswerService,
                                         val wipeRedundantDataService: WipeRedundantDataService,
                                         val serviceErrorHandler: ServiceErrorHandler,
                                         implicit val ec: ExecutionContext,
                                         implicit val appConfig: AppConfig) extends FrontendController(mcc) with I18nSupport with LoggingUtil {

  val form: Form[YesNoAmountModel] = YesNoAmountForm.yesNoAmountForm("capitalAssets.error.mandatoryRadioOption","capitalAssets.error.amount.noEntry")

  private def renderView(data: Form[YesNoAmountModel])(implicit user: User[_]) =
    capitalAssets(data)

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
        case Right(_) => Redirect(controllers.routes.OptionStocksToSellController.show)
        case Left(error) =>
          warnLog("[CapitalAssetsController][submit] - storedAnswerService returned an error: " + error.message)
          serviceErrorHandler.showInternalServerError
      }
    )
  }
}
