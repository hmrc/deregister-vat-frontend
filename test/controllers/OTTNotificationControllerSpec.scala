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
import forms.YesNoForm.yesNo
import models.{DeregisterVatSuccess, No, Yes}
import play.api.test.Helpers.{contentType, _}
import play.api.http.Status
import play.api.mvc.AnyContentAsFormUrlEncoded
import play.api.test.FakeRequest
import services.mocks.MockOTTNotificationAnswerService
import views.html.OTTNotification

class OTTNotificationControllerSpec extends ControllerBaseSpec with MockOTTNotificationAnswerService {

  lazy val ottNotification: OTTNotification = injector.instanceOf[OTTNotification]

  object TestOTTNotificationController extends OTTNotificationController(
    ottNotification,
    mcc,
    mockAuthPredicate,
    mockRegistrationStatusPredicate,
    mockOTTNotificationAnswerService,
    serviceErrorHandler,
    ec,
    mockConfig
  )

  "show" should {
    "return 200(OK)" when {
      "the user does not have a pre-selected option" in {
        lazy val result = TestOTTNotificationController.show(request)
        mockAuthResult(mockAuthorisedIndividual)
        setupMockGetOTTNotification(Right(None))
        status(result) shouldBe Status.OK
        contentType(result) shouldBe Some("text/html")
        charset(result) shouldBe Some("utf-8")
      }

      "the user has pre-selected 'yes' option" in {
        lazy val result = TestOTTNotificationController.show(request)
        mockAuthResult(mockAuthorisedIndividual)
        setupMockGetOTTNotification(Right(Some(Yes)))
        status(result) shouldBe Status.OK
        contentType(result) shouldBe Some("text/html")
        charset(result) shouldBe Some("utf-8")
        document(result).select(s"#$yesNo").hasAttr("checked") shouldBe true
      }

      "the user has pre-selected 'no' option" in {
        lazy val result = TestOTTNotificationController.show(request)
        mockAuthResult(mockAuthorisedIndividual)
        setupMockGetOTTNotification(Right(Some(No)))
        status(result) shouldBe Status.OK
        contentType(result) shouldBe Some("text/html")
        charset(result) shouldBe Some("utf-8")
        document(result).select(s"#$yesNo-2").hasAttr("checked") shouldBe true
      }
    }
  }

  "submit" should {
    "return 303 (SEE OTHER) and redirect to the correct URL" when {
      // TODO: This should point to the new page created as part of DL-18244

      "the user selected yes" in {
        lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] = requestPost.withFormUrlEncodedBody(("yes_no", "yes"))
        lazy val result = TestOTTNotificationController.submit(request)
        mockAuthResult(mockAuthorisedIndividual)
        setupMockStoreOTTNotification(Yes)(Right(DeregisterVatSuccess))
        status(result) shouldBe Status.SEE_OTHER
        redirectLocation(result) shouldBe Some(controllers.routes.OptionStocksToSellController.show.url)
      }

      "the user selected no" in {
        lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] = requestPost.withFormUrlEncodedBody(("yes_no", "no"))
        lazy val result = TestOTTNotificationController.submit(request)
        mockAuthResult(mockAuthorisedIndividual)
        setupMockStoreOTTNotification(No)(Right(DeregisterVatSuccess))
        status(result) shouldBe Status.SEE_OTHER
        redirectLocation(result) shouldBe Some(controllers.routes.OptionStocksToSellController.show.url)
      }
    }

    "return 400 (BAD_REQUEST)" when {
      "the user did not selected any choice" in {
        lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] = requestPost.withFormUrlEncodedBody(("yes_no", ""))
        lazy val result = TestOTTNotificationController.submit(request)
        mockAuthResult(mockAuthorisedIndividual)
        status(result) shouldBe Status.BAD_REQUEST
      }
    }

    "return 500 (INTERNAL_SERVER_ERROR)" when {
      "an exception is thrown" in {
        lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] = requestPost.withFormUrlEncodedBody(("yes_no", "yes"))
        lazy val result = TestOTTNotificationController.submit(request)
        mockAuthResult(mockAuthorisedIndividual)
        setupMockStoreOTTNotification(Yes)(Left(errorModel))
        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
      }
    }

  }

}
