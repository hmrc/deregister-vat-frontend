/*
 * Copyright 2023 HM Revenue & Customs
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

import com.google.inject.{Inject, Singleton}
import config.{AppConfig, ServiceErrorHandler}
import controllers.predicates.AuthPredicate
import models.User
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.DeleteAllStoredAnswersService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import utils.LoggerUtil

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SignOutController @Inject()(val mcc: MessagesControllerComponents,
                                  val authentication: AuthPredicate,
                                  val deleteAllStoredAnswersService: DeleteAllStoredAnswersService,
                                  implicit val ec: ExecutionContext,
                                  val serviceErrorHandler: ServiceErrorHandler)
                                 (implicit val appConfig: AppConfig) extends FrontendController(mcc) with I18nSupport with LoggerUtil {

  def signOut(authorised: Boolean): Action[AnyContent] = {
    if(authorised){
      signOutAuthorised
    } else {
      signOutUnauthorised
    }
  }

  def signOutAuthorised: Action[AnyContent] = authentication.async { implicit user =>
    if(user.isAgent) {
      deleteDataAndRedirect(appConfig.signOutUrl("VATCA"))
    } else {
      deleteDataAndRedirect(appConfig.signOutUrl("VATC"))
    }
  }

  def signOutUnauthorised: Action[AnyContent] = Action {
    Redirect(appConfig.unauthorisedSignOutUrl)
  }

  private def deleteDataAndRedirect(redirectUrl: String)(implicit user: User[_]): Future[Result] =
    deleteAllStoredAnswersService.deleteAllAnswers map {
      case Right(_) => Redirect(redirectUrl)
      case Left(error) =>
        logger.warn("[SignOutController][deleteDataAndRedirect] - storedAnswerService returned an error deleting answers: " + error.message)
        serviceErrorHandler.showInternalServerError
    }

  val timeout: Action[AnyContent] = Action {
    Redirect(appConfig.unauthorisedSignOutUrl)
  }
}
