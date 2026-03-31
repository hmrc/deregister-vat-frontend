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
import config.{AppConfig, ServiceErrorHandler}
import controllers.predicates.{AuthPredicate, DeniedAccessPredicate}
import forms.{YesNoAmountForm, YesNoForm}
import models._
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc._
import services.{OptionTaxAnswerService, OptionTaxNewAnswerService, WipeRedundantDataService}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import utils.LoggingUtil
import views.html.{OptionTax, OptionTaxNew}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class OptionTaxController @Inject()(optionTax: OptionTax,
                                    optionTaxNew: OptionTaxNew,
                                    val mcc: MessagesControllerComponents,
                                    val authenticate: AuthPredicate,
                                    val regStatusCheck: DeniedAccessPredicate,
                                    val optionTaxAnswerService: OptionTaxAnswerService,
                                    val optionTaxNewAnswerService: OptionTaxNewAnswerService,
                                    val wipeRedundantDataService: WipeRedundantDataService,
                                    val serviceErrorHandler: ServiceErrorHandler,
                                    implicit val ec: ExecutionContext,
                                    implicit val appConfig: AppConfig) extends FrontendController(mcc) with I18nSupport with LoggingUtil{


  val form: Form[YesNoAmountModel] = YesNoAmountForm.yesNoAmountForm("optionTax.error.mandatoryRadioOption","optionTax.error.amount.noEntry")
  val ottForm: Form[YesNo] = YesNoForm.yesNoForm("optionTax.error.mandatoryRadioOption")

  private def renderView(form: Form[YesNoAmountModel])(implicit user: User[_]) =
    optionTax(form)

  private def renderNewView(form: Form[YesNo])(implicit  user:User[_]) =
    optionTaxNew(form)

  val show: Action[AnyContent] = (authenticate andThen regStatusCheck).async { implicit user =>
    if (appConfig.features.ottJourneyEnabled()) {
      optionTaxNewAnswerService.getAnswer map {
        case Right(Some(data)) => Ok(renderNewView(ottForm.fill(data)))
        case _ => Ok(renderNewView(ottForm))
      }
    } else {
      optionTaxAnswerService.getAnswer map {
        case Right(Some(data)) => Ok(renderView(form.fill(data)))
        case _ => Ok(renderView(form))
      }
    }
  }

  val submit: Action[AnyContent] = authenticate.async { implicit user =>
    if (appConfig.features.ottJourneyEnabled()) {
      ottForm.bindFromRequest().fold(
        error => Future.successful(BadRequest(optionTaxNew(error))),
        data => (for {
          _ <- EitherT(optionTaxNewAnswerService.storeAnswer(data))
          result = redirect(appConfig.features.ottJourneyEnabled(), data)
        } yield result).value.flatMap {
            case Right(redirect) => Future.successful(redirect)
            case Left(error) =>
              warnLog("[OptionTaxNewController][submit] - storedAnswerService returned an error storing answer: " + error.message)
              serviceErrorHandler.showInternalServerError
        }
      )
    } else {
      form.bindFromRequest().fold(
        error => Future.successful(BadRequest(optionTax(error))),
        data => (for {
          _ <- EitherT(optionTaxAnswerService.storeAnswer(data))
          result = redirect(ottFlag = false, data.yesNo)
        } yield result).value.flatMap{
          case Right(redirect) => Future.successful(redirect)
          case Left(error) =>
            warnLog("[OptionTaxController][submit] - storedAnswerService returned an error storing answer: " + error.message)
            serviceErrorHandler.showInternalServerError
        }
      )
    }
  }

  def redirect (ottFlag: Boolean, yesNo: YesNo) : Result = (ottFlag, yesNo) match {
    case (true, Yes) => Redirect(controllers.routes.OTTNotificationController.show)
    case (_, _) => Redirect(controllers.routes.CapitalAssetsController.show)
  }

}

