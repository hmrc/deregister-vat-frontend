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

package controllers.zeroRated

import config.{AppConfig, ServiceErrorHandler}
import controllers.predicates.{AuthPredicate, DeniedAccessPredicate}
import forms.ZeroRatedSuppliesForm
import javax.inject.{Inject, Singleton}
import models.{NumberInputModel, User}
import play.api.Logger
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc._
import services.ZeroRatedSuppliesValueService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.ZeroRatedSupplies

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ZeroRatedSuppliesController @Inject()(zeroRatedSupplies: ZeroRatedSupplies,
                                            val mcc: MessagesControllerComponents,
                                            val authenticate: AuthPredicate,
                                            val regStatusCheck: DeniedAccessPredicate,
                                            val zeroRatedSuppliesValueService: ZeroRatedSuppliesValueService,
                                            val serviceErrorHandler: ServiceErrorHandler,
                                            implicit val appConfig: AppConfig,
                                            implicit val ec: ExecutionContext) extends FrontendController(mcc) with I18nSupport {

  private def renderView(form: Form[NumberInputModel] = ZeroRatedSuppliesForm.zeroRatedSuppliesForm)(implicit user: User[_]) =
    zeroRatedSupplies(form)

  val show: Action[AnyContent] = (authenticate andThen regStatusCheck).async { implicit user =>
    if (appConfig.features.zeroRatedJourney()) {
      zeroRatedSuppliesValueService.getAnswer map {
        case Right(Some(data)) => Ok(renderView(ZeroRatedSuppliesForm.zeroRatedSuppliesForm.fill(data)))
        case _ => Ok(renderView())
      }
    } else{
      Future(serviceErrorHandler.showBadRequestError)
    }
  }

  val submit: Action[AnyContent] = authenticate.async { implicit user =>
    if (appConfig.features.zeroRatedJourney()) {
      ZeroRatedSuppliesForm.zeroRatedSuppliesForm.bindFromRequest().fold(
        error => Future.successful(BadRequest(zeroRatedSupplies(error))),
        data => zeroRatedSuppliesValueService.storeAnswer(data) map {
          case Right(_) => Redirect(controllers.zeroRated.routes.PurchasesExceedSuppliesController.show())
          case Left(error) =>
            Logger.warn("[ZeroRatedSuppliesController][submit] - storedAnswerService returned an error storing answer: " + error.message)
            serviceErrorHandler.showInternalServerError
        }
      )
    }  else {
      Future(serviceErrorHandler.showBadRequestError)
    }
  }
}

