/*
 * Copyright 2018 HM Revenue & Customs
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
import config.AppConfig
import controllers.predicates.AuthPredicate
import forms.YesNoForm
import javax.inject.Inject
import models._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, Result}
import services.{CapitalAssetsAnswerService, DeregReasonAnswerService, OutstandingInvoicesAnswerService, WipeRedundantDataService}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController

import scala.concurrent.Future

class OutstandingInvoicesController @Inject()(val messagesApi: MessagesApi,
                                              val authenticate: AuthPredicate,
                                              val outstandingInvoicesAnswerService: OutstandingInvoicesAnswerService,
                                              val deregReasonAnswerService: DeregReasonAnswerService,
                                              val capitalAssetsAnswerService: CapitalAssetsAnswerService,
                                              val wipeRedundantDataService: WipeRedundantDataService,
                                              implicit val appConfig: AppConfig) extends FrontendController with I18nSupport {

  val show: Action[AnyContent] = authenticate.async { implicit user =>
    outstandingInvoicesAnswerService.getAnswer map {
      case Right(Some(data)) => Ok(views.html.outstandingInvoices(YesNoForm.yesNoForm.fill(data)))
      case _ => Ok(views.html.outstandingInvoices(YesNoForm.yesNoForm))
    }
  }

  val submit: Action[AnyContent] = authenticate.async { implicit user =>
    YesNoForm.yesNoForm.bindFromRequest().fold(
      error => Future.successful(BadRequest(views.html.outstandingInvoices(error))),
      data => (for {
        _ <- EitherT(outstandingInvoicesAnswerService.storeAnswer(data))
        _ <- EitherT(wipeRedundantDataService.wipeRedundantData)
        capitalAssets <- EitherT(capitalAssetsAnswerService.getAnswer)
        deregReason <- EitherT(deregReasonAnswerService.getAnswer)
        result = redirect(data, capitalAssets, deregReason)
      } yield result).value.map {
        case Right(redirect) => redirect
        case Left(_) => InternalServerError
      }
    )
  }

  private def redirect(outstandingInvoices: YesNo, capitalAssets: Option[YesNoAmountModel], deregReason: Option[DeregistrationReason])
                      (implicit user: User[_]) = {
    if (outstandingInvoices == Yes) {
      Redirect(controllers.routes.DeregistrationDateController.show())
    } else {
      deregReason match {
        case Some(BelowThreshold) => Redirect(controllers.routes.DeregistrationDateController.show())
        case Some(Ceased) => ceasedTradingJourneyLogic(capitalAssets)
        case _ => InternalServerError
      }
    }
  }

  private def ceasedTradingJourneyLogic(capitalAssets: Option[YesNoAmountModel])(implicit user: User[_]): Result = {
    capitalAssets match {
      case Some(assets) if assets.yesNo == Yes => Redirect(controllers.routes.DeregistrationDateController.show())
      case Some(_) => Redirect(controllers.routes.CheckAnswersController.show())
      case _ => InternalServerError
    }
  }
}
