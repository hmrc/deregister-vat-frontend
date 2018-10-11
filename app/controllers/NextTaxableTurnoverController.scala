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
import forms.NextTaxableTurnoverForm
import javax.inject.{Inject, Singleton}
import models._
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent}
import services.{NextTaxableTurnoverAnswerService, TaxableTurnoverAnswerService}
import play.api.mvc.{Action, AnyContent, Result}
import services.{NextTaxableTurnoverAnswerService, TaxableTurnoverAnswerService, WhyTurnoverBelowAnswerService}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.controller.FrontendController

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class NextTaxableTurnoverController @Inject()(val messagesApi: MessagesApi,
                                              val authenticate: AuthPredicate,
                                              val taxableTurnoverAnswerService: TaxableTurnoverAnswerService,
                                              val nextTaxableTurnoverAnswerService: NextTaxableTurnoverAnswerService,
                                              val whyTurnoverBelowAnswerService: WhyTurnoverBelowAnswerService,
                                              implicit val appConfig: AppConfig) extends FrontendController with I18nSupport {

  private def renderView(form: Form[NextTaxableTurnoverModel] = NextTaxableTurnoverForm.taxableTurnoverForm)(implicit user: User[_]) =
    views.html.nextTaxableTurnover(form)

  val show: Action[AnyContent] = authenticate.async { implicit user =>
    nextTaxableTurnoverAnswerService.getAnswer map {
      case Right(Some(data)) => Ok(renderView(NextTaxableTurnoverForm.taxableTurnoverForm.fill(data)))
      case _ => Ok(renderView())
    }
  }

  val submit: Action[AnyContent] = authenticate.async { implicit user =>
    NextTaxableTurnoverForm.taxableTurnoverForm.bindFromRequest().fold(
      error => Future.successful(BadRequest(views.html.nextTaxableTurnover(error))),
      data =>
        (for{
          _ <- EitherT(nextTaxableTurnoverAnswerService.storeAnswer(data))
          taxableTurnoverGetResponse <- EitherT(taxableTurnoverAnswerService.getAnswer)
          nextTurnover = data.turnover > appConfig.deregThreshold
        } yield (taxableTurnoverGetResponse, nextTurnover)).value.flatMap {
          case Right((_, nextTurnoverOverThreshold)) if nextTurnoverOverThreshold =>
            Future.successful(Redirect(controllers.routes.CannotDeregisterThresholdController.show()))
          case Right((Some(Yes), _)) => storeTurnoverBelow
          case Right(_) =>
            Future.successful(Redirect(controllers.routes.WhyTurnoverBelowController.show()))
          case Left(_) =>
            Future.successful(InternalServerError)
        }
    )
  }

  private def storeTurnoverBelow(implicit user: User[_]): Future[Result] = {
    whyTurnoverBelowAnswerService.storeAnswer(TurnoverAlreadyBelow).map {
      case Right(_) => Redirect(controllers.routes.VATAccountsController.show())
      case Left(_) => InternalServerError
    }
  }
}
