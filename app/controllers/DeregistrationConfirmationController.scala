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
import models.{DeregisterVatSuccess, Yes}
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.{CustomerDetailsService, DeleteAllStoredAnswersService, OTTNotificationAnswerService}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import utils.LoggingUtil
import views.html.{DeregistrationConfirmation, DeregistrationOTTConfirmation}

import javax.inject.Inject
import scala.concurrent.Future.successful
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

    val deRegistrationSuccessful: Boolean = user.session.get(SessionKeys.deregSuccessful).contains("true")
    def loadOttConfirmationPage: Result = Ok(deRegistrationOTTConfirmation())

    def loadDeregConfirmationPage: Future[Result] = customerDetailsService.getCustomerDetails(user.vrn).map { result =>
      val businessName: Option[String] = result.fold(_ => None, _.businessName)
      val contactPreference: Option[String] = result.fold(_ => None, _.commsPreference)
      val isEmailVerified: Option[Boolean] = result.fold(_ => None, _.emailVerified)

      Ok(deregistrationConfirmation(appConfig.features.ottJourneyEnabled(), businessName, contactPreference, isEmailVerified))
    }

    def logAndReturnErrorPage: Future[Result] = {
      warnLog("[DeregistrationConfirmationController][show] Error occurred when deleting stored answers. Rendering ISE.")
      serviceErrorHandler.showInternalServerError
    }

    if (deRegistrationSuccessful) {
      (for {
        ottFlag <- EitherT(ottAnswerService.getAnswer)
        deleteAllAnswersStatus <- EitherT(deleteAllStoredAnswersService.deleteAllAnswers)
      } yield (ottFlag, deleteAllAnswersStatus)).foldF(
        err => Future.successful(InternalServerError(err.message)),
        {
          case (Some(Yes), DeregisterVatSuccess) if appConfig.features.ottJourneyEnabled() =>
            Future.successful(loadOttConfirmationPage.addingToSession("ottFlag" -> "Yes"))
          case (None, DeregisterVatSuccess) if user.session.get("ottFlag").contains("Yes") =>
            Future.successful(loadOttConfirmationPage)
          case (_, DeregisterVatSuccess) => loadDeregConfirmationPage

          case _ => logAndReturnErrorPage
        }
      )
    } else {
      Future.successful(Redirect(controllers.routes.DeregisterForVATController.show))
    }
  }
}
