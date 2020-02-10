/*
 * Copyright 2020 HM Revenue & Customs
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
import forms.YesNoForm
import javax.inject.{Inject, Singleton}
import models.{User, YesNo}
import play.api.Logger
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent}
import services.{TaxableTurnoverAnswerService, WipeRedundantDataService}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import utils.MoneyFormatter

import scala.concurrent.Future

@Singleton
class TaxableTurnoverController @Inject()(val messagesApi: MessagesApi,
                                          val authenticate: AuthPredicate,
                                          val pendingDeregCheck: PendingChangesPredicate,
                                          val taxableTurnoverAnswerService: TaxableTurnoverAnswerService,
                                          val wipeRedundantDataService: WipeRedundantDataService,
                                          val serviceErrorHandler: ServiceErrorHandler,
                                          implicit val appConfig: AppConfig) extends FrontendController with I18nSupport {

  val form = YesNoForm.yesNoForm("taxableTurnover.error.mandatoryRadioOption", MoneyFormatter.formatStringAmount(appConfig.deregThreshold))

  private def renderView(form: Form[YesNo])(implicit user: User[_]) =
    views.html.taxableTurnover(form)

  val show: Action[AnyContent] = (authenticate andThen pendingDeregCheck).async { implicit user =>
    taxableTurnoverAnswerService.getAnswer map {
      case Right(Some(data)) => Ok(renderView(form.fill(data)))
      case _ => Ok(renderView(form))
    }
  }

  val submit: Action[AnyContent] = authenticate.async { implicit user =>
    form.bindFromRequest().fold(
      error => Future.successful(BadRequest(views.html.taxableTurnover(error))),
      data => (for {
        _ <- EitherT(taxableTurnoverAnswerService.storeAnswer(data))
        result <- EitherT(wipeRedundantDataService.wipeRedundantData)
      } yield result).value.map {
        case Right(_) => Redirect(controllers.routes.NextTaxableTurnoverController.show())
        case Left(error) =>
          Logger.warn("[TaxableTurnoverController][submit] - storedAnswerService returned an error: " + error.message)
          serviceErrorHandler.showInternalServerError
      }
    )
  }

}
