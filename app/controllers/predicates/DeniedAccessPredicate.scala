/*
 * Copyright 2022 HM Revenue & Customs
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

package controllers.predicates

import common.Constants
import common.Constants.vatGroup
import common.SessionKeys.registrationStatus
import config.{AppConfig, ServiceErrorHandler}
import models.User
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.Results.Redirect
import play.api.mvc.{ActionRefiner, MessagesControllerComponents, Result}
import services.CustomerDetailsService
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.http.HeaderCarrierConverter
import javax.inject.Inject
import utils.LoggerUtil

import scala.concurrent.{ExecutionContext, Future}

class DeniedAccessPredicate @Inject()(customerDetailsService: CustomerDetailsService,
                                      val serviceErrorHandler: ServiceErrorHandler,
                                      val mcc: MessagesControllerComponents,
                                      implicit val messagesApi: MessagesApi,
                                      implicit val appConfig: AppConfig) extends ActionRefiner[User, User] with I18nSupport with LoggerUtil {

  override implicit val executionContext: ExecutionContext = mcc.executionContext

  override def refine[A](request: User[A]): Future[Either[Result, User[A]]] = {

    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)
    implicit val req: User[A] = request

    req.session.get(registrationStatus) match {
      case Some(Constants.pending) | Some(Constants.deregistered) => Future.successful(Left(Redirect(redirectPage)))
      case Some(Constants.registered) => Future.successful(Right(req))
      case _ => getCustomerInfoCall(req.vrn)
    }
  }

  private def getCustomerInfoCall[A](vrn: String)(implicit hc: HeaderCarrier,
                                                  request: User[A]): Future[Either[Result, User[A]]] =
    customerDetailsService.getCustomerDetails(vrn).map {
      case Right(details) =>
        (details.pendingDereg, details.alreadyDeregistered, details.partyType) match {
          case (_, _, Some(partyType)) if partyType == vatGroup =>
            logger.debug("[PendingChangesPredicate][getCustomerInfoCall] - " +
              "PartyType is VAT Group. Redirecting to appropriate hub/overview page.")
            Left(Redirect(redirectPage))
          case (true, _, _) =>
            logger.debug("[PendingChangesPredicate][getCustomerInfoCall] - " +
              "Deregistration pending. Redirecting to user hub/overview page.")
            Left(Redirect(redirectPage).addingToSession(registrationStatus -> Constants.pending))
          case (_, true, _) =>
            logger.debug("[PendingChangesPredicate][getCustomerInfoCall] - " +
              "User has already deregistered. Redirecting to user hub/overview page.")
            Left(Redirect(redirectPage).addingToSession(registrationStatus -> Constants.deregistered))
          case _ =>
            logger.debug("[PendingChangesPredicate][getCustomerInfoCall] - Redirecting user to start of journey")
            Left(Redirect(controllers.routes.DeregisterForVATController.show().url)
              .addingToSession(registrationStatus -> Constants.registered))
        }
      case Left(error) =>
        logger.warn(s"[InflightPPOBPredicate][getCustomerInfoCall] - " +
          s"The call to the GetCustomerInfo API failed. Error: ${error.message}")
        Left(serviceErrorHandler.showInternalServerError)
    }

  private def redirectPage[A](implicit request: User[A]) =
    if(request.isAgent) appConfig.agentClientLookupAgentHubPath else appConfig.vatSummaryFrontendUrl
}