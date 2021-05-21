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
import javax.inject.{Inject, Singleton}
import models.{User, YesNo}
import play.api.data.Form
import forms.PurchasesExceedSuppliesForm
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc._
import services.PurchasesExceedSuppliesAnswerService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.PurchasesExceedSupplies

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PurchasesExceedSuppliesController @Inject()(purchasesExceedSupplies: PurchasesExceedSupplies,
                                                  val mcc: MessagesControllerComponents,
                                                  val authenticate: AuthPredicate,
                                                  val regStatusCheck: DeniedAccessPredicate,
                                                  val purchasesExceedSuppliesAnswerService: PurchasesExceedSuppliesAnswerService,
                                                  val serviceErrorHandler: ServiceErrorHandler,
                                                  implicit val ec: ExecutionContext,
                                                  implicit val appConfig: AppConfig) extends FrontendController(mcc)
                                                  with Logging with I18nSupport {

  private def renderView(form: Form[YesNo] = PurchasesExceedSuppliesForm.purchasesExceedSuppliesForm)
                        (implicit user: User[_]) = purchasesExceedSupplies(form)

  val show: Action[AnyContent] = (authenticate andThen regStatusCheck).async { implicit user =>
      for {
        pxs <- purchasesExceedSuppliesAnswerService.getAnswer
      } yield pxs match {
        case Right(Some(data)) => Ok(renderView(PurchasesExceedSuppliesForm.purchasesExceedSuppliesForm.fill(data)))
        case Right(None) => Ok(renderView())
        case Left(error) =>
          logger.warn("[PurchasesExceedSuppliesController][show] - storedAnswerService returned an error retrieving answer: " + error.message)

          serviceErrorHandler.showInternalServerError
      }
  }

  val submit: Action[AnyContent] = authenticate.async { implicit user =>
      PurchasesExceedSuppliesForm.purchasesExceedSuppliesForm.bindFromRequest().fold(
        error => Future.successful(BadRequest(purchasesExceedSupplies(error))),
        data => purchasesExceedSuppliesAnswerService.storeAnswer(data) map {
          case Right(_) => Redirect(controllers.routes.VATAccountsController.show().url)
          case Left(error) =>
            logger.warn("[PurchasesExceedSuppliesController][submit] - storedAnswerService returned an error storing answer: " + error.message)
            serviceErrorHandler.showInternalServerError
        }
      )
    }
}

