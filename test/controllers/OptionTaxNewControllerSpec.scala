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

import play.api.http.Status
import play.api.mvc.AnyContentAsFormUrlEncoded
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import assets.constants.BaseTestConstants._
import forms.YesNoForm._
import models.{DeregisterVatSuccess, No, Yes}
import services.mocks.MockOptionTaxNewAnswerService
import views.html.OptionTaxNew

class OptionTaxNewControllerSpec extends ControllerBaseSpec with MockOptionTaxNewAnswerService {

  lazy val optionTax: OptionTaxNew = injector.instanceOf[OptionTaxNew]

  object TestOptionTaxController extends OptionTaxNewController(
    optionTax,
    mcc,
    mockAuthPredicate,
    mockRegistrationStatusPredicate,
    mockOptionTaxNewAnswerService,
    serviceErrorHandler,
    ec,
    mockConfig
  )

  "the user is authorised" when {

    "Calling the .show action" when {

      "the user does not have a pre selected option" should {

        lazy val result = TestOptionTaxController.show(request)

        "return 200 (OK)" in {
          setupMockGetOptionTax(Right(None))
          mockAuthResult(mockAuthorisedIndividual)
          status(result) shouldBe Status.OK
        }

        "return HTML" in {
          contentType(result) shouldBe Some("text/html")
          Helpers.charset(result) shouldBe Some("utf-8")
        }
      }

      "the user has a pre selected option" should {

        lazy val result = TestOptionTaxController.show(request)

        "return 200 (OK)" in {
          setupMockGetOptionTax(Right(Some(Yes)))
          mockAuthResult(mockAuthorisedIndividual)
          status(result) shouldBe Status.OK
        }

        "return HTML" in {
          contentType(result) shouldBe Some("text/html")
          charset(result) shouldBe Some("utf-8")
        }

        "have the yes radio option checked" in {
          document(result).select(s"#$yesNo").hasAttr("checked") shouldBe true
        }
      }
      authChecks(".show", TestOptionTaxController.show, request)
    }

    "Calling the .submit action" when {

      "the user submits after selecting an 'Yes' option" should {

        lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
          requestPost.withFormUrlEncodedBody(
            (yesNo, "yes")
          )
        lazy val result = TestOptionTaxController.submit(request)

        "return 303 (SEE OTHER)" in {
          setupMockStoreOptionTax(Yes)(Right(DeregisterVatSuccess))
          mockAuthResult(mockAuthorisedIndividual)
          status(result) shouldBe Status.SEE_OTHER
        }

        s"redirect to '${controllers.routes.OTTNotificationController.show.url}'" in {
          redirectLocation(result) shouldBe Some(controllers.routes.OTTNotificationController.show.url)
        }
      }

      "the user submits after selecting the 'No' option" should {

        lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
          requestPost.withFormUrlEncodedBody((yesNo, "no"))
        lazy val result = TestOptionTaxController.submit(request)

        "return 303 (SEE OTHER)" in {
          setupMockStoreOptionTax(No)(Right(DeregisterVatSuccess))
          mockAuthResult(mockAuthorisedIndividual)
          status(result) shouldBe Status.SEE_OTHER
        }

        s"redirect to '${controllers.routes.CapitalAssetsController.show.url}'" in {
          redirectLocation(result) shouldBe Some(controllers.routes.CapitalAssetsController.show.url)
        }
      }

      "the user submits after selecting the 'No' option" should {
        s"redirect to '${controllers.routes.CapitalAssetsController.show.url}' if the OTTJourney feature switch is off" in {
          mockConfig.features.ottJourneyEnabled(false)

          lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
            requestPost.withFormUrlEncodedBody((yesNo, "no"))
          lazy val result = TestOptionTaxController.submit(request)

          setupMockStoreOptionTax(No)(Right(DeregisterVatSuccess))
          mockAuthResult(mockAuthorisedIndividual)
          status(result) shouldBe Status.SEE_OTHER
          redirectLocation(result) shouldBe Some(controllers.routes.CapitalAssetsController.show.url)
        }
      }

      "the user submits after selecting an option but an error is returned when storing answer" should {

        lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
          requestPost.withFormUrlEncodedBody((yesNo, "no"))
        lazy val result = TestOptionTaxController.submit(request)

        "return 500 (ISE)" in {
          setupMockStoreOptionTax(No)(Left(errorModel))
          mockAuthResult(mockAuthorisedIndividual)
          status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        }
      }

      "the user submits without selecting an option" should {

        lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
          requestPost.withFormUrlEncodedBody(("yes_no", ""))
        lazy val result = TestOptionTaxController.submit(request)

        "return 400 (BAD REQUEST)" in {
          mockAuthResult(mockAuthorisedIndividual)
          status(result) shouldBe Status.BAD_REQUEST
        }

        "return HTML" in {
          contentType(result) shouldBe Some("text/html")
          charset(result) shouldBe Some("utf-8")
        }
      }
    }

    authChecks(".submit", TestOptionTaxController.submit, requestPost.withFormUrlEncodedBody(("yes_no", "no")))
  }
}
