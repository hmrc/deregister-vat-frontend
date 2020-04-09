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

package controllers

import assets.constants.BaseTestConstants.vrn
import common.{Constants, SessionKeys}
import controllers.predicates.RegistrationStatusPredicate
import play.api.http.Status
import play.api.test.Helpers._
import services.CustomerDetailsService

import scala.concurrent.Future

class DeregisterForVATControllerSpec extends ControllerBaseSpec {

  val mockPendingDereg = new RegistrationStatusPredicate(
    new CustomerDetailsService(mockVatSubscriptionConnector),
    serviceErrorHandler,
    messagesApi,
    mockConfig,
    ec
  )

  object TestDeregisterForVATController extends DeregisterForVATController(
    messagesApi,
    mockAuthPredicate,
    mockPendingDereg,
    mockConfig
  )

  "the user is authorised" when {

    "Calling the .redirect action with an agent" should {
      "redirect to .../agent" should {
        lazy val result = {
          mockAuthResult(Future.successful(mockAuthorisedAgent), isAgent = true)
          TestDeregisterForVATController.redirect()(request.withSession(
            SessionKeys.registrationStatusKey -> Constants.registered,
            SessionKeys.CLIENT_VRN -> vrn
          ))
        }

        "return a 303" in {
          status(result) shouldBe Status.SEE_OTHER
        }

        "redirect to the correct URL" in {
          redirectLocation(result) shouldBe Some("/vat-through-software/account/cancel-vat/agent")
        }
      }
    }

    "Calling the .redirect action with a user" should {
      "redirect to .../non-agent" should {
        lazy val result = {
          mockAuthResult(Future.successful(mockAuthorisedIndividual))
          TestDeregisterForVATController.redirect()(request.withSession(
            SessionKeys.registrationStatusKey -> Constants.registered
          ))
        }

        "return a 303" in {
          status(result) shouldBe Status.SEE_OTHER
        }

        "redirect to the correct url" in {
          redirectLocation(result) shouldBe Some("/vat-through-software/account/cancel-vat/non-agent")
        }
      }
    }

    "Calling the .show action with agent" should {

      lazy val result = TestDeregisterForVATController.show("agent")(request.withSession(
        SessionKeys.registrationStatusKey -> Constants.registered
      ))

      "return 200 (OK)" in {
        mockAuthResult(Future.successful(mockAuthorisedIndividual))
        status(result) shouldBe Status.OK
      }

      "return HTML" in {
        contentType(result) shouldBe Some("text/html")
        charset(result) shouldBe Some("utf-8")
      }

    }

    "Calling the .show action with non-agent" should {

      lazy val result = TestDeregisterForVATController.show("non-agent")(request.withSession(
        SessionKeys.registrationStatusKey -> Constants.registered
      ))

      "return 200 (OK)" in {
        mockAuthResult(Future.successful(mockAuthorisedIndividual))
        status(result) shouldBe Status.OK
      }

      "return HTML" in {
        contentType(result) shouldBe Some("text/html")
        charset(result) shouldBe Some("utf-8")
      }

    }
  }

}
