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
import forms.TaxableTurnoverForm
import javax.inject.{Inject, Singleton}
import models.{TaxableTurnoverModel, User}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, Result}
import services.{TaxableTurnoverAnswerService, WipeRedundantDataService}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController

import scala.concurrent.Future

@Singleton
class TaxableTurnoverController @Inject()(val messagesApi: MessagesApi,
                                          val authenticate: AuthPredicate,
                                          val taxableTurnoverAnswerService: TaxableTurnoverAnswerService,
                                          val wipeRedundantDataService: WipeRedundantDataService,
                                          implicit val appConfig: AppConfig) extends FrontendController with I18nSupport {

  private def renderView(form: Form[TaxableTurnoverModel] = TaxableTurnoverForm.taxableTurnoverForm)(implicit user: User[_]) =
    views.html.taxableTurnover(form)

  val show: Action[AnyContent] = authenticate.async { implicit user =>
    taxableTurnoverAnswerService.getAnswer map {
      case Right(Some(data)) => Ok(renderView(TaxableTurnoverForm.taxableTurnoverForm.fill(data)))
      case _ => Ok(renderView())
    }
  }

  val submit: Action[AnyContent] = authenticate.async { implicit user =>
    TaxableTurnoverForm.taxableTurnoverForm.bindFromRequest().fold(
      error => Future.successful(BadRequest(views.html.taxableTurnover(error))),
      data => (for {
        _ <- EitherT(taxableTurnoverAnswerService.storeAnswer(data))
        _ <- EitherT(wipeRedundantDataService.wipeRedundantData)
        result = redirect(data)
      } yield result).value.map {
        case Right(result) => result
        case Left(_) => InternalServerError //TODO: Render ISE Page
      }
    )
  }

  private def redirect(taxableTurnover: TaxableTurnoverModel): Result = {
    if (taxableTurnover.turnover > appConfig.deregThreshold) {
      Redirect(controllers.routes.NextTaxableTurnoverController.show())
    } else {
      Redirect(controllers.routes.VATAccountsController.show())
    }
  }


}
