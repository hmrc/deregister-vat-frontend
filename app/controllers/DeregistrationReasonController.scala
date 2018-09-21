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
import config.AppConfig
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
                                               val ceasedTradingDateAnswerService: CeasedTradingDateAnswerService,
                                               val taxableTurnoverAnswerService: TaxableTurnoverAnswerService,
                                               val nextTaxableTurnoverAnswerService: NextTaxableTurnoverAnswerService,
                                               val whyTurnoverBelowAnswerService: WhyTurnoverBelowAnswerService,
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
      data => {
        deregReasonAnswerService.storeAnswer(data) flatMap {
          case Right(_) => data match {
            case Ceased => deleteAndRedirectCeased
            case BelowThreshold => deleteAndRedirectThreshold
            case Other => Future.successful(Redirect(appConfig.govUkCancelVatRegistration))
          }
          case Left(_) => Future.successful(InternalServerError) //TODO: Render ISE Page
        }
      }
    )
  }

  private def deleteAndRedirectCeased(implicit user: User[_]): Future[Result] = {
    val deleteAnswerResults = for {
      _ <- EitherT(taxableTurnoverAnswerService.deleteAnswer)
      _ <- EitherT(nextTaxableTurnoverAnswerService.deleteAnswer)
      _ <- EitherT(whyTurnoverBelowAnswerService.deleteAnswer)
    } yield None
    deleteAnswerResults.value.map {
      case Right(_) => Redirect(controllers.routes.CeasedTradingDateController.show())
      case Left(_) => InternalServerError //TODO: Render ISE Page
    }
  }

  private def deleteAndRedirectThreshold(implicit user: User[_]): Future[Result] = {
    ceasedTradingDateAnswerService.deleteAnswer.map {
      case Right(_) => Redirect(controllers.routes.TaxableTurnoverController.show())
      case Left(_) => InternalServerError //TODO: Render ISE Page
    }
  }
}

