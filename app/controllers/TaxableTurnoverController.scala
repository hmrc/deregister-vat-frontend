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
import controllers.predicates.{AuthPredicate, DeniedAccessPredicate}
import forms.YesNoForm
import javax.inject.{Inject, Singleton}
import models.{User, YesNo}
import play.api.Logger
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.{TaxableTurnoverAnswerService, WipeRedundantDataService}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import utils.MoneyFormatter
import views.html.TaxableTurnover

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TaxableTurnoverController @Inject()(taxableTurnover: TaxableTurnover,
                                           val mcc: MessagesControllerComponents,
                                          val authenticate: AuthPredicate,
                                          val regStatusCheck: DeniedAccessPredicate,
                                          val taxableTurnoverAnswerService: TaxableTurnoverAnswerService,
                                          val wipeRedundantDataService: WipeRedundantDataService,
                                          val serviceErrorHandler: ServiceErrorHandler,
                                          implicit val ec: ExecutionContext,
                                          implicit val appConfig: AppConfig) extends FrontendController(mcc) with I18nSupport {

  val form: Form[YesNo] = YesNoForm.yesNoForm("taxableTurnover.error.mandatoryRadioOption", MoneyFormatter.formatStringAmount(appConfig.deregThreshold))

  private def renderView(form: Form[YesNo])(implicit user: User[_]) =
    taxableTurnover(form)

  val show: Action[AnyContent] = (authenticate andThen regStatusCheck).async { implicit user =>
    taxableTurnoverAnswerService.getAnswer map {
      case Right(Some(data)) => Ok(renderView(form.fill(data)))
      case _ => Ok(renderView(form))
    }
  }

  val submit: Action[AnyContent] = authenticate.async { implicit user =>
    form.bindFromRequest().fold(
      error => Future.successful(BadRequest(taxableTurnover(error))),
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
