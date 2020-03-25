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

import java.time.LocalDate

import assets.constants.BaseTestConstants._
import forms.DateForm._
import forms.YesNoForm
import forms.YesNoForm._
import models._
import play.api.http.Status
import play.api.http.Status.INTERNAL_SERVER_ERROR
import play.api.mvc.AnyContentAsFormUrlEncoded
import play.api.test.FakeRequest
import play.api.test.Helpers.{contentType, _}
import services.mocks.{MockChooseDeregDateAnswerService, MockOutstandingInvoicesService, MockWipeRedundantDataService}

import scala.concurrent.Future

class ChooseDeregistrationDateControllerSpec extends ControllerBaseSpec with MockChooseDeregDateAnswerService
  with MockOutstandingInvoicesService with MockWipeRedundantDataService {

  object TestChooseDeregistrationDateController$ extends ChooseDeregistrationDateController (
    messagesApi,
    mockAuthPredicate,
    mockPendingDeregPredicate,
    mockChooseDeregDateAnswerService,
    mockOutstandingInvoicesService,
    mockWipeRedundantDataService,
    serviceErrorHandler,
    mockConfig
  )

  val testDay: Int = LocalDate.now.getDayOfMonth
  val testMonth: Int = LocalDate.now.getMonthValue
  val testYear: Int = LocalDate.now.getYear
  val testYesDeregModel = ChooseDeregistrationDateModel(Yes, Some(DateModel(testDay, testMonth, testYear)))
  val testNoDeregModel = ChooseDeregistrationDateModel(No, None)

  "the user is authorised" when {

    "Calling the .show action" when {

      "the user does not have a pre selected option" should {

        lazy val result = TestChooseDeregistrationDateController$.show()(request)

        "return 200 (OK)" in {
          setupMockGetChooseDeregDate(Right(None))
          setupMockGetOutstandingInvoices(Right(None))
          mockAuthResult(Future.successful(mockAuthorisedIndividual))
          status(result) shouldBe Status.OK
        }

        "return HTML" in {
          contentType(result) shouldBe Some("text/html")
          charset(result) shouldBe Some("utf-8")
        }
      }

      "the has a pre selected option" should {

        lazy val result = TestChooseDeregistrationDateController$.show()(request)

        "return 200 (OK)" in {
          setupMockGetChooseDeregDate(Right(Some(Yes)))
          setupMockGetOutstandingInvoices(Right(None))
          mockAuthResult(Future.successful(mockAuthorisedIndividual))
          status(result) shouldBe Status.OK
        }

        "return HTML" in {
          contentType(result) shouldBe Some("text/html")
          charset(result) shouldBe Some("utf-8")
        }

        "have the yes radio option checked" in {
          document(result).select(s"#$yesNo-$yes").hasAttr("checked") shouldBe true
        }

//        "have the correct value for the day populated" in {
//          document(result).select(s"#$day").attr("value") shouldBe testDay.toString
//        }
//
//        "have the correct value for the month populated" in {
//          document(result).select(s"#$month").attr("value") shouldBe testMonth.toString
//        }
//
//        "have the correct value for the year populated" in {
//          document(result).select(s"#$year").attr("value") shouldBe testYear.toString
//        }
      }

      "the retrieval of submitted data fails" should {

        lazy val result = TestChooseDeregistrationDateController$.show()(request)

        "return Internal Server Error" in {
          setupMockGetChooseDeregDate(Left(ErrorModel(INTERNAL_SERVER_ERROR, message = "")))
          setupMockGetOutstandingInvoices(Left(ErrorModel(INTERNAL_SERVER_ERROR, message = "")))
          mockAuthResult(Future.successful(mockAuthorisedIndividual))
          status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        }

        "return HTML" in {
          contentType(result) shouldBe Some("text/html")
        }
      }
    }

    "Calling the .submit action" when {

      "the user submits after selecting a 'Yes' option and a date" should {

        lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
          FakeRequest("POST", "/").withFormUrlEncodedBody(
            (yesNo, yes)
          )
        lazy val result = TestChooseDeregistrationDateController$.submit()(request)

        "return 303 (SEE OTHER)" in {
          setupMockStoreChooseDeregDate(Yes)(Right(DeregisterVatSuccess))
          mockAuthResult(Future.successful(mockAuthorisedIndividual))
          setupMockWipeRedundantData(Right(DeregisterVatSuccess))
          status(result) shouldBe Status.SEE_OTHER
        }

        "redirect to the deregDateController" in {
          redirectLocation(result) shouldBe Some(controllers.routes.DeregistrationDateController.show().url)
        }
      }

      "the user submits after selecting a 'No' option" should {

        lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
          FakeRequest("POST", "/").withFormUrlEncodedBody((yesNo, YesNoForm.no))
        lazy val result = TestChooseDeregistrationDateController$.submit()(request)

        "return 303 (SEE OTHER)" in {
          setupMockStoreChooseDeregDate(No)(Right(DeregisterVatSuccess))
          mockAuthResult(Future.successful(mockAuthorisedIndividual))
          setupMockWipeRedundantData(Right(DeregisterVatSuccess))
          status(result) shouldBe Status.SEE_OTHER
        }

        "redirect to the check your answers controller" in {
          redirectLocation(result) shouldBe Some(controllers.routes.CheckAnswersController.show().url)
        }
      }

      "the user submits after selecting an option but the storing of the answer fails" should {

        lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
          FakeRequest("POST", "/").withFormUrlEncodedBody((yesNo, YesNoForm.no))
        lazy val result = TestChooseDeregistrationDateController$.submit()(request)

        "return 500 (ISE)" in {
          setupMockStoreChooseDeregDate(No)(Left(errorModel))
          mockAuthResult(Future.successful(mockAuthorisedIndividual))
          status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        }
      }

      "the user submits without selecting anything" should {

        lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
          FakeRequest("POST", "/").withFormUrlEncodedBody(
            (yesNo, "")
          )
        lazy val result = TestChooseDeregistrationDateController$.submit()(request)

        "return 400 (BAD REQUEST)" in {
          setupMockGetOutstandingInvoices(Right(None))
          mockAuthResult(Future.successful(mockAuthorisedIndividual))
          status(result) shouldBe Status.BAD_REQUEST
        }

        "return HTML" in {
          contentType(result) shouldBe Some("text/html")
          charset(result) shouldBe Some("utf-8")
        }
      }

      "the user submits without selecting anything and the retrieval of outstanding invoices data fails" should {

        lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
          FakeRequest("POST", "/").withFormUrlEncodedBody(
            (yesNo, "")
          )
        lazy val result = TestChooseDeregistrationDateController$.submit()(request)

        "return Internal Server Error" in {
          setupMockGetOutstandingInvoices(Left(ErrorModel(INTERNAL_SERVER_ERROR, message = "")))
          mockAuthResult(Future.successful(mockAuthorisedIndividual))
          status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        }

        "return HTML" in {
          contentType(result) shouldBe Some("text/html")
        }
      }
    }
  }
}
