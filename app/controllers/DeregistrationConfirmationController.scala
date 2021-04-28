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

import audit.models.ContactPreferenceAuditModel
import audit.services.AuditService
import common.SessionKeys
import config.{AppConfig, ServiceErrorHandler}
import controllers.predicates.AuthPredicate
import javax.inject.Inject
import models.User
import play.api.Logger
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.{ContactPreferencesService, CustomerDetailsService, DeleteAllStoredAnswersService}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.DeregistrationConfirmation

import scala.concurrent.{ExecutionContext, Future}

class DeregistrationConfirmationController @Inject()(deregistrationConfirmation: DeregistrationConfirmation,
                                                     val mcc: MessagesControllerComponents,
                                                     val authentication: AuthPredicate,
                                                     val deleteAllStoredAnswersService: DeleteAllStoredAnswersService,
                                                     val serviceErrorHandler: ServiceErrorHandler,
                                                     val customerDetailsService: CustomerDetailsService,
                                                     val auditService: AuditService,
                                                     implicit val customerContactPreference: ContactPreferencesService,
                                                     implicit val ec: ExecutionContext,
                                                     implicit val appConfig: AppConfig) extends FrontendController(mcc) with I18nSupport {

  val show: Action[AnyContent] = authentication.async { implicit user =>

    user.session.get(SessionKeys.deregSuccessful) match {
      case Some("true") =>

        deleteAllStoredAnswersService.deleteAllAnswers flatMap {

          case Right(_) =>

            val serviceCalls = for {
              customerDetailsCall <- customerDetailsService.getCustomerDetails(user.vrn)
              contactPreferenceCall <- getContactPreference
            } yield (customerDetailsCall, contactPreferenceCall)

            serviceCalls.map { result =>

              val businessName: Option[String] = result._1.fold(_ => None, _.businessName)
              val contactPreference: Option[String] = if (appConfig.features.contactPrefMigrationFeature()) {
                result._1.fold(_ => None, _.commsPreference)
              } else {
                result._2.fold(_ => None, {
                  model =>
                    auditService.auditExtendedEvent(ContactPreferenceAuditModel(user.vrn, model.preference))
                    Some(model.preference)
                })
              }

              val verifiedEmail = {
                result._1.fold(_ => None, _.emailVerified)
              }

              Ok(deregistrationConfirmation(businessName, contactPreference, verifiedEmail))
            }

          case Left(_) =>
            Logger.warn("[DeregistrationConfirmationController][show] Error occurred when deleting stored answers. Rendering ISE.")
            Future.successful(serviceErrorHandler.showInternalServerError)
        }
      case _ => Future.successful(Redirect(controllers.routes.DeregisterForVATController.redirect()))
    }
  }

  private def getContactPreference(implicit user: User[AnyContent], hc: HeaderCarrier, ec: ExecutionContext) = {
    if (user.isAgent | appConfig.features.contactPrefMigrationFeature()) {
      Future(Left(None))(ec)
    } else {
      customerContactPreference.getCustomerContactPreferences(user.vrn)(hc, ec)
    }
  }
}
