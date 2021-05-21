/*
 * Copyright 2021 HM Revenue & Customs
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
import models.{No, User, Yes, YesNo}
import play.api.Logging
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc._
import services.{ChooseDeregDateAnswerService, OutstandingInvoicesAnswerService, WipeRedundantDataService}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.ChooseDeregistrationDate

import scala.concurrent.ExecutionContext

@Singleton
class ChooseDeregistrationDateController @Inject()(chooseDeregistrationDate: ChooseDeregistrationDate,
                                                    val mcc: MessagesControllerComponents,
                                                   val authenticate: AuthPredicate,
                                                   val regStatusCheck: DeniedAccessPredicate,
                                                   val chooseDateAnswerService: ChooseDeregDateAnswerService,
                                                   val outstandingInvoicesAnswerService: OutstandingInvoicesAnswerService,
                                                   val wipeRedundantDataService: WipeRedundantDataService,
                                                   val serviceErrorHandler: ServiceErrorHandler,
                                                   implicit val ec: ExecutionContext,
                                                   implicit val appConfig: AppConfig) extends FrontendController(mcc)
                                                   with Logging with I18nSupport {

  val form: Form[YesNo] = YesNoForm.yesNoForm("chooseDeregistrationDate.error.mandatoryRadioOption")

  private def renderView(outstanding: Option[YesNo], form: Form[YesNo])
                        (implicit user: User[_]) = chooseDeregistrationDate(outstanding, form)

  val show: Action[AnyContent] = (authenticate andThen regStatusCheck).async { implicit user =>
    for {
      chooseDateResult <- chooseDateAnswerService.getAnswer
      outstandingInvoicesResult <- outstandingInvoicesAnswerService.getAnswer
    } yield (outstandingInvoicesResult, chooseDateResult) match {
      case (Right(outstandingInvoices), Right(Some(deregDate))) =>
        Ok(renderView(outstandingInvoices,form.fill(deregDate)))
      case (Right(outstanding), Right(None)) =>
        Ok(renderView(outstanding,form))
      case (_,_) =>
        logger.warn("[ChooseDeregistrationDateController][show] - storedAnswerService returned an error retrieving answers")
        serviceErrorHandler.showInternalServerError
    }
  }

  val submit: Action[AnyContent] = authenticate.async { implicit user =>
    form.bindFromRequest().fold(
      error => outstandingInvoicesAnswerService.getAnswer map {
        case Right(outstandingInvoices) => BadRequest(renderView(outstandingInvoices, error))
        case Left(err) =>
          logger.warn("[ChooseDeregistrationDateController][submit] - storedAnswerService returned an error retrieving answers: " + err.message)
          serviceErrorHandler.showInternalServerError
      },
      data => (for {
        _ <- EitherT(chooseDateAnswerService.storeAnswer(data))
        _ <- EitherT(wipeRedundantDataService.wipeRedundantData)
        result = redirect(data)
      } yield result).value.map {
        case Right(redirect) => redirect
        case Left(error) =>
          logger.warn("[ChooseDeregistrationDateController][submit] - storedAnswerService returned an error storing answers: " + error.message)
          serviceErrorHandler.showInternalServerError
      }
    )
  }

  private def redirect (yesNo: YesNo) : Result = yesNo match {
    case Yes => Redirect(controllers.routes.DeregistrationDateController.show())
    case No => Redirect(controllers.routes.CheckAnswersController.show())
  }
}
