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

import com.google.inject.{Inject, Singleton}
import config.{AppConfig, ServiceErrorHandler}
import controllers.predicates.AuthPredicate
import models.User
import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, Result}
import services.DeleteAllStoredAnswersService
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.HeaderCarrierConverter
import uk.gov.hmrc.play.bootstrap.controller.FrontendController

import scala.concurrent.Future

@Singleton
class SignOutController @Inject()(val messagesApi: MessagesApi,
                                  val authentication: AuthPredicate,
                                  val deleteAllStoredAnswersService: DeleteAllStoredAnswersService,
                                  val serviceErrorHandler: ServiceErrorHandler)
                                 (implicit val appConfig: AppConfig) extends FrontendController with I18nSupport {

  def signOut(authorised: Boolean): Action[AnyContent] = authentication.async { implicit user =>
    implicit val hc: HeaderCarrier =
      HeaderCarrierConverter.fromHeadersAndSession(user.headers, Some(user.session))

    (authorised, user.isAgent) match {
      case (true, true) => deleteDataAndRedirect(appConfig.signOutUrl("VATCA"))
      case (true, false) => deleteDataAndRedirect(appConfig.signOutUrl("VATC"))
      case (false, _) => deleteDataAndRedirect(appConfig.unauthorisedSignOutUrl)
    }
  }

  private def deleteDataAndRedirect(redirectUrl: String)(implicit user: User[_]): Future[Result] =
    deleteAllStoredAnswersService.deleteAllAnswers map {
      case Right(_) => Redirect(redirectUrl)
      case Left(_) =>
        Logger.warn("[SignOutController][deleteDataAndRedirect] - storedAnswerService returned an error deleting answers")
        serviceErrorHandler.showInternalServerError
    }

  val timeout: Action[AnyContent] = Action { implicit request =>
    Redirect(appConfig.unauthorisedSignOutUrl)
  }
}
