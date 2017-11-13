/*
 * Copyright 2017 HM Revenue & Customs
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

package controllers.auth.actions

import config.features.SimpleAuthFeature
import controllers.auth.{AuthPredicates, AuthorisedActions}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController

trait VatUserAction extends AuthorisedActions {
  self: FrontendController =>

  def simpleAuthFeature: SimpleAuthFeature

  object VatUserAction {
    def async: AuthenticatedAction = {
      if(simpleAuthFeature.enabled) {
        println(">>>>>>>>>>>>> Simple Auth")
        action(AuthPredicates.timeoutPredicate)
      } else {
        println(">>>>>>>>>>>>> Really Complicated Auth that is hard to understand no really it is hard")
        action(AuthPredicates.enrolledUserPredicate)
      }
    }
  }
}
