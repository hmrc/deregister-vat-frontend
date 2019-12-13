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

package controllers.zeroRated

import cats.data.EitherT
import cats.instances.future._
import config.{AppConfig, ServiceErrorHandler}
import controllers.predicates.{AuthPredicate, PendingChangesPredicate}
import javax.inject.{Inject, Singleton}
import models.{No, User, Yes, YesNo}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import services._
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import views.html.businessActivity
import play.api.data.Form
import forms.BusinessActivityForm

import scala.concurrent.Future

@Singleton
class BusinessActivityController @Inject()(val messagesApi: MessagesApi,
                                           val authenticate: AuthPredicate,
                                           val pendingDeregCheck: PendingChangesPredicate,
                                           val businessActivityAnswerService: BusinessActivityAnswerService,
                                           val serviceErrorHandler: ServiceErrorHandler,
                                           implicit val appConfig: AppConfig) extends FrontendController with I18nSupport {

  private def renderView(form: Form[YesNo] = BusinessActivityForm.businessActivityForm)(implicit user: User[_]) = businessActivity(form)

  private def redirect (yesNo: Option[YesNo]) : Result = yesNo match {
    case Some(Yes) => Redirect(controllers.zeroRated.routes.SicCodeController.show())
    case Some(No) => Redirect(controllers.routes.NextTaxableTurnoverController.show())
  }

  val show: Action[AnyContent] = (authenticate andThen pendingDeregCheck).async { implicit user =>
    if (appConfig.features.zeroRatedJourney()) {
      businessActivityAnswerService.getAnswer map {
        case Right(Some(data)) => Ok(renderView(BusinessActivityForm.businessActivityForm.fill(data)))
        case _ => Ok(renderView())
      }
    } else {
      Future(serviceErrorHandler.showBadRequestError)
    }
  }

  val submit: Action[AnyContent] = authenticate.async { implicit user =>
    if (appConfig.features.zeroRatedJourney()) {
      BusinessActivityForm.businessActivityForm.bindFromRequest().fold(
        error => Future.successful(BadRequest(views.html.businessActivity(error))),
        data => (for {
          _ <- EitherT(businessActivityAnswerService.storeAnswer(data))
          result = redirect(Some(data))
        } yield result).value.map {
          case Right(redirect) => redirect
          case Left(_) => serviceErrorHandler.showInternalServerError
        }
      )
    } else {
      Future(serviceErrorHandler.showBadRequestError)
    }
  }
}

