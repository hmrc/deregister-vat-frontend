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

import config.{AppConfig, ServiceErrorHandler}
import controllers.predicates.{AuthPredicate, DeniedAccessPredicate}
import forms.DateForm
import javax.inject.{Inject, Singleton}
import models.{DateModel, User}
import play.api.Logger
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc._
import services.CeasedTradingDateAnswerService
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import views.html.CeasedTradingDate

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CeasedTradingDateController @Inject()(ceasedTradingDate: CeasedTradingDate,
                                             val mcc: MessagesControllerComponents,
                                            val authenticate: AuthPredicate,
                                            val regStatusCheck: DeniedAccessPredicate,
                                            val ceasedTradingDateAnswerService: CeasedTradingDateAnswerService,
                                            val serviceErrorHandler: ServiceErrorHandler,
                                            implicit val ec: ExecutionContext,
                                            implicit val appConfig: AppConfig) extends FrontendController(mcc) with I18nSupport {

  private def renderView(form: Form[DateModel] = DateForm.dateForm)(implicit user: User[_]) = ceasedTradingDate(form)

  val show: Action[AnyContent] = (authenticate andThen regStatusCheck).async { implicit user =>
    ceasedTradingDateAnswerService.getAnswer map {
      case Right(Some(data)) => Ok(renderView(DateForm.dateForm.fill(data)))
      case _ => Ok(renderView())
    }
  }

  val submit: Action[AnyContent] = authenticate.async { implicit user =>
    DateForm.dateForm.bindFromRequest().fold(
      error => Future.successful(BadRequest(ceasedTradingDate(error))),
      data => ceasedTradingDateAnswerService.storeAnswer(data) map {
        case Right(_) => Redirect(controllers.routes.VATAccountsController.show())
        case Left(error) =>
          Logger.warn("[CeasedTradingDateController][submit] - storedAnswerService returned an error: " + error.message)
          serviceErrorHandler.showInternalServerError
      }
    )
  }
}
