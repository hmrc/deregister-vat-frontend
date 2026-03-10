/*
 * Copyright 2026 HM Revenue & Customs
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
import forms.OptionTaxValueForm
import models.NumberInputModel
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.OptionTaxValueAnswerService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import utils.LoggingUtil
import views.html.OptionTaxValue

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class OptionTaxValueController @Inject()(optionTaxValue: OptionTaxValue,
                                         val mcc: MessagesControllerComponents,
                                         val authenticate: AuthPredicate,
                                         val regStatusCheck: DeniedAccessPredicate,
                                         val valueOptionTaxAnswerService: OptionTaxValueAnswerService,
                                         val serviceErrorHandler: ServiceErrorHandler,
                                         implicit val ec: ExecutionContext,
                                         implicit val appConfig: AppConfig
                                        ) extends FrontendController(mcc) with I18nSupport with LoggingUtil {

  val form: Form[NumberInputModel] = OptionTaxValueForm.optionTaxValueForm

  val show: Action[AnyContent] = (authenticate andThen regStatusCheck).async { implicit user =>
    valueOptionTaxAnswerService.getAnswer map {
      case Right(Some(data)) => Ok(optionTaxValue(form.fill(data)))
      case _ => Ok(optionTaxValue(form))
    }
  }

  val submit: Action[AnyContent] = (authenticate andThen regStatusCheck).async { implicit user =>
    form.bindFromRequest().fold(
      error => Future.successful(BadRequest(optionTaxValue(error))),
      data => valueOptionTaxAnswerService.storeAnswer(data).flatMap {
        case Right(_) => Future.successful(Redirect(controllers.routes.CapitalAssetsController.show))
        case Left(error) =>
          warnLog("[OptionTaxValueController][submit] - storedAnswerService returned an error storing answer: " + error.message)
          serviceErrorHandler.showInternalServerError
      }
    )

  }

}
