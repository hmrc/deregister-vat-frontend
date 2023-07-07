/*
 * Copyright 2023 HM Revenue & Customs
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
import forms.YesNoForm
import models._
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.{CapitalAssetsAnswerService, DeregReasonAnswerService, OutstandingInvoicesAnswerService, WipeRedundantDataService}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.OutstandingInvoices
import javax.inject.Inject
import utils.LoggingUtil

import scala.concurrent.{ExecutionContext, Future}

class OutstandingInvoicesController @Inject()(outstandingInvoices: OutstandingInvoices,
                                              val mcc: MessagesControllerComponents,
                                              val authenticate: AuthPredicate,
                                              val regStatusCheck: DeniedAccessPredicate,
                                              val outstandingInvoicesAnswerService: OutstandingInvoicesAnswerService,
                                              val deregReasonAnswerService: DeregReasonAnswerService,
                                              val capitalAssetsAnswerService: CapitalAssetsAnswerService,
                                              val wipeRedundantDataService: WipeRedundantDataService,
                                              val serviceErrorHandler: ServiceErrorHandler,
                                              implicit val ec: ExecutionContext,
                                              implicit val appConfig: AppConfig) extends FrontendController(mcc) with I18nSupport with LoggingUtil {

  val form: Form[YesNo] = YesNoForm.yesNoForm("outstandingInvoice.error.mandatoryRadioOption")

  val show: Action[AnyContent] = (authenticate andThen regStatusCheck).async { implicit user =>
    outstandingInvoicesAnswerService.getAnswer map {
      case Right(Some(data)) => Ok(outstandingInvoices(form.fill(data)))
      case _ => Ok(outstandingInvoices(form))
    }
  }

  val submit: Action[AnyContent] = authenticate.async { implicit user =>
    form.bindFromRequest().fold(
      error => Future.successful(BadRequest(outstandingInvoices(error))),
      data => (for {
        _ <- EitherT(outstandingInvoicesAnswerService.storeAnswer(data))
        _ <- EitherT(wipeRedundantDataService.wipeRedundantData)
        capitalAssets <- EitherT(capitalAssetsAnswerService.getAnswer)
        deregReason <- EitherT(deregReasonAnswerService.getAnswer)
        result = redirect(data, capitalAssets, deregReason)
      } yield result).value.map {
        case Right(redirect) => redirect
        case Left(error) =>
          warnLog("[OutstandingInvoicesController][submit] - failed to retrieve one or more answers from answer service: " + error.message)
          serviceErrorHandler.showInternalServerError
      }
    )
  }

  private def redirect(outstandingInvoices: YesNo, capitalAssets: Option[YesNoAmountModel], deregReason: Option[DeregistrationReason])
                      (implicit user: User[_]) = {
    if (outstandingInvoices == Yes) {
      Redirect(controllers.routes.ChooseDeregistrationDateController.show)
    } else {
      deregReason match {
        case Some(BelowThreshold) => Redirect(controllers.routes.ChooseDeregistrationDateController.show)
        case Some(ZeroRated) => Redirect(controllers.routes.ChooseDeregistrationDateController.show)
        case Some(ExemptOnly) => Redirect(controllers.routes.ChooseDeregistrationDateController.show)
        case Some(Ceased) => ceasedTradingJourneyLogic(capitalAssets)
        case _ =>
          warnLog("[OutstandingInvoicesController][redirect] - answer for deregReason doesn't exist or wasn't expected")
          serviceErrorHandler.showInternalServerError
      }
    }
  }

  private def ceasedTradingJourneyLogic(capitalAssets: Option[YesNoAmountModel])(implicit user: User[_]): Result = {
    capitalAssets match {
      case Some(assets) if assets.yesNo == Yes => Redirect(controllers.routes.ChooseDeregistrationDateController.show)
      case Some(_) => Redirect(controllers.routes.CheckAnswersController.show)
      case _ =>
        warnLog("[OutstandingInvoicesController][ceasedTradingJourneyLogic] - answer for capitalAssets doesn't exist or wasn't expected")
        serviceErrorHandler.showInternalServerError
    }
  }
}
