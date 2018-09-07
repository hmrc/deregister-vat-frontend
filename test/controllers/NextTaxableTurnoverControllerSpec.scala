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

import models.{DeregisterVatSuccess, TaxableTurnoverModel}
import play.api.http.Status
import play.api.mvc.AnyContentAsFormUrlEncoded
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.mocks.MockNextTaxableTurnoverAnswerService
import assets.constants.BaseTestConstants._

import scala.concurrent.Future

class NextTaxableTurnoverControllerSpec extends ControllerBaseSpec with MockNextTaxableTurnoverAnswerService {

  object TestNextTaxableTurnoverController extends NextTaxableTurnoverController(
    messagesApi, mockAuthPredicate, mockStoredAnswersService, mockConfig
  )

  val testTurnoverAmt = 500
  val testTurnoverModel = TaxableTurnoverModel(testTurnoverAmt)

  "the user is authorised" when {

    "Calling the .show action" when {

      "the user does not have a pre selected amount" should {

        lazy val result = TestNextTaxableTurnoverController.show()(request)

        "return 200 (OK)" in {
          setupMockGetAnswers(Right(None))
          mockAuthResult(Future.successful(mockAuthorisedIndividual))
          status(result) shouldBe Status.OK
        }

        "return HTML" in {
          contentType(result) shouldBe Some("text/html")
          charset(result) shouldBe Some("utf-8")
        }
      }

      "the user is has pre selected amount" should {

        lazy val result = TestNextTaxableTurnoverController.show()(request)

        "return 200 (OK)" in {
          setupMockGetAnswers(Right(Some(testTurnoverModel)))
          mockAuthResult(Future.successful(mockAuthorisedIndividual))
          status(result) shouldBe Status.OK
        }

        "return HTML" in {
          contentType(result) shouldBe Some("text/html")
          charset(result) shouldBe Some("utf-8")
        }

        "have the amount pre-populate in the field" in {
          document(result).select("#turnover").attr("value") shouldBe testTurnoverAmt.toString
        }
      }

      authChecks(".show", TestNextTaxableTurnoverController.show(), request)
    }

    "Calling the .submit action" when {

      "the user submits after inputting an amount that is equal to the threshold" should {

        val testTurnoverAmt = mockConfig.deregThreshold

        lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
          FakeRequest("POST", "/").withFormUrlEncodedBody(("turnover", testTurnoverAmt.toString))
        lazy val result = TestNextTaxableTurnoverController.submit()(request)

        "return 303 (SEE OTHER)" in {
          setupMockStoreAnswers(TaxableTurnoverModel(testTurnoverAmt))(Right(DeregisterVatSuccess))
          mockAuthResult(Future.successful(mockAuthorisedIndividual))
          status(result) shouldBe Status.SEE_OTHER
        }

        s"Redirect to the '${controllers.routes.WhyTurnoverBelowController.show().url}'" in {
          redirectLocation(result) shouldBe Some(controllers.routes.WhyTurnoverBelowController.show().url)
        }
      }

      "the user submits after inputting an amount that is greater than the threshold" should {

        val testTurnoverAmt = mockConfig.deregThreshold + 0.01

        lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
          FakeRequest("POST", "/").withFormUrlEncodedBody(("turnover", testTurnoverAmt.toString))
        lazy val result = TestNextTaxableTurnoverController.submit()(request)

        "return 303 (SEE OTHER)" in {
          setupMockStoreAnswers(TaxableTurnoverModel(testTurnoverAmt))(Right(DeregisterVatSuccess))
          mockAuthResult(Future.successful(mockAuthorisedIndividual))
          status(result) shouldBe Status.SEE_OTHER
        }

        s"Redirect to the '${controllers.routes.CannotDeregisterThresholdController.show().url}'" in {
          redirectLocation(result) shouldBe Some(controllers.routes.CannotDeregisterThresholdController.show().url)
        }
      }

      "the user submits after inputting an amount that is less than the threshold" should {

        val testTurnoverAmt = mockConfig.deregThreshold - 0.01

        lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
          FakeRequest("POST", "/").withFormUrlEncodedBody(("turnover", testTurnoverAmt.toString))
        lazy val result = TestNextTaxableTurnoverController.submit()(request)

        "return 303 (SEE OTHER)" in {
          setupMockStoreAnswers(TaxableTurnoverModel(testTurnoverAmt))(Right(DeregisterVatSuccess))
          mockAuthResult(Future.successful(mockAuthorisedIndividual))
          status(result) shouldBe Status.SEE_OTHER
        }

        s"Redirect to the '${controllers.routes.WhyTurnoverBelowController.show().url}'" in {
          redirectLocation(result) shouldBe Some(controllers.routes.WhyTurnoverBelowController.show().url)
        }
      }

      "the user submits after inputting an amount and an error is returned when storing" should {

        lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
          FakeRequest("POST", "/").withFormUrlEncodedBody(("turnover", testTurnoverAmt.toString))
        lazy val result = TestNextTaxableTurnoverController.submit()(request)

        "return 500 (ISE)" in {
          setupMockStoreAnswers(TaxableTurnoverModel(testTurnoverAmt))(Left(errorModel))
          mockAuthResult(Future.successful(mockAuthorisedIndividual))
          status(result) shouldBe Status.INTERNAL_SERVER_ERROR
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
