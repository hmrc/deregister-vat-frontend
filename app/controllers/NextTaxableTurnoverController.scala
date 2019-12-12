/*
 * Copyright 2019 HM Revenue & Customs
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
import controllers.predicates.{AuthPredicate, PendingChangesPredicate}
import forms.NextTaxableTurnoverForm
import javax.inject.{Inject, Singleton}
import models.{BelowThreshold, NumberInputModel, No, User, Yes, YesNo, ZeroRated}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent}
import services.{BusinessActivityAnswerService, DeregReasonAnswerService, NextTaxableTurnoverAnswerService, TaxableTurnoverAnswerService}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import services.DeregReasonAnswerService

import scala.concurrent.Future

@Singleton
class NextTaxableTurnoverController @Inject()(val messagesApi: MessagesApi,
                                              val authenticate: AuthPredicate,
                                              val pendingDeregCheck: PendingChangesPredicate,
                                              val taxableTurnoverAnswerService: TaxableTurnoverAnswerService,
                                              val businessActivityAnswerService: BusinessActivityAnswerService,
                                              val deregReasonAnswerService: DeregReasonAnswerService,
                                              val nextTaxableTurnoverAnswerService: NextTaxableTurnoverAnswerService,
                                              val serviceErrorHandler: ServiceErrorHandler,
                                              implicit val appConfig: AppConfig) extends FrontendController with I18nSupport {

  def backLink(businessActivityAnswerService: Option[YesNo]): String = {
    businessActivityAnswerService match {
      case Some(Yes) => controllers.zeroRated.routes.SicCodeController.show().url
      case Some(No) => controllers.zeroRated.routes.BusinessActivityController.show().url
      case _ => controllers.routes.TaxableTurnoverController.show().url
    }
  }

  private def renderView(form: Form[NumberInputModel] = NextTaxableTurnoverForm.taxableTurnoverForm, backLink: String)(implicit user: User[_]) =
    views.html.nextTaxableTurnover(form, backLink)

  val show: Action[AnyContent] = (authenticate andThen pendingDeregCheck).async { implicit user =>
    for {
      data <- nextTaxableTurnoverAnswerService.getAnswer
      businessActivity <- businessActivityAnswerService.getAnswer
      deregReason <- deregReasonAnswerService.getAnswer
    } yield (data, businessActivity, deregReason) match {
      case (Right(Some(data)), Right(Some(businessActivity)), _) =>
        Ok(renderView(NextTaxableTurnoverForm.taxableTurnoverForm.fill(data), backLink(Some(businessActivity))))
      case (Right(Some(data)), _, Right(Some(BelowThreshold))) =>
        Ok(renderView(NextTaxableTurnoverForm.taxableTurnoverForm.fill(data), controllers.routes.TaxableTurnoverController.show().url))
      case (_, Right(Some(businessActivity)), Right(Some(ZeroRated))) =>
        Ok(renderView(NextTaxableTurnoverForm.taxableTurnoverForm, backLink(Some(businessActivity))))
      case (_, _, Right(Some(BelowThreshold))) =>
        Ok(renderView(NextTaxableTurnoverForm.taxableTurnoverForm, controllers.routes.TaxableTurnoverController.show().url))
      case _ => serviceErrorHandler.showInternalServerError
    }
  }

  val submit: Action[AnyContent] = authenticate.async { implicit user =>
    NextTaxableTurnoverForm.taxableTurnoverForm.bindFromRequest().fold(
      error => for {
        businessActivity <- businessActivityAnswerService.getAnswer
        deregReason <- deregReasonAnswerService.getAnswer
      } yield (businessActivity, deregReason) match {
        case (Right(Some(businessActivity)), _) =>
          BadRequest(renderView(error, backLink(Some(businessActivity))))
        case (_, Right(Some(BelowThreshold))) =>
          BadRequest(renderView(error, controllers.routes.TaxableTurnoverController.show().url))
        case _ => serviceErrorHandler.showInternalServerError
      },
      data => (for {
        _ <- EitherT(nextTaxableTurnoverAnswerService.storeAnswer(data))
        taxableTurnover <- EitherT(taxableTurnoverAnswerService.getAnswer)
        deregReason <- EitherT(deregReasonAnswerService.getAnswer)
      } yield (taxableTurnover, deregReason))
        .value.map {
        case Right((_ ,Some(ZeroRated))) => Redirect(controllers.zeroRated.routes.ZeroRatedSuppliesController.show())
        case Right((Some(_), Some(_))) if data.value > appConfig.deregThreshold => Redirect(controllers.routes.CannotDeregisterThresholdController.show())
        case Right((Some(Yes), Some(_))) => Redirect(controllers.routes.VATAccountsController.show())
        case Right((Some(No), Some(_))) => Redirect(controllers.routes.WhyTurnoverBelowController.show())
        case _ => serviceErrorHandler.showInternalServerError
      }
    )
  }

}
