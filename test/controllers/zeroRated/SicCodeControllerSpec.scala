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

package controllers.zeroRated

import controllers.ControllerBaseSpec
import models.{DeregisterVatSuccess, No, Yes}
import play.api.http.Status
import play.api.mvc.AnyContentAsFormUrlEncoded
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.mocks.{MockBusinessActivityAnswerService, MockDeleteAllStoredAnswersService, MockSicCodeAnswerService}
import views.html.SicCode

import scala.concurrent.Future

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

      "the zero rated journey feature switch on" when {

        "the user has a value saved" should {
          lazy val result = TestController.show()(request)

          "return a 200" in {
            mockConfig.features.zeroRatedJourney(true)
            mockAuthResult(Future.successful(mockAuthorisedIndividual))
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

        "the user has no value saved" should {
          lazy val result = TestController.show()(request)

          "return a 200" in {
            mockConfig.features.zeroRatedJourney(true)
            mockAuthResult(Future.successful(mockAuthorisedIndividual))
            setupMockGetBusinessActivityAnswer(Right(Some(Yes)))
            setupMockGetSicCode(Right(None))
            status(result) shouldBe Status.OK
          }

          "return HTML" in {
            contentType(result) shouldBe Some("text/html")
            charset(result) shouldBe Some("utf-8")
          }
        }

        "the user doesn't have a value saved for businessActivity" should {
          lazy val result = TestController.show()(request)

          "return a 303" in {
            mockConfig.features.zeroRatedJourney(true)
            mockAuthResult(Future.successful(mockAuthorisedIndividual))
            setupMockGetBusinessActivityAnswer(Right(None))
            setupMockGetSicCode(Right(None))
            status(result) shouldBe Status.SEE_OTHER
          }

          s"redirect to ${controllers.zeroRated.routes.BusinessActivityController.show().url}" in {
            redirectLocation(result) shouldBe Some(controllers.zeroRated.routes.BusinessActivityController.show().url)
          }
        }

        "the user has 'No' saved for businessActivity" should {
          lazy val result = TestController.show()(request)

          "return a 303" in {
            mockConfig.features.zeroRatedJourney(true)
            mockAuthResult(Future.successful(mockAuthorisedIndividual))
            setupMockGetBusinessActivityAnswer(Right(Some(No)))
            setupMockGetSicCode(Right(None))
            status(result) shouldBe Status.SEE_OTHER
          }

          s"redirect to ${controllers.routes.NextTaxableTurnoverController.show().url}" in {
            redirectLocation(result) shouldBe Some(controllers.routes.NextTaxableTurnoverController.show().url)
          }
        }
      }

      "the user is unauthorised" should {

        "return a 303" in {
          lazy val result = TestController.show()(request)
          mockAuthResult(Future.successful(mockUnauthorisedIndividual))
          status(result) shouldBe Status.FORBIDDEN
        }
      }

      "the zero rated journey feature is switched off" should {

        "return a 400" in {
          mockConfig.features.zeroRatedJourney(false)
          lazy val result = TestController.show()(request)
          mockAuthResult(Future.successful(mockAuthorisedIndividual))
          status(result) shouldBe Status.BAD_REQUEST
        }
      }
    }

    "calling the .submit method" when {

      "the zero rated journey feature switch is on" when {

        "the form is filled correctly" should {
          lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] = FakeRequest("POST", "/").withFormUrlEncodedBody(("value", "12345"))
          lazy val result = TestController.submit()(request)

          "return a 303" in {
            mockConfig.features.zeroRatedJourney(true)
            mockAuthResult(Future.successful(mockAuthorisedIndividual))
            setupMockStoreSicCode("12345")(Right(DeregisterVatSuccess))
            status(result) shouldBe Status.SEE_OTHER
          }

          s"redirect to ${controllers.routes.NextTaxableTurnoverController.show().url}" in {
            redirectLocation(result) shouldBe Some(controllers.routes.NextTaxableTurnoverController.show().url)
          }
        }

        "the form contains errors" should {
          lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] = FakeRequest("POST", "/").withFormUrlEncodedBody(("value", "7"))
          lazy val result = TestController.submit()(request)

          "return a 303" in {
            mockConfig.features.zeroRatedJourney(true)

            mockAuthResult(Future.successful(mockAuthorisedIndividual))
            status(result) shouldBe Status.BAD_REQUEST
          }

          "return HTML" in {
            contentType(result) shouldBe Some("text/html")
            charset(result) shouldBe Some("utf-8")
          }
        }


      }

      "the zero rated journey feature switch is off" should {

        "return a 400" in {
          mockConfig.features.zeroRatedJourney(false)
          lazy val result = TestController.submit()(request)
          mockAuthResult(Future.successful(mockAuthorisedIndividual))
          status(result) shouldBe Status.BAD_REQUEST
        }
      }
    }
  }
}
