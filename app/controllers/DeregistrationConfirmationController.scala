/*
 * Copyright 2019 HM Revenue & Customs
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

import audit.models.ContactPreferenceAuditModel
import audit.services.AuditService
import config.{AppConfig, ServiceErrorHandler}
import controllers.predicates.AuthPredicate
import javax.inject.Inject
import models.User
import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent}
import services.{ContactPreferencesService, CustomerDetailsService, DeleteAllStoredAnswersService}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.controller.FrontendController

import scala.concurrent.{ExecutionContext, Future}

class DeregistrationConfirmationController @Inject()(val messagesApi: MessagesApi,
                                                     val authentication: AuthPredicate,
                                                     val deleteAllStoredAnswersService: DeleteAllStoredAnswersService,
                                                     val serviceErrorHandler: ServiceErrorHandler,
                                                     val customerDetailsService: CustomerDetailsService,
                                                     val auditService: AuditService,
                                                     implicit val customerContactPreference: ContactPreferencesService,
                                                     implicit val appConfig: AppConfig) extends FrontendController with I18nSupport {

  val show: Action[AnyContent] = authentication.async { implicit user =>

    deleteAllStoredAnswersService.deleteAllAnswers flatMap {

      case Right(_) =>

        val serviceCalls = for {
          customerDetailsCall <- customerDetailsService.getCustomerDetails(user.vrn)
          contactPreferenceCall <- getContactPreference
        } yield (customerDetailsCall, contactPreferenceCall)

        serviceCalls.map { result =>

          val businessName: Option[String] = result._1.fold(_ => None, _.businessName)
          val contactPreference: Option[String] = result._2.fold(_ => None, {
            model =>
              auditService.auditExtendedEvent(ContactPreferenceAuditModel(user.vrn, model.preference))
              Some(model.preference)
          })

          Ok(views.html.deregistrationConfirmation(businessName, contactPreference))
        }

      case Left(_) =>
        Logger.warn("[DeregistrationConfirmationController][show] Error occurred when deleting stored answers. Rendering ISE.")
        Future.successful(serviceErrorHandler.showInternalServerError)
    }
  }

  private def getContactPreference(implicit user: User[AnyContent], hc: HeaderCarrier, ec: ExecutionContext) = {
    if (!user.isAgent && appConfig.features.useContactPreferences()) {
      customerContactPreference.getCustomerContactPreferences(user.vrn)(hc, ec)
    } else {
      Future(Left(None))(ec)
    }
  }
}
