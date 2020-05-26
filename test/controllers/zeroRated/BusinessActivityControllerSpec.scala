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

import assets.constants.BaseTestConstants.errorModel
import controllers.ControllerBaseSpec
import forms.YesNoForm.yesNo
import models.{DeregisterVatSuccess, No, Yes, YesNo}
import play.api.http.Status
import play.api.mvc.AnyContentAsFormUrlEncoded
import play.api.test.FakeRequest
import play.api.test.Helpers.{contentType, _}
import services.mocks.{MockBusinessActivityAnswerService, MockDeleteAllStoredAnswersService, MockWipeRedundantDataService}
import views.html.BusinessActivity

import scala.concurrent.Future

class BusinessActivityControllerSpec extends ControllerBaseSpec
  with MockDeleteAllStoredAnswersService
  with MockBusinessActivityAnswerService
  with MockWipeRedundantDataService
  {

  lazy val businessActivity: BusinessActivity = injector.instanceOf[BusinessActivity]

  object TestController extends BusinessActivityController(
    businessActivity,
    mcc,
    mockAuthPredicate,
    mockRegistrationStatusPredicate,
    mockBusinessActivityAnswerService,
    mockWipeRedundantDataService,
    serviceErrorHandler,
    ec,
    mockConfig
  )

  "The BusinessActivityController" when {

    "calling .show with the zero rated journey feature switch on with existing data of yes" should {

      lazy val result = TestController.show()(request)

      "return a 200" in {

        mockConfig.features.zeroRatedJourney(true)
        setupMockGetBusinessActivityAnswer(Right(Some(Yes)))
        mockAuthResult(Future.successful(mockAuthorisedIndividual))
        status(result) shouldBe Status.OK
      }

      "return HTML" in {
        contentType(result) shouldBe Some("text/html")
        charset(result) shouldBe Some("utf-8")
      }
    }

    "calling .show with the zero rated journey feature switch on with existing data of no" should {

      lazy val result = TestController.show()(request)

      "return a 200" in {

        mockConfig.features.zeroRatedJourney(true)
        setupMockGetBusinessActivityAnswer(Right(Some(No)))
        mockAuthResult(Future.successful(mockAuthorisedIndividual))
        status(result) shouldBe Status.OK
      }

      "return HTML" in {
        contentType(result) shouldBe Some("text/html")
        charset(result) shouldBe Some("utf-8")
      }
    }

    "calling .show with the zero rated journey feature switch on with no existing data" should {

      lazy val result = TestController.show()(request)

      "return a 200" in {

        mockConfig.features.zeroRatedJourney(true)
        setupMockGetBusinessActivityAnswer(Right(None))
        mockAuthResult(Future.successful(mockAuthorisedIndividual))
        status(result) shouldBe Status.OK
      }

      "return HTML" in {
        contentType(result) shouldBe Some("text/html")
        charset(result) shouldBe Some("utf-8")
      }

    }

    "calling .show with an unauthorised user" should {

      "return a 303" in {
        lazy val result = TestController.show()(request)
        mockAuthResult(Future.successful(mockUnauthorisedIndividual))
        status(result) shouldBe Status.FORBIDDEN
      }
    }

    "calling .show with the zero rated journey feature switch off" should {

      "return a 400" in {
        mockConfig.features.zeroRatedJourney(false)
        lazy val result = TestController.show()(request)
        mockAuthResult(Future.successful(mockAuthorisedIndividual))
        status(result) shouldBe Status.BAD_REQUEST
      }
    }

    "calling .submit with the zero rated journey feature switch on and yes is selected" should {

      mockConfig.features.zeroRatedJourney(true)
      lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
        FakeRequest("POST", "/").withFormUrlEncodedBody((yesNo, "yes"))
      lazy val result = TestController.submit()(request)

      "return 303 " in {
        setupMockStoreBusinessActivityAnswer(Yes)(Right(DeregisterVatSuccess))
        setupMockWipeRedundantData(Right(DeregisterVatSuccess))
        mockAuthResult(Future.successful(mockAuthorisedIndividual))
        status(result) shouldBe Status.SEE_OTHER
      }

      s"Redirect to the '${controllers.zeroRated.routes.SicCodeController.show().url}'" in {
        redirectLocation(result) shouldBe Some(controllers.zeroRated.routes.SicCodeController.show().url)
      }
    }

    "calling .submit with the zero rated journey feature switch on and no is selected" should {

      mockConfig.features.zeroRatedJourney(true)
      lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
        FakeRequest("POST", "/").withFormUrlEncodedBody((yesNo, "no"))
      lazy val result = TestController.submit()(request)

      "return 303 " in {
        setupMockStoreBusinessActivityAnswer(No)(Right(DeregisterVatSuccess))
        setupMockWipeRedundantData(Right(DeregisterVatSuccess))
        mockAuthResult(Future.successful(mockAuthorisedIndividual))
        status(result) shouldBe Status.SEE_OTHER
      }

      s"Redirect to the '${controllers.routes.NextTaxableTurnoverController.show.url}'" in {
        redirectLocation(result) shouldBe Some(controllers.routes.NextTaxableTurnoverController.show().url)
      }
    }


    "calling .submit with the user does not select an option" should {

      lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
        FakeRequest("POST", "/").withFormUrlEncodedBody((yesNo, ""))
      lazy val result = TestController.submit()(request)

      "return a 400" in {
        mockAuthResult(Future.successful(mockAuthorisedIndividual))
        status(result) shouldBe Status.BAD_REQUEST
      }
    }

    "calling .submit with the zero rated journey feature switch off" should {

      lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
        FakeRequest("POST", "/").withFormUrlEncodedBody((yesNo, "no"))
      lazy val result = {
        mockConfig.features.zeroRatedJourney(false)
        TestController.submit()(request)
      }

      "return a 400" in {
        mockAuthResult(Future.successful(mockAuthorisedIndividual))
        status(result) shouldBe Status.BAD_REQUEST
      }
    }

    "the user submits after selecting an option and an error is returned when storing the answer" should {

      lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
        FakeRequest("POST", "/").withFormUrlEncodedBody((yesNo, "no"))
      lazy val result = TestController.submit()(request)

      "return a 500" in {
        setupMockStoreBusinessActivityAnswer(No)(Left(errorModel))
        mockAuthResult(Future.successful(mockAuthorisedIndividual))
        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
      }
    }
  }
}
