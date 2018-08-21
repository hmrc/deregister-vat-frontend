/*
 * Copyright 2018 HM Revenue & Customs
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

import play.api.http.Status
import play.api.mvc.AnyContentAsFormUrlEncoded
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.concurrent.Future

class NextTaxableTurnoverControllerSpec extends ControllerBaseSpec {

  object TestNextTaxableTurnoverController extends NextTaxableTurnoverController(messagesApi, mockAuthPredicate, mockConfig)

  "the user is authorised" when {

    "Calling the .show action" when {

      "the user does not have a pre selected amount" should {

        lazy val result = TestNextTaxableTurnoverController.show()(request)

        "return 200 (OK)" in {
          mockAuthResult(Future.successful(mockAuthorisedIndividual))
          status(result) shouldBe Status.OK
        }

        "return HTML" in {
          contentType(result) shouldBe Some("text/html")
          charset(result) shouldBe Some("utf-8")
        }
      }

      //TODO - update once stored data can be retrieved from Mongo
      "the user is has pre selected amount" should {

        lazy val result = TestNextTaxableTurnoverController.show()(request)

        "return 200 (OK)" in {
          mockAuthResult(Future.successful(mockAuthorisedIndividual))
          status(result) shouldBe Status.OK
        }

        "return HTML" in {
          contentType(result) shouldBe Some("text/html")
          charset(result) shouldBe Some("utf-8")
        }
      }

      authChecks(".show", TestNextTaxableTurnoverController.show(), request)
    }

    "Calling the .submit action" when {

      "the user submits after inputting an amount that is equal to the threshold" should {

        lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
          FakeRequest("POST", "/").withFormUrlEncodedBody(("turnover", mockConfig.deregThreshold.toString))
        lazy val result = TestNextTaxableTurnoverController.submit()(request)

        "return 303 (SEE OTHER)" in {
          mockAuthResult(Future.successful(mockAuthorisedIndividual))
          status(result) shouldBe Status.SEE_OTHER
        }

        s"Redirect to the '${controllers.routes.WhyTurnoverBelowController.show().url}'" in {
          redirectLocation(result) shouldBe Some(controllers.routes.WhyTurnoverBelowController.show().url)
        }
      }

      "the user submits after inputting an amount that is greater than the threshold" should {

        lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
          FakeRequest("POST", "/").withFormUrlEncodedBody(("turnover", (mockConfig.deregThreshold + 0.01).toString))
        lazy val result = TestNextTaxableTurnoverController.submit()(request)

        "return 303 (SEE OTHER)" in {
          mockAuthResult(Future.successful(mockAuthorisedIndividual))
          status(result) shouldBe Status.SEE_OTHER
        }

        s"Redirect to the '${controllers.routes.CannotDeregisterThresholdController.show().url}'" in {
          redirectLocation(result) shouldBe Some(controllers.routes.CannotDeregisterThresholdController.show().url)
        }
      }

      "the user submits after inputting an amount that is less than the threshold" should {

        lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
          FakeRequest("POST", "/").withFormUrlEncodedBody(("turnover", (mockConfig.deregThreshold - 0.01).toString))
        lazy val result = TestNextTaxableTurnoverController.submit()(request)

        "return 303 (SEE OTHER)" in {
          mockAuthResult(Future.successful(mockAuthorisedIndividual))
          status(result) shouldBe Status.SEE_OTHER
        }

        s"Redirect to the '${controllers.routes.WhyTurnoverBelowController.show().url}'" in {
          redirectLocation(result) shouldBe Some(controllers.routes.WhyTurnoverBelowController.show().url)
        }
      }

      "the user submits without inputting an amount" should {

        lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
          FakeRequest("POST", "/").withFormUrlEncodedBody(("turnover", ""))
        lazy val result = TestNextTaxableTurnoverController.submit()(request)

        "return 400 (BAD REQUEST)" in {
          mockAuthResult(Future.successful(mockAuthorisedIndividual))
          status(result) shouldBe Status.BAD_REQUEST
        }

        "return HTML" in {
          contentType(result) shouldBe Some("text/html")
          charset(result) shouldBe Some("utf-8")
        }
      }
    }

    authChecks(".submit", TestNextTaxableTurnoverController.submit(), FakeRequest("POST", "/").withFormUrlEncodedBody(("turnover", "1000.01")))
  }

}
