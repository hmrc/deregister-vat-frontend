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
import play.api.Logger
import models.{NumberInputModel, No, User, Yes,ZeroRated}

import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent}
import services.{NextTaxableTurnoverAnswerService, TaxableTurnoverAnswerService}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import services.DeregReasonAnswerService

import scala.concurrent.Future

@Singleton
class NextTaxableTurnoverController @Inject()(val messagesApi: MessagesApi,
                                              val authenticate: AuthPredicate,
                                              val pendingDeregCheck: PendingChangesPredicate,
                                              val taxableTurnoverAnswerService: TaxableTurnoverAnswerService,
                                              val deregReasonAnswerService: DeregReasonAnswerService,
                                              val nextTaxableTurnoverAnswerService: NextTaxableTurnoverAnswerService,
                                              val serviceErrorHandler: ServiceErrorHandler,
                                              implicit val appConfig: AppConfig) extends FrontendController with I18nSupport {

  private def renderView(form: Form[NumberInputModel] = NextTaxableTurnoverForm.taxableTurnoverForm)(implicit user: User[_]) =
    views.html.nextTaxableTurnover(form)

  val show: Action[AnyContent] = (authenticate andThen pendingDeregCheck).async { implicit user =>
    nextTaxableTurnoverAnswerService.getAnswer map {
      case Right(Some(data)) => Ok(renderView(NextTaxableTurnoverForm.taxableTurnoverForm.fill(data)))
      case _ => Ok(renderView())
    }
  }

  val submit: Action[AnyContent] = authenticate.async { implicit user =>
    NextTaxableTurnoverForm.taxableTurnoverForm.bindFromRequest().fold(
      error => Future.successful(BadRequest(views.html.nextTaxableTurnover(error))),
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
