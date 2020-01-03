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
import controllers.predicates.{AuthPredicate, PendingChangesPredicate}
import forms.ZeroRatedSuppliesForm
import javax.inject.{Inject, Singleton}
import models.{NumberInputModel, User}
import play.api.Logger
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import services.ZeroRatedSuppliesValueService
import uk.gov.hmrc.play.bootstrap.controller.FrontendController

import scala.concurrent.Future

@Singleton
class ZeroRatedSuppliesController @Inject()(val messagesApi: MessagesApi,
                                            val authenticate: AuthPredicate,
                                            val pendingDeregCheck: PendingChangesPredicate,
                                            val zeroRatedSuppliesValueService: ZeroRatedSuppliesValueService,
                                            val serviceErrorHandler: ServiceErrorHandler,
                                            implicit val appConfig: AppConfig) extends FrontendController with I18nSupport {

  private def renderView(form: Form[NumberInputModel] = ZeroRatedSuppliesForm.zeroRatedSuppliesForm)(implicit user: User[_]) =
    views.html.zeroRatedSupplies(form)

  val show: Action[AnyContent] = (authenticate andThen pendingDeregCheck).async { implicit user =>
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
        error => Future.successful(BadRequest(views.html.zeroRatedSupplies(error))),
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

