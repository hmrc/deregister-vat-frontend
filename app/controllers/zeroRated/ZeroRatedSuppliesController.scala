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

import config.{AppConfig, ServiceErrorHandler}
import controllers.predicates.{AuthPredicate, PendingChangesPredicate}
import javax.inject.{Inject, Singleton}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import uk.gov.hmrc.play.bootstrap.controller.FrontendController

@Singleton
class ZeroRatedSuppliesController @Inject()(val messagesApi: MessagesApi,
                                            val authenticate: AuthPredicate,
                                            val pendingDeregCheck: PendingChangesPredicate,
                                            val serviceErrorHandler: ServiceErrorHandler,
                                            implicit val appConfig: AppConfig) extends FrontendController with I18nSupport {

  val show: Action[AnyContent] = (authenticate andThen pendingDeregCheck) { implicit user =>
    if (appConfig.features.zeroRatedJourney()) {
      Ok("")
    } else {
      serviceErrorHandler.showBadRequestError
    }
  }

  val submit: Action[AnyContent] = authenticate { implicit user =>
    if (appConfig.features.zeroRatedJourney()) {
      Ok("")
    } else {
      serviceErrorHandler.showBadRequestError
    }
  }
}

