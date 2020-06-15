/*
 * Copyright 2020 HM Revenue & Customs
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
import javax.inject.Inject
import models.User
import play.api.Logger

import play.api.i18n.{I18nSupport}

import play.api.mvc.{ActionBuilder, ActionFunction, AnyContent, BodyParser, MessagesControllerComponents, Request, Result}

import services.EnrolmentsAuthService
import uk.gov.hmrc.auth.core.{AuthorisationException, Enrolments, NoActiveSession}
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals._
import uk.gov.hmrc.auth.core.retrieve.~
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import views.html.errors.client.Unauthorised

import scala.concurrent.{ExecutionContext, Future}

class AuthPredicate @Inject()(unauthorised: Unauthorised,
                              enrolmentsAuthService: EnrolmentsAuthService,
                              val serviceErrorHandler: ServiceErrorHandler,
                              val authoriseAsAgent: AuthoriseAsAgent,
                              val mcc: MessagesControllerComponents,
                              implicit val appConfig: AppConfig)

  extends FrontendController(mcc) with I18nSupport with ActionBuilder[User, AnyContent] with ActionFunction[Request, User] with AuthBasePredicate {

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
          Logger.warn("[AuthPredicate][invokeBlock] - Missing affinity group")
          Future.successful(serviceErrorHandler.showInternalServerError)
      } recover {
      case _: NoActiveSession =>
        Logger.debug("[AuthPredicate][invokeBlock] - No active session, redirect to GG Sign In")
        Redirect(appConfig.signInUrl)
      case _: AuthorisationException =>
        Logger.debug("[AuthPredicate][invokeBlock] - Unauthorised exception, rendering Unauthorised view")
        Forbidden(unauthorised())
    }
  }

  private[AuthPredicate] def checkVatEnrolment[A](enrolments: Enrolments, block: User[A] => Future[Result])(implicit request: Request[A]) =
    if (enrolments.enrolments.exists(_.key == EnrolmentKeys.vatEnrolmentId)) {
      Logger.debug("[AuthPredicate][checkVatEnrolment] - Authenticated as principle")
      block(User(enrolments))
    }
    else {
      Logger.debug(s"[AuthPredicate][checkVatEnrolment] - Individual without HMRC-MTD-VAT enrolment. $enrolments")
      Future.successful(Forbidden(unauthorised()))
    }

}
