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
import services.{CustomerDetailsService, EnrolmentsAuthService}
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals._
import uk.gov.hmrc.auth.core.retrieve.~
import uk.gov.hmrc.auth.core.{AuthorisationException, Enrolments, NoActiveSession}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.errors.client.Unauthorised
import views.html.errors.client.InsolventError
import javax.inject.Inject
import utils.LoggerUtil

import scala.concurrent.{ExecutionContext, Future}

class AuthPredicate @Inject()(unauthorised: Unauthorised,
                              insolvent: InsolventError,
                              enrolmentsAuthService: EnrolmentsAuthService,
                              customerDetailsService: CustomerDetailsService,
                              val serviceErrorHandler: ServiceErrorHandler,
                              val authoriseAsAgent: AuthoriseAsAgent,
                              val mcc: MessagesControllerComponents)
                             (implicit val appConfig: AppConfig)

  extends FrontendController(mcc)
  with I18nSupport with ActionBuilder[User, AnyContent] with ActionFunction[Request, User] with AuthBasePredicate with LoggerUtil{

  override val parser: BodyParser[AnyContent] = mcc.parsers.defaultBodyParser
  override implicit protected val executionContext: ExecutionContext = mcc.executionContext

  override def invokeBlock[A](request: Request[A], block: User[A] => Future[Result]): Future[Result] = {

    implicit val req: Request[A] = request

    enrolmentsAuthService
      .authorised()
      .retrieve(affinityGroup and allEnrolments) {
        case Some(affinity) ~ enrolments =>
          if(isAgent(affinity)) {
            authoriseAsAgent.invokeBlock(request, block)
          } else {
            checkVatEnrolment(enrolments, block)
          }
        case _ =>
          logger.warn("[AuthPredicate][invokeBlock] - Missing affinity group")
          Future.successful(serviceErrorHandler.showInternalServerError)
      } recover {
      case _: NoActiveSession =>
        logger.debug("[AuthPredicate][invokeBlock] - No active session, redirect to GG Sign In")
        Redirect(appConfig.signInUrl)
      case _: AuthorisationException =>
        logger.debug("[AuthPredicate][invokeBlock] - Unauthorised exception, rendering Unauthorised view")
        Forbidden(unauthorised())
    }
  }

  private[AuthPredicate] def checkVatEnrolment[A](enrolments: Enrolments, block: User[A] => Future[Result])(implicit request: Request[A]) =
    if (enrolments.enrolments.exists(_.key == EnrolmentKeys.vatEnrolmentId)) {
      val user = User(enrolments)
      request.session.get(SessionKeys.insolventWithoutAccessKey) match {
        case Some("true") => Future.successful(Forbidden(insolvent()(user, request2Messages, appConfig)))
        case Some("false") => block(user)
        case _ =>
          customerDetailsService.getCustomerDetails(user.vrn).flatMap {
            case Right(details) if details.isInsolventWithoutAccess =>
              logger.debug("[AuthPredicate][checkVatEnrolment] - User is insolvent and not continuing to trade")
              Future.successful(Forbidden(insolvent()
              (user, request2Messages, appConfig)).addingToSession(SessionKeys.insolventWithoutAccessKey -> "true"))
            case Right(_) =>
              logger.debug("[AuthPredicate][checkVatEnrolment] - Authenticated as principle")
              block(user).map(result => result.addingToSession(SessionKeys.insolventWithoutAccessKey -> "false"))
            case _ =>
              logger.warn("[AuthPredicate][checkVatEnrolment] - Failure obtaining insolvency status from Customer Info API")
              Future.successful(serviceErrorHandler.showInternalServerError)
          }
      }

    }
    else {
      logger.debug(s"[AuthPredicate][checkVatEnrolment] - Individual without HMRC-MTD-VAT enrolment. $enrolments")
      Future.successful(Forbidden(unauthorised()))
    }

}