/*
 * Copyright 2024 HM Revenue & Customs
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
import services.{CapitalAssetsAnswerService, OptionTaxNewAnswerService, WipeRedundantDataService}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import utils.LoggingUtil
import views.html.CapitalAssets

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class  CapitalAssetsController @Inject()(capitalAssets: CapitalAssets,
                                         val mcc: MessagesControllerComponents,
                                         val authenticate: AuthPredicate,
                                         val regStatusCheck: DeniedAccessPredicate,
                                         val capitalAssetsAnswerService: CapitalAssetsAnswerService,
                                         val optionTaxNewAnswerService: OptionTaxNewAnswerService,
                                         val wipeRedundantDataService: WipeRedundantDataService,
                                         val serviceErrorHandler: ServiceErrorHandler,
                                         implicit val ec: ExecutionContext,
                                         implicit val appConfig: AppConfig) extends FrontendController(mcc) with I18nSupport with LoggingUtil {

  val form: Form[YesNoAmountModel] = YesNoAmountForm.yesNoAmountForm("capitalAssets.error.mandatoryRadioOption","capitalAssets.error.amount.noEntry")

  private def renderView(optTax: Boolean, data: Form[YesNoAmountModel])(implicit user: User[_]) = {
    capitalAssets(backLink(optTax), data)
  }

  private def backLink(flag: Boolean ): String = {
     (flag, appConfig.features.ottJourneyEnabled()) match{
       case (true, true) => controllers.routes.OptionTaxValueController.show.url
       case _ => controllers.routes.OptionTaxController.show.url
     }
  }

  val show: Action[AnyContent] = (authenticate andThen regStatusCheck).async { implicit user =>
    for {
      capitalData <- capitalAssetsAnswerService.getAnswer
      optTaxAnswer <- optionTaxNewAnswerService.getAnswer

      result <- (capitalData, optTaxAnswer) match {
        case (Right(Some(data)), Right(Some(optTax))) => Future.successful(
          Ok (renderView(optTax.value, form.fill(data))))
        case (Right(Some(data)), _) => Future.successful(
          Ok (renderView(false, form.fill(data))))
        case (_, Right(Some(optTax))) => Future.successful(
          Ok (renderView(optTax.value, form)))
        case _ => Future.successful(
          Ok(renderView(false, form)))
      }
    } yield result
  }

  val submit: Action[AnyContent] = authenticate.async { implicit user =>
    form.bindFromRequest().fold(
      error => (for {
                  optTaxAnswer <- EitherT(optionTaxNewAnswerService.getAnswer)
                } yield optTaxAnswer).value.flatMap {
                  case Right(Some(optTaxData)) if optTaxData.value => Future.successful(BadRequest(renderView(true, error)))
                  case _ => Future.successful(BadRequest(renderView(false, error)))
               },
      data => (for {
        _ <- EitherT(capitalAssetsAnswerService.storeAnswer(data))
        result <- EitherT(wipeRedundantDataService.wipeRedundantData)
      } yield result).value.flatMap {
        case Right(_) =>
          Future.successful(
            Redirect(controllers.routes.OptionStocksToSellController.show))
        case Left(error) =>
          warnLog("[CapitalAssetsController][submit] - storedAnswerService returned an error: " + error.message)
          serviceErrorHandler.showInternalServerError
      }
    )
  }

}
