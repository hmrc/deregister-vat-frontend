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

import assets.constants.BaseTestConstants._
import models._
import play.api.http.Status
import play.api.mvc.AnyContentAsFormUrlEncoded
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.mocks.{MockBusinessActivityAnswerService, MockDeregReasonAnswerService, MockNextTaxableTurnoverAnswerService, MockTaxableTurnoverAnswerService}

import scala.concurrent.Future

class NextTaxableTurnoverControllerSpec extends ControllerBaseSpec
  with MockTaxableTurnoverAnswerService
  with MockNextTaxableTurnoverAnswerService
  with MockBusinessActivityAnswerService
  with MockDeregReasonAnswerService {

  object TestNextTaxableTurnoverController extends NextTaxableTurnoverController(
    messagesApi,
    mockAuthPredicate,
    mockPendingDeregPredicate,
    mockTaxableTurnoverAnswerService,
    mockBusinessActivityAnswerService,
    mockDeregReasonAnswerService,
    mockNextTaxableTurnoverAnswerService,
    serviceErrorHandler,
    mockConfig
  )

  val testTurnoverAmt = 500
  val testTurnoverModel = NumberInputModel(testTurnoverAmt)

  "the user is authorised" when {

    "Calling the .show action" when {

      "the user does not have a pre selected amount" should {

        lazy val result = TestNextTaxableTurnoverController.show()(request)

        "return 200 (OK)" in {
          setupMockGetNextTaxableTurnover(Right(None))
          setupMockGetBusinessActivity(Right(None))
          setupMockGetDeregReason(Right(Some(BelowThreshold)))
          mockAuthResult(Future.successful(mockAuthorisedIndividual))
          status(result) shouldBe Status.OK
        }

        "return HTML" in {
          contentType(result) shouldBe Some("text/html")
          charset(result) shouldBe Some("utf-8")
        }
      }

      "the user has pre selected amount" should {

        lazy val result = TestNextTaxableTurnoverController.show()(request)

        "return 200 (OK)" in {
          setupMockGetNextTaxableTurnover(Right(Some(testTurnoverModel)))
          setupMockGetBusinessActivity(Right(None))
          setupMockGetDeregReason(Right(Some(BelowThreshold)))
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

      "the user has pre selected amount and the use also has value saved for the business activity page of yes" should {

        lazy val result = TestNextTaxableTurnoverController.show()(request)

        "return 200 (OK)" in {
          setupMockGetNextTaxableTurnover(Right(Some(testTurnoverModel)))
          setupMockGetBusinessActivity(Right(Some(Yes)))
          setupMockGetDeregReason(Right(Some(ZeroRated)))
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
        s"have the correct back link of ${controllers.zeroRated.routes.SicCodeController.show().url}" in {
          document(result).select(".link-back").attr("href") shouldBe controllers.zeroRated.routes.SicCodeController.show().url
        }
      }

      "the user has pre selected amount and the use also has value saved for the business activity page of no" should {

        lazy val result = TestNextTaxableTurnoverController.show()(request)

        "return 200 (OK)" in {
          setupMockGetNextTaxableTurnover(Right(Some(testTurnoverModel)))
          setupMockGetBusinessActivity(Right(Some(No)))
          setupMockGetDeregReason(Right(Some(ZeroRated)))
          mockAuthResult(Future.successful(mockAuthorisedIndividual))
          status(result) shouldBe Status.OK
        }

        "return HTML" in {
          contentType(result) shouldBe Some("text/html")
          charset(result) shouldBe Some("utf-8")
        }

        "have the amount pre-populate in the field" in {
          document(result).select("#value").attr("value") shouldBe testTurnoverAmt.toString
        }
        s"have the correct back link of ${controllers.zeroRated.routes.BusinessActivityController.show().url}" in {
          document(result).select(".link-back").attr("href") shouldBe controllers.zeroRated.routes.BusinessActivityController.show().url
        }
      }

      "the user has a value saved for the business activity page of no and is on the zero rated journey" should {

        lazy val result = TestNextTaxableTurnoverController.show()(request)

        "return 200 (OK)" in {
          setupMockGetNextTaxableTurnover(Right(None))
          setupMockGetBusinessActivity(Right(Some(No)))
          setupMockGetDeregReason(Right(Some(ZeroRated)))
          mockAuthResult(Future.successful(mockAuthorisedIndividual))
          status(result) shouldBe Status.OK
        }

        "return HTML" in {
          contentType(result) shouldBe Some("text/html")
          charset(result) shouldBe Some("utf-8")
        }

        "not have an amount pre-populate in the field" in {
          document(result).select("#turnover").attr("value") shouldBe ""
        }
        s"have the correct back link of ${controllers.zeroRated.routes.BusinessActivityController.show().url}" in {
          document(result).select(".link-back").attr("href") shouldBe controllers.zeroRated.routes.BusinessActivityController.show().url
        }
      }

      "the user has a value saved for the business activity page of yes and is on the zero rated journey" should {

        lazy val result = TestNextTaxableTurnoverController.show()(request)

        "return 200 (OK)" in {
          setupMockGetNextTaxableTurnover(Right(None))
          setupMockGetBusinessActivity(Right(Some(Yes)))
          setupMockGetDeregReason(Right(Some(ZeroRated)))
          mockAuthResult(Future.successful(mockAuthorisedIndividual))
          status(result) shouldBe Status.OK
        }

        "return HTML" in {
          contentType(result) shouldBe Some("text/html")
          charset(result) shouldBe Some("utf-8")
        }

        "not have an amount pre-populate in the field" in {
          document(result).select("#turnover").attr("value") shouldBe ""
        }
        s"have the correct back link of ${controllers.zeroRated.routes.SicCodeController.show().url}" in {
          document(result).select(".link-back").attr("href") shouldBe controllers.zeroRated.routes.SicCodeController.show().url
        }
      }

      "the call to the service returns a error model" should {

        lazy val result = TestNextTaxableTurnoverController.show()(request)

        "return 500 (ISE)" in {
          setupMockGetNextTaxableTurnover(Left(errorModel))
          setupMockGetBusinessActivity(Left(errorModel))
          setupMockGetDeregReason(Left(errorModel))
          mockAuthResult(Future.successful(mockAuthorisedIndividual))
          status(result) shouldBe Status.INTERNAL_SERVER_ERROR
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

        val testTurnoverAmt = mockConfig.deregThreshold

        lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
          FakeRequest("POST", "/").withFormUrlEncodedBody(("value", testTurnoverAmt.toString))
        lazy val result = TestNextTaxableTurnoverController.submit()(request)

        "return 303 (SEE OTHER)" in {
          setupMockGetTaxableTurnover(Right(Some(No)))

          setupMockGetDeregReason(Right(Some(BelowThreshold)))

          setupMockStoreNextTaxableTurnover(NumberInputModel(testTurnoverAmt))(Right(DeregisterVatSuccess))

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
          FakeRequest("POST", "/").withFormUrlEncodedBody(("value", testTurnoverAmt.toString))
        lazy val result = TestNextTaxableTurnoverController.submit()(request)

        "return 303 (SEE OTHER)" in {
          setupMockGetTaxableTurnover(Right(Some(Yes)))
          setupMockGetDeregReason(Right(Some(BelowThreshold)))
          setupMockStoreNextTaxableTurnover(NumberInputModel(testTurnoverAmt))(Right(DeregisterVatSuccess))

          mockAuthResult(Future.successful(mockAuthorisedIndividual))
          status(result) shouldBe Status.SEE_OTHER
        }

        s"Redirect to the '${controllers.routes.CannotDeregisterThresholdController.show().url}'" in {
          redirectLocation(result) shouldBe Some(controllers.routes.CannotDeregisterThresholdController.show().url)
        }
      }

      "the user submits after inputting an amount that is less than the threshold and their last turnover is less than the threshold" should {

        val testTurnoverAmt = mockConfig.deregThreshold - 0.01

        lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
          FakeRequest("POST", "/").withFormUrlEncodedBody(("value", testTurnoverAmt.toString))
        lazy val result = TestNextTaxableTurnoverController.submit()(request)

        "return 303 (SEE OTHER)" in {
          setupMockGetTaxableTurnover(Right(Some(Yes)))
          setupMockGetDeregReason(Right(Some(BelowThreshold)))
          setupMockStoreNextTaxableTurnover(NumberInputModel(testTurnoverAmt))(Right(DeregisterVatSuccess))
          mockAuthResult(Future.successful(mockAuthorisedIndividual))
          status(result) shouldBe Status.SEE_OTHER
        }

        s"Redirect to the '${controllers.routes.WhyTurnoverBelowController.show().url}'" in {
          redirectLocation(result) shouldBe Some(controllers.routes.VATAccountsController.show().url)
        }
      }

      "the user submits after inputting an amount that is less than the threshold and their last turnover is more than the threshold" should {

        val testTurnoverAmt = mockConfig.deregThreshold - 0.01

        lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
          FakeRequest("POST", "/").withFormUrlEncodedBody(("value", testTurnoverAmt.toString))
        lazy val result = TestNextTaxableTurnoverController.submit()(request)

        "return 303 (SEE OTHER)" in {
          setupMockGetDeregReason(Right(Some(BelowThreshold)))
          setupMockGetTaxableTurnover(Right(Some(No)))
          setupMockStoreNextTaxableTurnover(NumberInputModel(testTurnoverAmt))(Right(DeregisterVatSuccess))
          mockAuthResult(Future.successful(mockAuthorisedIndividual))
          status(result) shouldBe Status.SEE_OTHER
        }

        s"Redirect to the '${controllers.routes.WhyTurnoverBelowController.show().url}'" in {
          redirectLocation(result) shouldBe Some(controllers.routes.WhyTurnoverBelowController.show().url)
        }
      }

      "the user submits after selecting zero rated for the deregistration reason" should {

        val testTurnoverAmt = mockConfig.deregThreshold - 0.01

        lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
          FakeRequest("POST", "/").withFormUrlEncodedBody(("value", testTurnoverAmt.toString))
        lazy val result = TestNextTaxableTurnoverController.submit()(request)

        "return 303 (SEE OTHER)" in {
          setupMockGetDeregReason(Right(Some(ZeroRated)))
          setupMockGetTaxableTurnover(Right(Some(No)))
          setupMockStoreNextTaxableTurnover(NumberInputModel(testTurnoverAmt))(Right(DeregisterVatSuccess))
          mockAuthResult(Future.successful(mockAuthorisedIndividual))
          status(result) shouldBe Status.SEE_OTHER
        }

        s"Redirect to the '${controllers.zeroRated.routes.ZeroRatedSuppliesController.show().url}'" in {
          redirectLocation(result) shouldBe Some(controllers.zeroRated.routes.ZeroRatedSuppliesController.show().url)
        }
      }

      "the user submits after selecting zero rated for the deregistration reason and taxable turnover value is missing" should {

        val testTurnoverAmt = mockConfig.deregThreshold - 0.01

        lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
          FakeRequest("POST", "/").withFormUrlEncodedBody(("value", testTurnoverAmt.toString))
        lazy val result = TestNextTaxableTurnoverController.submit()(request)

        "return 303 (SEE OTHER)" in {
          setupMockGetTaxableTurnover(Right(None))
          setupMockGetDeregReason(Right(Some(ZeroRated)))
          setupMockStoreNextTaxableTurnover(NumberInputModel(testTurnoverAmt))(Right(DeregisterVatSuccess))
          mockAuthResult(Future.successful(mockAuthorisedIndividual))
          status(result) shouldBe Status.SEE_OTHER
        }

        s"Redirect to the '${controllers.zeroRated.routes.ZeroRatedSuppliesController.show().url}'" in {
          redirectLocation(result) shouldBe Some(controllers.zeroRated.routes.ZeroRatedSuppliesController.show().url)
        }
      }

      "the user submits after inputting an amount and an error is returned when storing" should {

        lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
          FakeRequest("POST", "/").withFormUrlEncodedBody(("value", testTurnoverAmt.toString))
        lazy val result = TestNextTaxableTurnoverController.submit()(request)

        "return 500 (ISE)" in {
          setupMockStoreNextTaxableTurnover(NumberInputModel(testTurnoverAmt))(Left(errorModel))
          mockAuthResult(Future.successful(mockAuthorisedIndividual))
          status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        }
      }

      "the user submits without inputting an amount and are on the below threshold journey" should {

        lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
          FakeRequest("POST", "/").withFormUrlEncodedBody(("value", ""))
        lazy val result = TestNextTaxableTurnoverController.submit()(request)

        "return 400 (BAD REQUEST)" in {
          setupMockGetDeregReason(Right(Some(BelowThreshold)))
          setupMockGetBusinessActivity(Right(None))
          mockAuthResult(Future.successful(mockAuthorisedIndividual))
          status(result) shouldBe Status.BAD_REQUEST
        }

        "return HTML" in {
          contentType(result) shouldBe Some("text/html")
          charset(result) shouldBe Some("utf-8")
        }

      }

      "the user submits without inputting an amount and are on the zero rated journey and BusinessActivity is no" should {

        lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
          FakeRequest("POST", "/").withFormUrlEncodedBody(("turnover", ""))
        lazy val result = TestNextTaxableTurnoverController.submit()(request)

        "return 400 (BAD REQUEST)" in {
          setupMockGetDeregReason(Right(Some(ZeroRated)))
          setupMockGetBusinessActivity(Right(Some(No)))
          mockAuthResult(Future.successful(mockAuthorisedIndividual))
          status(result) shouldBe Status.BAD_REQUEST
        }

        "return HTML" in {
          contentType(result) shouldBe Some("text/html")
          charset(result) shouldBe Some("utf-8")
        }

        "display the back url" in {
          document(result).getElementsByClass("link-back").attr("href") shouldBe controllers.zeroRated.routes.BusinessActivityController.show().url
        }
      }

      "the user submits without inputting an amount and are on the zero rated journey and BusinessActivity is yes" should {

        lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
          FakeRequest("POST", "/").withFormUrlEncodedBody(("turnover", ""))
        lazy val result = TestNextTaxableTurnoverController.submit()(request)

        "return 400 (BAD REQUEST)" in {
          setupMockGetDeregReason(Right(Some(ZeroRated)))
          setupMockGetBusinessActivity(Right(Some(Yes)))
          mockAuthResult(Future.successful(mockAuthorisedIndividual))
          status(result) shouldBe Status.BAD_REQUEST
        }

        "return HTML" in {
          contentType(result) shouldBe Some("text/html")
          charset(result) shouldBe Some("utf-8")
        }

        "display the back url" in {
          document(result).getElementsByClass("link-back").attr("href") shouldBe controllers.zeroRated.routes.SicCodeController.show().url
        }
      }

      "the user submits without inputting an amount and an error model is returned from the database" should {

        lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
          FakeRequest("POST", "/").withFormUrlEncodedBody(("turnover", ""))
        lazy val result = TestNextTaxableTurnoverController.submit()(request)

        "return 500 (ISE)" in {
          setupMockGetDeregReason(Left(errorModel))
          setupMockGetBusinessActivity(Left(errorModel))
          mockAuthResult(Future.successful(mockAuthorisedIndividual))
          status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        }

        "return HTML" in {
          contentType(result) shouldBe Some("text/html")
          charset(result) shouldBe Some("utf-8")
        }
      }


      "the user submits after selecting the 'No' option but an error is returned when storing" should {

        lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
          FakeRequest("POST", "/").withFormUrlEncodedBody(("turnover", testTurnoverAmt.toString))
        lazy val result = TestNextTaxableTurnoverController.submit()(request)

        "return 500 (ISE)" in {
          setupMockStoreNextTaxableTurnover(NumberInputModel(testTurnoverAmt))(Left(errorModel))
          mockAuthResult(Future.successful(mockAuthorisedIndividual))
          status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        }
      }
    }

    authChecks(".submit", TestNextTaxableTurnoverController.submit(), FakeRequest("POST", "/").withFormUrlEncodedBody(("value", "1000.01")))

    "the backLink when businessActivityAnswerService return a Some(Yes)" should {

      lazy val result = TestNextTaxableTurnoverController.backLink(Some(Yes))

      s"return the correct back link of ${controllers.zeroRated.routes.SicCodeController.show().url}" in {
        result shouldBe controllers.zeroRated.routes.SicCodeController.show().url
      }
    }

    "the backLink when businessActivityAnswerService return a Some(No)" should {

      lazy val result = TestNextTaxableTurnoverController.backLink(Some(No))

      s"return the correct back link of ${controllers.zeroRated.routes.BusinessActivityController.show().url}" in {
        result shouldBe controllers.zeroRated.routes.BusinessActivityController.show().url
      }
    }

    "the backLink when businessActivityAnswerService return a None" should {

      lazy val result = TestNextTaxableTurnoverController.backLink(None)

      s"return the correct back link of ${controllers.routes.TaxableTurnoverController.show().url}" in {
        result shouldBe controllers.routes.TaxableTurnoverController.show().url
      }
    }


    authChecks(".submit", TestNextTaxableTurnoverController.submit(), FakeRequest("POST", "/").withFormUrlEncodedBody(("turnover", "1000.01")))
  }

}
