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
import javax.inject.{Inject, Singleton}
import config.{AppConfig, ServiceErrorHandler}
import controllers.predicates.AuthPredicate
import forms.DeregistrationReasonForm
import models._
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import services._
import uk.gov.hmrc.play.bootstrap.controller.FrontendController

import scala.concurrent.Future

@Singleton
class DeregistrationReasonController @Inject()(val messagesApi: MessagesApi,
                                               val authenticate: AuthPredicate,
                                               val deregReasonAnswerService: DeregReasonAnswerService,
                                               val wipeRedundantDataService: WipeRedundantDataService,
                                               val serviceErrorHandler: ServiceErrorHandler,
                                               implicit val appConfig: AppConfig) extends FrontendController with I18nSupport {

  private def renderView(data: Form[DeregistrationReason] = DeregistrationReasonForm.deregistrationReasonForm)(implicit user: User[_]) =
    views.html.deregistrationReason(data)

  val show: Boolean => Action[AnyContent] = _ => authenticate.async { implicit user =>
    deregReasonAnswerService.getAnswer map {
      case Right(Some(data)) => Ok(renderView(DeregistrationReasonForm.deregistrationReasonForm.fill(data)))
      case _ => Ok(renderView())
    }
  }

  val submit: Action[AnyContent] = authenticate.async { implicit user =>
    DeregistrationReasonForm.deregistrationReasonForm.bindFromRequest().fold(
      error => Future.successful(BadRequest(renderView(error))),
      data => (for {
        _ <- EitherT(deregReasonAnswerService.storeAnswer(data))
        _ <- EitherT(wipeRedundantDataService.wipeRedundantData)
        route = redirect(data)
      } yield route).value.map {
        case Right(result) => result
        case Left(_) => serviceErrorHandler.showInternalServerError
      }
    )
  }

  private def redirect(deregReason: DeregistrationReason): Result = deregReason match {
    case Ceased => Redirect(controllers.routes.CeasedTradingDateController.show())
    case BelowThreshold => Redirect(controllers.routes.TaxableTurnoverController.show())
    case Other => Redirect(appConfig.govUkCancelVatRegistration)
  }
}

