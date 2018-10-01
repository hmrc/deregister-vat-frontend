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

import assets.constants.BaseTestConstants._
import forms.YesNoForm._
import models._
import play.api.http.Status
import play.api.mvc.AnyContentAsFormUrlEncoded
import play.api.test.FakeRequest
import play.api.test.Helpers.{contentType, _}
import services.mocks.{MockCapitalAssetsAnswerService, MockDeregReasonAnswerService, MockIssueNewInvoicesAnswerService, MockOutstandingInvoicesService}

import scala.concurrent.Future

class IssueNewInvoicesControllerSpec extends ControllerBaseSpec {

  object TestIssueNewInvoicesController extends IssueNewInvoicesController(
    messagesApi,
    mockAuthPredicate,
    MockIssueNewInvoicesAnswerService.mockStoredAnswersService,
    MockOutstandingInvoicesService.mockStoredAnswersService,
    MockDeregReasonAnswerService.mockStoredAnswersService,
    MockCapitalAssetsAnswerService.mockStoredAnswersService,
    mockConfig
  )

  "the user is authorised" when {

    "Calling the .show action" when {

      "the user does not have a pre selected option" should {

        lazy val result = TestIssueNewInvoicesController.show()(request)

        "return 200 (OK)" in {
          MockIssueNewInvoicesAnswerService.setupMockGetAnswers(Right(None))
          mockAuthResult(Future.successful(mockAuthorisedIndividual))
          status(result) shouldBe Status.OK
        }

        "return HTML" in {
          contentType(result) shouldBe Some("text/html")
          charset(result) shouldBe Some("utf-8")
        }
      }

      "the user is has pre selected option" should {

        lazy val result = TestIssueNewInvoicesController.show()(request)

        "return 200 (OK)" in {
          MockIssueNewInvoicesAnswerService.setupMockGetAnswers(Right(Some(Yes)))
          mockAuthResult(Future.successful(mockAuthorisedIndividual))
          status(result) shouldBe Status.OK
        }

        "return HTML" in {
          contentType(result) shouldBe Some("text/html")
          charset(result) shouldBe Some("utf-8")
        }

        "has the Yes radio option checked" in {
          document(result).select(s"#$yesNo-yes").hasAttr("checked") shouldBe true
        }
      }

      authChecks(".show", TestIssueNewInvoicesController.show(), request)
    }

    "Calling the .submit action" when {

      "the user submits after selecting an 'Yes' option" should {

        lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
          FakeRequest("POST", "/").withFormUrlEncodedBody((yesNo, "yes"))
        lazy val result = TestIssueNewInvoicesController.submit()(request)

        "return 303 (SEE OTHER)" in {
          MockIssueNewInvoicesAnswerService.setupMockStoreAnswers(Yes)(Right(DeregisterVatSuccess))
          MockOutstandingInvoicesService.setupMockDeleteAnswer(Right(DeregisterVatSuccess))
          mockAuthResult(Future.successful(mockAuthorisedIndividual))
          status(result) shouldBe Status.SEE_OTHER
        }

        s"Redirect to the '${controllers.routes.DeregistrationDateController.show().url}'" in {
          redirectLocation(result) shouldBe Some(controllers.routes.DeregistrationDateController.show().url)
        }
      }

      "the user submits after selecting the 'No' option" should {

        lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] = FakeRequest("POST", "/").withFormUrlEncodedBody((yesNo, "no"))
        lazy val result = TestIssueNewInvoicesController.submit()(request)

        "return 303 (SEE OTHER)" in {
          MockIssueNewInvoicesAnswerService.setupMockStoreAnswers(No)(Right(DeregisterVatSuccess))
          mockAuthResult(Future.successful(mockAuthorisedIndividual))
          status(result) shouldBe Status.SEE_OTHER
        }

        s"Redirect to the '${controllers.routes.OutstandingInvoicesController.show().url}'" in {
          redirectLocation(result) shouldBe Some(controllers.routes.OutstandingInvoicesController.show().url)
        }
      }

      "the user submits a 'Yes' but an error is returned when deleting redundant questions" should {

        lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] = FakeRequest("POST", "/").withFormUrlEncodedBody((yesNo, "yes"))
        lazy val result = TestIssueNewInvoicesController.submit()(request)

        "return 500 (ISE)" in {
          MockIssueNewInvoicesAnswerService.setupMockStoreAnswers(Yes)(Right(DeregisterVatSuccess))
          MockOutstandingInvoicesService.setupMockDeleteAnswer(Left(ErrorModel(INTERNAL_SERVER_ERROR,"error")))
          mockAuthResult(Future.successful(mockAuthorisedIndividual))
          status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        }
      }

      "the user submits after selecting an option and an error is returned when storing the answer" should {

        lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
          FakeRequest("POST", "/").withFormUrlEncodedBody((yesNo, "no"))
        lazy val result = TestIssueNewInvoicesController.submit()(request)

        "return 500 (ISE)" in {
          MockIssueNewInvoicesAnswerService.setupMockStoreAnswers(No)(Left(errorModel))
          mockAuthResult(Future.successful(mockAuthorisedIndividual))
          status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        }
      }

      "the user submits without selecting an option" should {

        lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
          FakeRequest("POST", "/").withFormUrlEncodedBody(("yes_no", ""))
        lazy val result = TestIssueNewInvoicesController.submit()(request)

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

    authChecks(".submit", TestIssueNewInvoicesController.submit(), FakeRequest("POST", "/").withFormUrlEncodedBody(("yes_no", "no")))
  }
}
