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

import common.SessionKeys
import config.{AppConfig, ServiceErrorHandler}
import controllers.predicates.AuthPredicate
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.{CustomerDetailsService, DeleteAllStoredAnswersService}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.DeregistrationConfirmation
import javax.inject.Inject
import utils.LoggerUtil

import scala.concurrent.{ExecutionContext, Future}

class DeregistrationConfirmationController @Inject()(deregistrationConfirmation: DeregistrationConfirmation,
                                                     val mcc: MessagesControllerComponents,
                                                     val authentication: AuthPredicate,
                                                     val deleteAllStoredAnswersService: DeleteAllStoredAnswersService,
                                                     val serviceErrorHandler: ServiceErrorHandler,
                                                     val customerDetailsService: CustomerDetailsService)
                                                    (implicit val ec: ExecutionContext,
                                                     val appConfig: AppConfig) extends FrontendController(mcc) with I18nSupport with LoggerUtil {

  val show: Action[AnyContent] = authentication.async { implicit user =>

    user.session.get(SessionKeys.deregSuccessful) match {
      case Some("true") =>

        deleteAllStoredAnswersService.deleteAllAnswers flatMap {

          case Right(_) =>

            customerDetailsService.getCustomerDetails(user.vrn).map { result =>

              val businessName: Option[String] = result.fold(_ => None, _.businessName)
              val contactPreference: Option[String] = result.fold(_ => None, _.commsPreference)
              val verifiedEmail = result.fold(_ => None, _.emailVerified)

              Ok(deregistrationConfirmation(businessName, contactPreference, verifiedEmail))
            }

          case Left(_) =>
            logger.warn("[DeregistrationConfirmationController][show] Error occurred when deleting stored answers. Rendering ISE.")
            Future.successful(serviceErrorHandler.showInternalServerError)
        }
      case _ => Future.successful(Redirect(controllers.routes.DeregisterForVATController.show()))
    }
  }
}