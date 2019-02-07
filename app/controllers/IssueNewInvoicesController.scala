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

import cats.data.EitherT
import cats.instances.future._
import config.{AppConfig, ServiceErrorHandler}
import controllers.predicates.{AuthPredicate, PendingChangesPredicate}
import forms.YesNoForm
import javax.inject.{Inject, Singleton}
import models._
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import services._
import uk.gov.hmrc.play.bootstrap.controller.FrontendController

import scala.concurrent.Future

@Singleton
class IssueNewInvoicesController @Inject()(val messagesApi: MessagesApi,
                                           val authenticate: AuthPredicate,
                                           val pendingDeregCheck: PendingChangesPredicate,
                                           val issueNewInvoiceAnswerService: IssueNewInvoicesAnswerService,
                                           val wipeRedundantDataService: WipeRedundantDataService,
                                           val serviceErrorHandler: ServiceErrorHandler,
                                           implicit val appConfig: AppConfig) extends FrontendController with I18nSupport {

  private def renderView(form: Form[YesNo] = YesNoForm.yesNoForm)(implicit user: User[_]) = views.html.issueNewInvoices(form)

  private def redirect: YesNo => Result = {
    case Yes => Redirect(controllers.routes.DeregistrationDateController.show())
    case No => Redirect(controllers.routes.OutstandingInvoicesController.show())
  }

  val show: Action[AnyContent] = (authenticate andThen pendingDeregCheck).async { implicit user =>
    issueNewInvoiceAnswerService.getAnswer map {
      case Right(Some(data)) => Ok(renderView(YesNoForm.yesNoForm.fill(data)))
      case _ => Ok(renderView())
    }
  }

  val submit: Action[AnyContent] = authenticate.async { implicit user =>
    YesNoForm.yesNoForm.bindFromRequest().fold(
      error => Future.successful(BadRequest(views.html.issueNewInvoices(error))),
      data => (for {
        _ <- EitherT(issueNewInvoiceAnswerService.storeAnswer(data))
        _ <- EitherT(wipeRedundantDataService.wipeRedundantData)
        result = redirect(data)
      } yield result).value.map {
        case Right(redirect) => redirect
        case Left(_) => serviceErrorHandler.showInternalServerError
      }
    )
  }
}
