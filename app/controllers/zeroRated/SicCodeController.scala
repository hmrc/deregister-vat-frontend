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

import config.{AppConfig, ServiceErrorHandler}
import controllers.predicates.{AuthPredicate, DeniedAccessPredicate}
import forms.SicCodeForm
import javax.inject.{Inject, Singleton}
import models._
import play.api.Logger
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc._
import services.{BusinessActivityAnswerService, SicCodeAnswerService}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.SicCode

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SicCodeController @Inject()(sicCode: SicCode,
                                  val mcc: MessagesControllerComponents,
                                  val authenticate: AuthPredicate,
                                  val regStatusCheck: DeniedAccessPredicate,
                                  val serviceErrorHandler: ServiceErrorHandler,
                                  val businessActivityAnswerService: BusinessActivityAnswerService,
                                  val sicCodeAnswerService: SicCodeAnswerService,
                                  implicit val ec: ExecutionContext,
                                  implicit val appConfig: AppConfig) extends FrontendController(mcc) with I18nSupport {

  private def renderView(form: Form[String] = SicCodeForm.sicCodeForm)(implicit user: User[_]) = sicCode(form)

  val show: Action[AnyContent] = (authenticate andThen regStatusCheck).async { implicit user =>
    if (appConfig.features.zeroRatedJourney()) {
      for {
        ba <- businessActivityAnswerService.getAnswer
        sc <- sicCodeAnswerService.getAnswer
      } yield (ba, sc) match {
        case (Right(Some(Yes)), Right(Some(sicCode))) => Ok(renderView(SicCodeForm.sicCodeForm.fill(sicCode)))
        case (Right(Some(Yes)), Right(_)) => Ok(renderView())
        case (Right(Some(No)), Right(_)) => Redirect(controllers.routes.NextTaxableTurnoverController.show())
        case (Right(None), Right(_)) => Redirect(controllers.zeroRated.routes.BusinessActivityController.show())
        case _ =>
          Logger.warn("[SicCodeController][show] - storedAnswerService returned an error retrieving answers")
          serviceErrorHandler.showInternalServerError
      }
    } else {
      Future.successful(serviceErrorHandler.showBadRequestError)
    }
  }

  val submit: Action[AnyContent] = authenticate.async { implicit user =>
    if (appConfig.features.zeroRatedJourney()) {
      SicCodeForm.sicCodeForm.bindFromRequest().fold(
        error => Future.successful(BadRequest(sicCode(error))),
        data => sicCodeAnswerService.storeAnswer(data) map {
          case Right(_) => Redirect(controllers.routes.NextTaxableTurnoverController.show())
          case Left(error) =>
            Logger.warn("[SicCodeController][submit] - storedAnswerService returned an error storing answer: " + error.message)
            serviceErrorHandler.showInternalServerError
        }
      )
    } else {
      Future.successful(serviceErrorHandler.showBadRequestError)
    }
  }
}

