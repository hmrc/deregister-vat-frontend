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

import assets.constants.BaseTestConstants._
import forms.DeregistrationReasonForm._
import models._
import org.jsoup.Jsoup
import play.api.http.Status
import play.api.mvc.AnyContentAsFormUrlEncoded
import play.api.test.FakeRequest
import play.api.test.Helpers.{contentType, _}
import services.mocks._

import scala.concurrent.Future

class DeregistrationReasonControllerSpec extends ControllerBaseSpec with MockWipeRedundantDataService with MockDeregReasonAnswerService {

  object TestDeregistrationReasonController extends DeregistrationReasonController(
    messagesApi,
    mockAuthPredicate,
    mockPendingDeregPredicate,
    mockDeregReasonAnswerService,
    mockWipeRedundantDataService,
    serviceErrorHandler,
    mockConfig
  )

  "the user is authorised" when {

    "Calling the .show action" when {

      "the user does not have a pre selected option" should {

        lazy val result = TestDeregistrationReasonController.show()(user)

        "return 200 (OK)" in {
          setupMockGetDeregReason(Right(None))
          mockAuthResult(Future.successful(mockAuthorisedIndividual))
          status(result) shouldBe Status.OK
        }

        "return HTML" in {
          contentType(result) shouldBe Some("text/html")
          charset(result) shouldBe Some("utf-8")
        }
      }

      "the user does have a pre selected option" should {

        lazy val result = TestDeregistrationReasonController.show()(user)

        "return 200 (OK)" in {
          setupMockGetDeregReason(Right(Some(Ceased)))
          mockAuthResult(Future.successful(mockAuthorisedIndividual))
          status(result) shouldBe Status.OK
        }

        "return HTML" in {
          contentType(result) shouldBe Some("text/html")
          charset(result) shouldBe Some("utf-8")
        }

        "should have the 'Ceased' option checked" in {
          Jsoup.parse(bodyOf(result)).select("#reason-stoppedtrading").hasAttr("checked") shouldBe true
        }
      }

      authChecks(".show", TestDeregistrationReasonController.show(), user)
    }

    "Calling the .submit action" when {

      "a success response is received from the wipeRedundantData service and" when {

        "the user submits after selecting an 'stoppedTrading' option" should {

          lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
            FakeRequest("POST", "/").withFormUrlEncodedBody((reason, ceased))
          lazy val result = TestDeregistrationReasonController.submit()(request)

          "return 303 (SEE OTHER)" in {
            setupMockStoreDeregReason(Ceased)(Right(DeregisterVatSuccess))
            setupMockWipeRedundantData(Right(DeregisterVatSuccess))

            mockAuthResult(Future.successful(mockAuthorisedIndividual))
            status(result) shouldBe Status.SEE_OTHER
          }

          s"redirect to ${controllers.routes.CeasedTradingDateController.show().url}" in {
            redirectLocation(result) shouldBe Some(controllers.routes.CeasedTradingDateController.show().url)
          }
        }

        "the user submits after selecting the 'turnoverBelowThreshold' option" should {

          lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
            FakeRequest("POST", "/").withFormUrlEncodedBody((reason, belowThreshold))
          lazy val result = TestDeregistrationReasonController.submit()(request)


          "return 303 (SEE OTHER)" in {
            setupMockStoreDeregReason(BelowThreshold)(Right(DeregisterVatSuccess))
            setupMockWipeRedundantData(Right(DeregisterVatSuccess))

            mockAuthResult(Future.successful(mockAuthorisedIndividual))
            status(result) shouldBe Status.SEE_OTHER
          }

          s"redirect to ${controllers.routes.TaxableTurnoverController.show().url}" in {
            redirectLocation(result) shouldBe Some(controllers.routes.TaxableTurnoverController.show().url)
          }
        }

        "the user submits after selecting the 'other' option" should {

          lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
            FakeRequest("POST", "/").withFormUrlEncodedBody((reason, other))
          lazy val result = TestDeregistrationReasonController.submit()(request)

          "return 303 (SEE OTHER)" in {
            setupMockStoreDeregReason(Other)(Right(DeregisterVatSuccess))
            setupMockWipeRedundantData(Right(DeregisterVatSuccess))

            mockAuthResult(Future.successful(mockAuthorisedIndividual))
            status(result) shouldBe Status.SEE_OTHER
          }

          s"redirect to ${mockConfig.govUkCancelVatRegistration}" in {
            redirectLocation(result) shouldBe Some(mockConfig.govUkCancelVatRegistration)
          }
        }

        "the user submits without selecting an option" should {

          lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
            FakeRequest("POST", "/").withFormUrlEncodedBody((reason, ""))
          lazy val result = TestDeregistrationReasonController.submit()(request)

          "return 400 (BAD REQUEST)" in {
            mockAuthResult(Future.successful(mockAuthorisedIndividual))
            status(result) shouldBe Status.BAD_REQUEST
          }

          "return HTML" in {
            contentType(result) shouldBe Some("text/html")
            charset(result) shouldBe Some("utf-8")
          }
        }

        "if an error is returned when storing" should {

          lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
            FakeRequest("POST", "/").withFormUrlEncodedBody((reason, other))
          lazy val result = TestDeregistrationReasonController.submit()(request)

          "return ISE (INTERNAL SERVER ERROR)" in {
            setupMockStoreDeregReason(Other)(Left(errorModel))
            mockAuthResult(Future.successful(mockAuthorisedIndividual))
            status(result) shouldBe Status.INTERNAL_SERVER_ERROR
          }
        }
      }

      "an error is returned when deleting redundant data" should {

        lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
          FakeRequest("POST", "/").withFormUrlEncodedBody((reason, ceased))
        lazy val result = TestDeregistrationReasonController.submit()(request)

        "return ISE (INTERNAL SERVER ERROR)" in {
          setupMockStoreDeregReason(Ceased)(Right(DeregisterVatSuccess))
          setupMockWipeRedundantData(Left(errorModel))

          mockAuthResult(Future.successful(mockAuthorisedIndividual))
          status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        }
      }
    }

    authChecks(".submit", TestDeregistrationReasonController.submit(), FakeRequest("POST", "/").withFormUrlEncodedBody((reason, other)))
  }
}
