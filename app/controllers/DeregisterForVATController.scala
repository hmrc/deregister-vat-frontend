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

import config.AppConfig
import controllers.predicates.{AuthPredicate, DeniedAccessPredicate}
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.DeregisterForVAT
import javax.inject.{Inject, Singleton}
import utils.LoggerUtil

import scala.concurrent.Future

@Singleton
class DeregisterForVATController @Inject()(deregisterForVAT: DeregisterForVAT,
                                            val mcc: MessagesControllerComponents,
                                           val authenticate: AuthPredicate,
                                           val regStatusCheck: DeniedAccessPredicate,
                                           implicit val appConfig: AppConfig) extends FrontendController(mcc) with I18nSupport with LoggerUtil {

  val show: Action[AnyContent] = (authenticate andThen regStatusCheck).async { implicit user =>
    Future.successful(Ok(deregisterForVAT()))
  }

}
