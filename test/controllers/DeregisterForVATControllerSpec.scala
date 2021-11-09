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

package controllers

import assets.constants.BaseTestConstants.vrn
import common.{Constants, SessionKeys}
import controllers.predicates.DeniedAccessPredicate
import play.api.http.Status
import play.api.test.Helpers._
import services.CustomerDetailsService
import views.html.DeregisterForVAT

class DeregisterForVATControllerSpec extends ControllerBaseSpec {


  lazy val deregisterForVAT: DeregisterForVAT = injector.instanceOf[DeregisterForVAT]

  val mockPendingDereg = new DeniedAccessPredicate(
    new CustomerDetailsService(mockVatSubscriptionConnector),
    serviceErrorHandler,
    mcc,
    messagesApi,
    mockConfig
  )

  object TestDeregisterForVATController extends DeregisterForVATController(
    deregisterForVAT,
    mcc,
    mockAuthPredicate,
    mockPendingDereg,
    mockConfig
  )

  "the user is authorised" when {

    "Calling the .redirect action with an agent" should {
      "redirect to .../agent" should {
        lazy val result = {
          mockAuthResult(mockAuthorisedAgent, isAgent = true)
          TestDeregisterForVATController.show()(request.withSession(
            SessionKeys.registrationStatusKey -> Constants.registered,
            SessionKeys.CLIENT_VRN -> vrn
          ))
        }

        "return a 200" in {
          status(result) shouldBe Status.OK
        }
      }
    }

    "Calling the .redirect action with a user" should {
      "redirect to .../non-agent" should {
        lazy val result = {
          mockAuthResult(mockAuthorisedIndividual)
          TestDeregisterForVATController.show()(request.withSession(
            SessionKeys.registrationStatusKey -> Constants.registered
          ))
        }

        "return a 200" in {
          status(result) shouldBe Status.OK
        }
      }
    }

    "Calling the .show action with agent" should {

      lazy val result = TestDeregisterForVATController.show()(request.withSession(
        SessionKeys.registrationStatusKey -> Constants.registered
      ))

      "return 200 (OK)" in {
        mockAuthResult(mockAuthorisedIndividual)
        status(result) shouldBe Status.OK
      }

      "return HTML" in {
        contentType(result) shouldBe Some("text/html")
        charset(result) shouldBe Some("utf-8")
      }

    }

    "Calling the .show action with non-agent" should {

      lazy val result = TestDeregisterForVATController.show()(request.withSession(
        SessionKeys.registrationStatusKey -> Constants.registered
      ))

      "return 200 (OK)" in {
        mockAuthResult(mockAuthorisedIndividual)
        status(result) shouldBe Status.OK
      }

      "return HTML" in {
        contentType(result) shouldBe Some("text/html")
        charset(result) shouldBe Some("utf-8")
      }

    }
  }

}
