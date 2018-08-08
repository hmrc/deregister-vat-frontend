/*
 * Copyright 2018 HM Revenue & Customs
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

import common.EnrolmentKeys
import config.{AppConfig, ServiceErrorHandler}
import javax.inject.{Inject, Singleton}
import models.User
import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import services.EnrolmentsAuthService
import uk.gov.hmrc.auth.core.retrieve.Retrievals._
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.play.bootstrap.controller.FrontendController

import scala.concurrent.Future

@Singleton
class AuthoriseAsAgent @Inject()(enrolmentsAuthService: EnrolmentsAuthService,
                                 val serviceErrorHandler: ServiceErrorHandler,
                                 implicit val messagesApi: MessagesApi,
                                 implicit val appConfig: AppConfig)
  extends FrontendController with I18nSupport with ActionBuilder[User] with ActionFunction[Request, User] {

  override def invokeBlock[A](request: Request[A], block: User[A] => Future[Result]): Future[Result] = {

    implicit val req: Request[A] = request

    //TODO: use VRN in session, redirect to vat-agent-client-lookup-frontend if not present
    val vrn = "999999999"

    Logger.debug(s"[AuthoriseAsAgent][invokeBlock] - Client VRN from Session: $vrn")
    enrolmentsAuthService
      .authorised(delegatedAuthorityRule(vrn))
      .retrieve(allEnrolments) {
        Logger.debug(s"[AuthoriseAsAgent][invokeBlock] - Authenticated as Agent")
        allEnrolments => block(User(vrn, active = true, Some(arn(allEnrolments))))
      } recover {
      case _: InternalError =>
        Logger.debug(s"[AuthoriseAsAgent][invokeBlock] - Agent does not have a HMRC-AS-AGENT enrolment")
        Forbidden(views.html.errors.agent.unauthorised())

      case _: AuthorisationException =>
        Logger.warn(s"[AuthoriseAsAgent][invokeBlock] - Agent does not have delegated authority for Client")

        //TODO: redirect to new agent lookup service
        Unauthorized(views.html.errors.agent.unauthorised())
    }
  }

  private lazy val delegatedAuthorityRule: String => Enrolment = vrn =>
    Enrolment(EnrolmentKeys.vatEnrolmentId)
      .withIdentifier(EnrolmentKeys.vatIdentifierId, vrn)
      .withDelegatedAuthRule(EnrolmentKeys.mtdVatDelegatedAuthRule)

  private val arn: Enrolments => String = enrolments =>
    enrolments.enrolments.collectFirst {
      case Enrolment(EnrolmentKeys.agentEnrolmentId, EnrolmentIdentifier(_, arnValue) :: _,_,_) => arnValue
    } getOrElse(throw InternalError("Agent Service Enrolment missing"))
}
