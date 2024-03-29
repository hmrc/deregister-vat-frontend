/*
 * Copyright 2024 HM Revenue & Customs
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

import controllers.ControllerBaseSpec
import models.{DeregisterVatSuccess, ErrorModel, No, Yes}
import play.api.http.Status
import play.api.mvc.AnyContentAsFormUrlEncoded
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.mocks.{MockBusinessActivityAnswerService, MockDeleteAllStoredAnswersService, MockSicCodeAnswerService}
import views.html.SicCode

class SicCodeControllerSpec extends ControllerBaseSpec with MockDeleteAllStoredAnswersService
                            with MockSicCodeAnswerService with MockBusinessActivityAnswerService {

  lazy val sicCode: SicCode = injector.instanceOf[SicCode]

  object TestController extends SicCodeController(
    sicCode,
    mcc,
    mockAuthPredicate,
    mockRegistrationStatusPredicate,
    serviceErrorHandler,
    mockBusinessActivityAnswerService,
    mockSicCodeAnswerService,
    ec,
    mockConfig
  )

  "The SicCodeController" when {

    "calling .show method" when {

      "the user has a Yes value saved for business activity and has a SIC code value saved" should {
        lazy val result = TestController.show(request)

        "return a 200" in {
          mockAuthResult(mockAuthorisedIndividual)
          setupMockGetBusinessActivityAnswer(Right(Some(Yes)))
          setupMockGetSicCode(Right(Some("12345")))
          status(result) shouldBe Status.OK
        }

        "return HTML" in {
          contentType(result) shouldBe Some("text/html")
          charset(result) shouldBe Some("utf-8")
        }

        "has the value pre-populated" in {
          document(result).select(s"#value").attr("value") shouldBe "12345"
        }
      }

      "the user has Yes value saved for business activity and no SIC code value saved" should {
        lazy val result = TestController.show(request)

        "return a 200" in {
          mockAuthResult(mockAuthorisedIndividual)
          setupMockGetBusinessActivityAnswer(Right(Some(Yes)))
          setupMockGetSicCode(Right(None))
          status(result) shouldBe Status.OK
        }

        "return HTML" in {
          contentType(result) shouldBe Some("text/html")
          charset(result) shouldBe Some("utf-8")
        }
      }

      "the user doesn't have a value saved for businessActivity or SIC code" should {
        lazy val result = TestController.show(request)

        "return a 303" in {
          mockAuthResult(mockAuthorisedIndividual)
          setupMockGetBusinessActivityAnswer(Right(None))
          setupMockGetSicCode(Right(None))
          status(result) shouldBe Status.SEE_OTHER
        }

        s"redirect to ${controllers.zeroRated.routes.BusinessActivityController.show.url}" in {
          redirectLocation(result) shouldBe Some(controllers.zeroRated.routes.BusinessActivityController.show.url)
        }
      }

      "the user doesn't have a value saved for businessActivity but for a SIC code" should {
        lazy val result = TestController.show(request)

        "return a 303" in {
          mockAuthResult(mockAuthorisedIndividual)
          setupMockGetBusinessActivityAnswer(Right(None))
          setupMockGetSicCode(Right(Some("12345")))
          status(result) shouldBe Status.SEE_OTHER
        }

        s"redirect to ${controllers.zeroRated.routes.BusinessActivityController.show.url}" in {
          redirectLocation(result) shouldBe Some(controllers.zeroRated.routes.BusinessActivityController.show.url)
        }
      }

      "the user has 'No' saved for businessActivity and a SIC code value" should {
        lazy val result = TestController.show(request)

        "return a 303" in {
          mockAuthResult(mockAuthorisedIndividual)
          setupMockGetBusinessActivityAnswer(Right(Some(No)))
          setupMockGetSicCode(Right(Some("12345")))
          status(result) shouldBe Status.SEE_OTHER
        }

        s"redirect to ${controllers.routes.NextTaxableTurnoverController.show.url}" in {
          redirectLocation(result) shouldBe Some(controllers.routes.NextTaxableTurnoverController.show.url)
        }
      }

      "the user has 'No' saved for businessActivity and no SIC code value" should {
        lazy val result = TestController.show(request)

        "return a 303" in {
          mockAuthResult(mockAuthorisedIndividual)
          setupMockGetBusinessActivityAnswer(Right(Some(No)))
          setupMockGetSicCode(Right(None))
          status(result) shouldBe Status.SEE_OTHER
        }

        s"redirect to ${controllers.routes.NextTaxableTurnoverController.show.url}" in {
          redirectLocation(result) shouldBe Some(controllers.routes.NextTaxableTurnoverController.show.url)
        }
      }

      "the business activity answer service returns an error" should {
        lazy val result = TestController.show(request)

        "return an internal server error" in {
          mockAuthResult(mockAuthorisedIndividual)
          setupMockGetBusinessActivityAnswer(Left(ErrorModel(500, "Error")))
          setupMockGetSicCode(Right(None))
          status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        }
      }

      "the SIC code answer service returns an error" should {
        lazy val result = TestController.show(request)

        "return an internal server error" in {
          mockAuthResult(mockAuthorisedIndividual)
          setupMockGetBusinessActivityAnswer(Right(Some(Yes)))
          setupMockGetSicCode(Left(ErrorModel(404, "Requested Data not found")))
          status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        }
      }

      "the user is unauthorised" should {

        "return a 303" in {
          lazy val result = TestController.show(request)
          mockAuthResult(mockUnauthorisedIndividual)
          status(result) shouldBe Status.FORBIDDEN
        }
      }
    }

    "calling the .submit method" when {

        "the form is filled correctly" should {
          lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] = requestPost.withFormUrlEncodedBody(("value", "12345"))
          lazy val result = TestController.submit(request)

          "return a 303" in {
            mockAuthResult(mockAuthorisedIndividual)
            setupMockStoreSicCode("12345")(Right(DeregisterVatSuccess))
            status(result) shouldBe Status.SEE_OTHER
          }

          s"redirect to ${controllers.routes.NextTaxableTurnoverController.show.url}" in {
            redirectLocation(result) shouldBe Some(controllers.routes.NextTaxableTurnoverController.show.url)
          }
        }

        "the form contains errors" should {
          lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] = requestPost.withFormUrlEncodedBody(("value", "7"))
          lazy val result = TestController.submit(request)

          "return a 303" in {
            mockAuthResult(mockAuthorisedIndividual)
            status(result) shouldBe Status.BAD_REQUEST
          }

          "return HTML" in {
            contentType(result) shouldBe Some("text/html")
            charset(result) shouldBe Some("utf-8")
          }
        }
    }
  }
}
