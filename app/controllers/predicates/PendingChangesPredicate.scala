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

package controllers.predicates

import common.SessionKeys.pendingDeregKey
import config.{AppConfig, ServiceErrorHandler}
import javax.inject.Inject
import models.{PendingDeregModel, User}
import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.Results.Redirect
import play.api.mvc.{ActionRefiner, Result}
import services.CustomerDetailsService
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.HeaderCarrierConverter

import scala.concurrent.{ExecutionContext, Future}

class PendingChangesPredicate @Inject()(customerDetailsService: CustomerDetailsService,
                                        val serviceErrorHandler: ServiceErrorHandler,
                                        implicit val messagesApi: MessagesApi,
                                        implicit val appConfig: AppConfig,
                                        implicit val ec: ExecutionContext) extends ActionRefiner[User, User] with I18nSupport {

  override def refine[A](request: User[A]): Future[Either[Result, User[A]]] = {

    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromHeadersAndSession(request.headers, Some(request.session))
    implicit val req: User[A] = request

    req.session.get(pendingDeregKey) match {
      case Some("true") => Future.successful(Left(Redirect(appConfig.manageVatSubscriptionFrontendUrl)))
      case Some("false") => Future.successful(Right(req))
      case Some(_) => Future.successful(Left(serviceErrorHandler.showInternalServerError))
      case None => getCustomerInfoCall(req.vrn)
    }
  }

  private def getCustomerInfoCall[A](vrn: String)(implicit hc: HeaderCarrier,
                                                  request: User[A]): Future[Either[Result, User[A]]] =
    customerDetailsService.getDeregPending(vrn).map {
      case Right(pending) =>
        pending.deregistration match {
          case Some(PendingDeregModel(true)) =>
            Logger.warn("[PendingChangesPredicate][getCustomerInfoCall] - " +
              "Deregistration pending. Redirecting to Manage VAT service.")
            Left(Redirect(appConfig.manageVatSubscriptionFrontendUrl).addingToSession(pendingDeregKey -> "true"))
          case Some(PendingDeregModel(false)) =>
            Logger.debug("[PendingChangesPredicate][getCustomerInfoCall] - " +
              "Pending deregistration is false - Setting to 'false' and redirecting to start of journey")
            Left(Redirect(controllers.routes.DeregisterForVATController.show().url).addingToSession(pendingDeregKey -> "false"))
          case None =>
            Logger.warn("[InflightPPOBPredicate][getCustomerInfoCall] - " +
              "There is no pending deregistration data - Setting to 'false' and redirecting to start of journey")
            Left(Redirect(controllers.routes.DeregisterForVATController.show().url)
              .addingToSession(pendingDeregKey -> "false"))
        }
      case Left(error) =>
        Logger.warn(s"[InflightPPOBPredicate][getCustomerInfoCall] - " +
          s"The call to the GetCustomerInfo API failed. Error: ${error.message}")
        Left(serviceErrorHandler.showInternalServerError)
    }
}
