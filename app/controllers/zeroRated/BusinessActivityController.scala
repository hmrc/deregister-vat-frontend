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

package controllers.zeroRated

import cats.data.EitherT
import cats.instances.future._
import config.{AppConfig, ServiceErrorHandler}
import controllers.predicates.{AuthPredicate, DeniedAccessPredicate}

import javax.inject.{Inject, Singleton}
import models.{No, User, Yes, YesNo}
import play.api.i18n.I18nSupport
import play.api.mvc._
import services._
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.BusinessActivity
import play.api.data.Form
import forms.YesNoForm
import utils.LoggerUtil.logWarn

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class BusinessActivityController @Inject()(businessActivity: BusinessActivity,
                                           val mcc: MessagesControllerComponents,
                                           val authenticate: AuthPredicate,
                                           val regStatusCheck: DeniedAccessPredicate,
                                           val businessActivityAnswerService: BusinessActivityAnswerService,
                                           val wipeRedundantDataService: WipeRedundantDataService,
                                           val serviceErrorHandler: ServiceErrorHandler,
                                           implicit val ec: ExecutionContext,
                                           implicit val appConfig: AppConfig) extends FrontendController(mcc) with I18nSupport {

  val form: Form[YesNo] = YesNoForm.yesNoForm("businessActivity.error.mandatoryRadioOption")

  private def renderView(form: Form[YesNo])(implicit user: User[_]) = businessActivity(form)

  private def redirect (yesNo: YesNo) : Result = yesNo match {
    case Yes => Redirect(controllers.zeroRated.routes.SicCodeController.show())
    case No => Redirect(controllers.routes.NextTaxableTurnoverController.show())
  }

  val show: Action[AnyContent] = (authenticate andThen regStatusCheck).async { implicit user =>
      businessActivityAnswerService.getAnswer map {
        case Right(Some(data)) => Ok(renderView(form.fill(data)))
        case _ => Ok(renderView(form))
      }
  }

  val submit: Action[AnyContent] = authenticate.async { implicit user =>
      form.bindFromRequest().fold(
        error => Future.successful(BadRequest(businessActivity(error))),
        data => (for {
          _ <- EitherT(businessActivityAnswerService.storeAnswer(data))
          _ <- EitherT(wipeRedundantDataService.wipeRedundantData)
          result = redirect(data)
        } yield result).value.map {
          case Right(redirect) => redirect
          case Left(error) =>
            logWarn("[BusinessActivityController][submit] - storedAnswerService returned an error: " + error.message)
            serviceErrorHandler.showInternalServerError
        }
      )
  }
}