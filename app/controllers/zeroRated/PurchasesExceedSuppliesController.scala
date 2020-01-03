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
import javax.inject.{Inject, Singleton}
import models.{User, YesNo}
import play.api.data.Form
import forms.PurchasesExceedSuppliesForm
import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import services.PurchasesExceedSuppliesAnswerService
import uk.gov.hmrc.play.bootstrap.controller.FrontendController

import scala.concurrent.Future

@Singleton
class PurchasesExceedSuppliesController @Inject()(val messagesApi: MessagesApi,
                                                  val authenticate: AuthPredicate,
                                                  val pendingDeregCheck: PendingChangesPredicate,
                                                  val purchasesExceedSuppliesAnswerService: PurchasesExceedSuppliesAnswerService,
                                                  val serviceErrorHandler: ServiceErrorHandler,
                                                  implicit val appConfig: AppConfig) extends FrontendController with I18nSupport {

  private def renderView(form: Form[YesNo] = PurchasesExceedSuppliesForm.purchasesExceedSuppliesForm)
                        (implicit user: User[_]) = views.html.purchasesExceedSupplies(form)

  val show: Action[AnyContent] = (authenticate andThen pendingDeregCheck).async { implicit user =>
    if (appConfig.features.zeroRatedJourney()) {
      for {
        pxs <- purchasesExceedSuppliesAnswerService.getAnswer
      } yield pxs match {
        case Right(Some(data)) => Ok(renderView(PurchasesExceedSuppliesForm.purchasesExceedSuppliesForm.fill(data)))
        case Right(None) => Ok(renderView())
        case Left(error) =>
          Logger.warn("[PurchasesExceedSuppliesController][show] - storedAnswerService returned an error retrieving answer: " + error.message)

          serviceErrorHandler.showInternalServerError
      }
    } else {
      Future(serviceErrorHandler.showBadRequestError)
    }
  }

  val submit: Action[AnyContent] = authenticate.async { implicit user =>
    if (appConfig.features.zeroRatedJourney()) {
      PurchasesExceedSuppliesForm.purchasesExceedSuppliesForm.bindFromRequest().fold(
        error => Future.successful(BadRequest(views.html.purchasesExceedSupplies(error))),
        data => purchasesExceedSuppliesAnswerService.storeAnswer(data) map {
          case Right(_) => Redirect(controllers.routes.VATAccountsController.show().url)
          case Left(error) =>
            Logger.warn("[PurchasesExceedSuppliesController][submit] - storedAnswerService returned an error storing answer: " + error.message)
            serviceErrorHandler.showInternalServerError
        }
      )
    } else {
      Future.successful(serviceErrorHandler.showBadRequestError)
    }
  }
}

