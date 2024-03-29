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

package controllers.zeroRated

import config.{AppConfig, ServiceErrorHandler}
import controllers.predicates.{AuthPredicate, DeniedAccessPredicate}
import forms.PurchasesExceedSuppliesForm
import models.{User, YesNo}
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc._
import services.{PurchasesExceedSuppliesAnswerService, ThresholdService}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.PurchasesExceedSupplies

import javax.inject.{Inject, Singleton}
import utils.LoggingUtil

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PurchasesExceedSuppliesController @Inject()(purchasesExceedSupplies: PurchasesExceedSupplies,
                                                  val mcc: MessagesControllerComponents,
                                                  val authenticate: AuthPredicate,
                                                  val regStatusCheck: DeniedAccessPredicate,
                                                  val purchasesExceedSuppliesAnswerService: PurchasesExceedSuppliesAnswerService,
                                                  val thresholdService: ThresholdService,
                                                  val serviceErrorHandler: ServiceErrorHandler,
                                                  implicit val ec: ExecutionContext,
                                                  implicit val appConfig: AppConfig) extends FrontendController(mcc) with I18nSupport with LoggingUtil{

  private def renderView(form: Form[YesNo] = PurchasesExceedSuppliesForm.purchasesExceedSuppliesForm, vatThreshold: String)
                        (implicit user: User[_]) = purchasesExceedSupplies(form, vatThreshold)

  val show: Action[AnyContent] = (authenticate andThen regStatusCheck).async { implicit user =>
      for {
        pxs <- purchasesExceedSuppliesAnswerService.getAnswer
      } yield pxs match {
        case Right(Some(data)) => Ok(renderView(PurchasesExceedSuppliesForm.purchasesExceedSuppliesForm.fill(data), thresholdService.formattedVatThreshold()))
        case Right(None) => Ok(renderView(vatThreshold = thresholdService.formattedVatThreshold()))
        case Left(error) =>
          warnLog("[PurchasesExceedSuppliesController][show] - storedAnswerService returned an error retrieving answer: " + error.message)

          serviceErrorHandler.showInternalServerError
      }
  }

  val submit: Action[AnyContent] = authenticate.async { implicit user =>
      PurchasesExceedSuppliesForm.purchasesExceedSuppliesForm.bindFromRequest().fold(
        error => Future.successful(BadRequest(purchasesExceedSupplies(error, thresholdService.formattedVatThreshold()))),
        data => purchasesExceedSuppliesAnswerService.storeAnswer(data) map {
          case Right(_) => Redirect(controllers.routes.VATAccountsController.show.url)
          case Left(error) =>
            warnLog("[PurchasesExceedSuppliesController][submit] - storedAnswerService returned an error storing answer: " + error.message)
            serviceErrorHandler.showInternalServerError
        }
      )
    }
}
