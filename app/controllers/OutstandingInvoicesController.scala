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

import config.AppConfig
import controllers.predicates.AuthPredicate
import forms.YesNoForm
import javax.inject.Inject
import models._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, Result}
import services.{CapitalAssetsAnswerService, DeregReasonAnswerService, OutstandingInvoicesAnswerService}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController

import scala.concurrent.Future

class OutstandingInvoicesController @Inject()(val messagesApi: MessagesApi,
                                              val authenticate: AuthPredicate,
                                              val outstandingInvoicesAnswerService: OutstandingInvoicesAnswerService,
                                              val deregReasonAnswerService: DeregReasonAnswerService,
                                              val capitalAssetsAnswerService: CapitalAssetsAnswerService,
                                              implicit val appConfig: AppConfig) extends FrontendController with I18nSupport {

  val show: Action[AnyContent] = authenticate.async { implicit user =>
    outstandingInvoicesAnswerService.getAnswer map {
      case Right(Some(data)) => Ok(views.html.optionTax(YesNoForm.yesNoForm.fill(data)))
      case _ => Ok(views.html.optionTax(YesNoForm.yesNoForm))
    }
  }

  val submit: Action[AnyContent] = authenticate.async { implicit user =>
    YesNoForm.yesNoForm.bindFromRequest().fold(
      error => Future.successful(BadRequest(views.html.optionTax(error))),
      data => outstandingInvoicesAnswerService.storeAnswer(data) flatMap {
        case Right(_) => submitRedirectLogic(data)
        case Left(_) => Future.successful(InternalServerError)
      }
    )
  }

  private def submitRedirectLogic(data: YesNo)(implicit user: User[_]): Future[Result] = {
    if (data == Yes) {
      Future.successful(Redirect(controllers.routes.DeregistrationDateController.show()))
    } else {
      deregReasonAnswerService.getAnswer flatMap {
        case Right(Some(reason)) if reason == BelowThreshold =>
          Future.successful(Redirect(controllers.routes.DeregistrationDateController.show()))
        case Right(Some(_)) => ceasedTradingJourneyLogic(data)
        case _ => Future.successful(InternalServerError)
      }
    }
  }

  private def ceasedTradingJourneyLogic(data: YesNo)(implicit user: User[_]): Future[Result] = {
    capitalAssetsAnswerService.getAnswer map {
      case Right(Some(assets)) =>
        if (assets.yesNo == Yes) {
          Redirect(controllers.routes.DeregistrationDateController.show())
        } else {
          Redirect(controllers.routes.CheckAnswersController.show())
        }
      case _ => InternalServerError
    }
  }
}
