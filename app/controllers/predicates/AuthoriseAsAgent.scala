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

package controllers.predicates

import common.{EnrolmentKeys, SessionKeys}
import config.{AppConfig, ServiceErrorHandler}
import models.User
import play.api.i18n.I18nSupport
import play.api.mvc._
import services.EnrolmentsAuthService
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals._
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import javax.inject.{Inject, Singleton}
import utils.LoggerUtil

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AuthoriseAsAgent @Inject()(enrolmentsAuthService: EnrolmentsAuthService,
                                 val serviceErrorHandler: ServiceErrorHandler,
                                 val mcc: MessagesControllerComponents,
                                 implicit val appConfig: AppConfig)
  extends FrontendController(mcc) with I18nSupport with ActionBuilder[User, AnyContent] with ActionFunction[Request, User] with LoggerUtil {

  override val parser: BodyParser[AnyContent] = mcc.parsers.defaultBodyParser
  override implicit protected val executionContext: ExecutionContext = mcc.executionContext

  override def invokeBlock[A](request: Request[A], block: User[A] => Future[Result]): Future[Result] = {

    implicit val req: Request[A] = request

    request.session.get(SessionKeys.clientVRN) match {
      case Some(vrn) =>
        logger.debug(s"[AuthoriseAsAgent][invokeBlock] - Client VRN from Session: $vrn")
        enrolmentsAuthService
          .authorised(delegatedAuthorityRule(vrn))
          .retrieve(allEnrolments) {
            logger.debug(s"[AuthoriseAsAgent][invokeBlock] - Authenticated as Agent")
            allEnrolments => block(User(vrn, active = true, Some(arn(allEnrolments))))
          } recover {
          case _: NoActiveSession =>
            logger.debug(s"[AuthoriseAsAgent][invokeBlock] - Agent does not have an active session, redirect to GG Sign In")
            Redirect(appConfig.signInUrl)
          case _ =>
            logger.debug(s"[AuthoriseAsAgent][invokeBlock] - Agent does not have delegated authority for Client")
            Redirect(appConfig.agentClientUnauthorisedUrl)
        }
      case _ =>
        logger.info("[AuthoriseAsAgent][invokeBlock] - No Client VRN in session")
        Future.successful(Redirect(appConfig.agentClientLookupUrl))
    }
  }

  private lazy val delegatedAuthorityRule: String => Enrolment = vrn =>
    Enrolment(EnrolmentKeys.vatEnrolmentId)
      .withIdentifier(EnrolmentKeys.vatIdentifierId, vrn)
      .withDelegatedAuthRule(EnrolmentKeys.mtdVatDelegatedAuthRule)

  private val arn: Enrolments => String = enrolments =>
    enrolments.enrolments.collectFirst {
      case Enrolment(EnrolmentKeys.agentEnrolmentId, Seq(EnrolmentIdentifier(_, arnValue)), _, _) => arnValue
    } getOrElse(throw InternalError("Agent Service Enrolment missing"))
}