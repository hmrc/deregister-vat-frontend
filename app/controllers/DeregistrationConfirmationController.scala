/*
 * Copyright 2024 HM Revenue & Customs
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
import common.SessionKeys
import config.{AppConfig, ServiceErrorHandler}
import controllers.predicates.AuthPredicate
import models.{DeregisterVatResponse, DeregisterVatSuccess, Yes, YesNo}
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.{CustomerDetailsService, DeleteAllStoredAnswersService, OTTNotificationAnswerService, OptionTaxAnswerService}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import utils.LoggingUtil
import views.html.{DeregistrationConfirmation, DeregistrationOTTConfirmation}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DeregistrationConfirmationController @Inject()(deregistrationConfirmation: DeregistrationConfirmation,
                                                     deRegistrationOTTConfirmation: DeregistrationOTTConfirmation,
                                                     val mcc: MessagesControllerComponents,
                                                     val authentication: AuthPredicate,
                                                     val ottAnswerService: OTTNotificationAnswerService,
                                                     val deleteAllStoredAnswersService: DeleteAllStoredAnswersService,
                                                     val serviceErrorHandler: ServiceErrorHandler,
                                                     val customerDetailsService: CustomerDetailsService)
                                                    (implicit val ec: ExecutionContext,
                                                     val appConfig: AppConfig) extends FrontendController(mcc) with I18nSupport with LoggingUtil {

  val show: Action[AnyContent] = authentication.async { implicit user =>

    user.session.get(SessionKeys.deregSuccessful) match {
      case Some("true") =>
        (for{
          ottFlag <- EitherT(ottAnswerService.getAnswer)
          deregStatus <- EitherT(deleteAllStoredAnswersService.deleteAllAnswers)
        } yield {
            val yesValue: Boolean = ottFlag.contains(Yes.value)
            val deRegStatusValue: DeregisterVatResponse = deregStatus

            (yesValue, deRegStatusValue) match {

            case (true, DeregisterVatSuccess) =>
              if(appConfig.features.ottJourneyEnabled()){
                Future.successful(Ok(deRegistrationOTTConfirmation()))
              }
            case (_, DeregisterVatSuccess) =>
              customerDetailsService.getCustomerDetails(user.vrn).flatMap { result =>
                val businessName: Option[String]      = result.fold(_ => None, _.businessName)
                val contactPreference: Option[String] = result.fold(_ => None, _.commsPreference)
                val isEmailVerified: Option[Boolean]  = result.fold(_ => None, _.emailVerified)

                Future.successful(Ok(deregistrationConfirmation(appConfig.ottJourneyFlag, businessName, contactPreference, isEmailVerified)))
              }
            case _ =>
              warnLog("[DeregistrationConfirmationController][show] Error occurred when deleting stored answers. Rendering ISE.")
              serviceErrorHandler.showInternalServerError
          }
        }).foldF (
          {err =>
            warnLog(s"[DeregistrationConfirmationController][show] Error occurred when deleting stored answers. Rendering ISE.${err.message}")
            serviceErrorHandler.showInternalServerError
          },
          view => Future.successful(Ok(view))
        )

      case _ => Future.successful(Redirect(controllers.routes.DeregisterForVATController.show))
    }
  }
}
