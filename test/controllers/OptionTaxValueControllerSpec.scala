/*
 * Copyright 2026 HM Revenue & Customs
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

import assets.constants.BaseTestConstants.errorModel
import models.{DeregisterVatSuccess, NumberInputModel}
import play.api.http.Status
import play.api.mvc.AnyContentAsFormUrlEncoded
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.mocks.MockOptionTaxValueAnswerService
import views.html.OptionTaxValue

class OptionTaxValueControllerSpec extends ControllerBaseSpec with MockOptionTaxValueAnswerService {

  lazy val optionTaxValue: OptionTaxValue = injector.instanceOf[OptionTaxValue]

  object TestOptionTaxValueController extends OptionTaxValueController(
    optionTaxValue,
    mcc,
    mockAuthPredicate,
    mockRegistrationStatusPredicate,
    mockOptionTaxValueAnswerService,
    serviceErrorHandler,
    ec,
    mockConfig
  )

  val testAmt = 500

  "show" should {
    "return 200(OK)" when {
      "the user does not have a pre-selected amount" in {
        lazy val result = TestOptionTaxValueController.show(request)
        mockAuthResult(mockAuthorisedIndividual)
        setupMockGetOptionTaxValue(Right(None))
        status(result) shouldBe Status.OK
        contentType(result) shouldBe Some("text/html")
        charset(result) shouldBe Some("utf-8")
        document(result).select("#amount").attr("value") shouldBe ""
      }

      "the user has pre-selected amount" in {
        lazy val result = TestOptionTaxValueController.show(request)
        mockAuthResult(mockAuthorisedIndividual)
        setupMockGetOptionTaxValue(Right(Some(NumberInputModel(testAmt))))
        status(result) shouldBe Status.OK
        contentType(result) shouldBe Some("text/html")
        charset(result) shouldBe Some("utf-8")
        document(result).select("#amount").attr("value") shouldBe testAmt.toString      }
    }
  }

  "submit" should {
    "return 303 (SEE OTHER) and redirect to the correct URL" when {
      "the user inserted an amount" in {
        lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
          requestPost.withFormUrlEncodedBody(("amount", testAmt.toString))

        lazy val result = TestOptionTaxValueController.submit(request)
        mockAuthResult(mockAuthorisedIndividual)
        setupMockStoreOptionTaxValue(NumberInputModel(testAmt))(Right(DeregisterVatSuccess))
        status(result) shouldBe Status.SEE_OTHER
        redirectLocation(result) shouldBe Some(controllers.routes.CapitalAssetsController.show.url)
      }
    }

    "return 400 (BAD_REQUEST)" when {
      "the user did not inserted any amount" in {
        lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
          requestPost.withFormUrlEncodedBody(("amount", ""))

        lazy val result = TestOptionTaxValueController.submit(request)
        mockAuthResult(mockAuthorisedIndividual)
        status(result) shouldBe Status.BAD_REQUEST
      }

      "the user did not inserted a negative amount" in {
        lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
          requestPost.withFormUrlEncodedBody(("amount", "-1"))
        lazy val result = TestOptionTaxValueController.submit(request)
        mockAuthResult(mockAuthorisedIndividual)
        status(result) shouldBe Status.BAD_REQUEST
      }

      "the user did not inserted a non numeric value" in {
        lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
          requestPost.withFormUrlEncodedBody(("amount", "abcd"))
        lazy val result = TestOptionTaxValueController.submit(request)
        mockAuthResult(mockAuthorisedIndividual)
        status(result) shouldBe Status.BAD_REQUEST
      }

    }

    "return 500 (INTERNAL_SERVER_ERROR)" when {
      "an exception is thrown" in {
        lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] = requestPost.withFormUrlEncodedBody(("amount", testAmt.toString))
        lazy val result = TestOptionTaxValueController.submit(request)
        mockAuthResult(mockAuthorisedIndividual)
        setupMockStoreOptionTaxValue(NumberInputModel(testAmt))(Left(errorModel))
        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
      }
    }
  }

}
