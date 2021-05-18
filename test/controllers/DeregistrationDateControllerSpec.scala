/*
 * Copyright 2021 HM Revenue & Customs
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
import models._
import play.api.http.Status
import play.api.http.Status.INTERNAL_SERVER_ERROR
import play.api.mvc.AnyContentAsFormUrlEncoded
import play.api.test.FakeRequest
import play.api.test.Helpers.{contentType, _}
import services.mocks.MockDeregDateAnswerService
import views.html.DeregistrationDate

import scala.concurrent.Future

class DeregistrationDateControllerSpec extends ControllerBaseSpec with MockDeregDateAnswerService {

  lazy val deregistrationDate: DeregistrationDate = injector.instanceOf[DeregistrationDate]

  object TestDeregistrationDateController extends DeregistrationDateController(
    deregistrationDate,
    mcc,
    mockAuthPredicate,
    mockRegistrationStatusPredicate,
    serviceErrorHandler,
    mockDeregDateAnswerService,
    mockConfig,
    ec
  )

  val testDay: Int = LocalDate.now.getDayOfMonth
  val testMonth: Int = LocalDate.now.getMonthValue
  val testYear: Int = LocalDate.now.getYear
  val testDateModel: DateModel = DateModel(testDay, testMonth, testYear)

  "the user is authorised" when {

    "Calling the .show action" when {

      "the user does not have pre entered data" should {

        lazy val result = TestDeregistrationDateController.show()(request)

        "return 200 (OK)" in {
          setupMockGetDeregDate(Right(None))
          mockAuthResult(mockAuthorisedIndividual)
          status(result) shouldBe Status.OK
        }

        "return HTML" in {
          contentType(result) shouldBe Some("text/html")
          charset(result) shouldBe Some("utf-8")
        }
      }

      "the user has pre entered data" should {

        lazy val result = TestDeregistrationDateController.show()(request)

        "return 200 (OK)" in {
          setupMockGetDeregDate(Right(Some(testDateModel)))
          mockAuthResult(mockAuthorisedIndividual)
          status(result) shouldBe Status.OK
        }

        "return HTML" in {
          contentType(result) shouldBe Some("text/html")
          charset(result) shouldBe Some("utf-8")
        }

        "have the correct value for the day populated" in {
          document(result).select(s"#$day").attr("value") shouldBe testDay.toString
        }

        "have the correct value for the month populated" in {
          document(result).select(s"#$month").attr("value") shouldBe testMonth.toString
        }

        "have the correct value for the year populated" in {
          document(result).select(s"#$year").attr("value") shouldBe testYear.toString
        }
      }

      "the retrieval of pre entered data fails" should {

        lazy val result = TestDeregistrationDateController.show()(request)

        "return Internal Server Error" in {
          setupMockGetDeregDate(Left(ErrorModel(INTERNAL_SERVER_ERROR, message = "")))
          mockAuthResult(mockAuthorisedIndividual)
          status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        }

        "return HTML" in {
          contentType(result) shouldBe Some("text/html")
        }
      }

      authChecks(".show", TestDeregistrationDateController.show(), request)
    }

    "Calling the .submit action" when {

      "the user submits valid data" when {

        "storing of data is successful" should {

          lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
            requestPost.withFormUrlEncodedBody(
              (day, testDay.toString),
              (month, testMonth.toString),
              (year, testYear.toString)
            )
          lazy val result = TestDeregistrationDateController.submit()(request)

          "return 303 (SEE OTHER)" in {
            mockAuthResult(mockAuthorisedIndividual)
            setupMockStoreDeregDate(testDateModel)(Right(DeregisterVatSuccess))
            status(result) shouldBe Status.SEE_OTHER
          }

          "redirect to the check your answers controller" in {
            redirectLocation(result) shouldBe Some(controllers.routes.CheckAnswersController.show().url)
          }
        }

        "storing of data is unsuccessful" should {
          lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] = requestPost.withFormUrlEncodedBody(
            (day, testDay.toString),
            (month, testMonth.toString),
            (year, testYear.toString)
          )
          lazy val result = TestDeregistrationDateController.submit()(request)

          "return 500 (ISE)" in {
            mockAuthResult(mockAuthorisedIndividual)
            setupMockStoreDeregDate(testDateModel)(Left(errorModel))
            status(result) shouldBe Status.INTERNAL_SERVER_ERROR
          }
        }
      }

      "the user submits invalid data" should {

        lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
          requestPost.withFormUrlEncodedBody(
            (day, ""),
            (month, ""),
            (year, "")
          )
        lazy val result = TestDeregistrationDateController.submit()(request)

        "return 400 (BAD REQUEST)" in {
          mockAuthResult(mockAuthorisedIndividual)
          status(result) shouldBe Status.BAD_REQUEST
        }

        "return HTML" in {
          contentType(result) shouldBe Some("text/html")
          charset(result) shouldBe Some("utf-8")
        }
      }

      authChecks(".submit", TestDeregistrationDateController.submit(), requestPost.withFormUrlEncodedBody(
        ("dateDay", "1"),
        ("dateMonth", "1"),
        ("dateYear", "2018")
      ))
    }
  }
}
