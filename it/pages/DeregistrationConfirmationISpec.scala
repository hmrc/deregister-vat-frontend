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

package pages

import common.SessionKeys
import helpers.IntegrationBaseSpec
import play.api.libs.ws.WSResponse
import assets.IntegrationTestConstants.vrn
import config.FrontendAppConfig
import play.api.i18n.Messages
import play.api.libs.json.Json
import stubs.{ContactPreferencesStub, DeregisterVatStub, VatSubscriptionStub}
import play.api.test.Helpers.OK

class DeregistrationConfirmationISpec extends IntegrationBaseSpec {

  val session: Map[String, String] = Map(SessionKeys.CLIENT_VRN -> vrn, SessionKeys.deregSuccessful -> "true")
  lazy val mockAppConfig: FrontendAppConfig = app.injector.instanceOf[FrontendAppConfig]

  override def beforeEach() {
    super.beforeEach()
  }

  override def afterEach() {
    super.afterEach()
  }

  "Calling the DeregistrationConfirmationController.show" when {

    def deregConfirmationRequest(): WSResponse = get("/cancel-vat-request-received", session)

    "the user is authorised" should {

      "return 200 OK" in {

        given.user.isAuthorised

        DeregisterVatStub.successfulDeleteAllAnswers(vrn)
        VatSubscriptionStub.noPendingData()

        val res: WSResponse = deregConfirmationRequest()

        res should have(
          httpStatus(OK),
          elementText("#preference-message")(Messages("deregistrationConfirmation.digitalPreference"))
        )
      }
    }
  }
}
