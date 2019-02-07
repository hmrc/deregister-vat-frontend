/*
 * Copyright 2019 HM Revenue & Customs
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
import models.{DateModel, DeregisterVatSuccess}
import play.api.http.Status
import play.api.mvc.AnyContentAsFormUrlEncoded
import play.api.test.FakeRequest
import play.api.test.Helpers.{contentType, _}
import services.mocks.MockCeasedTradingDateAnswerService

import scala.concurrent.Future

class CeasedTradingDateControllerSpec extends ControllerBaseSpec with MockCeasedTradingDateAnswerService {

  object TestCeasedTradingDateController extends CeasedTradingDateController(
    messagesApi,
    mockAuthPredicate,
    mockPendingDeregPredicate,
    mockCeasedTradingDateAnswerService,
    serviceErrorHandler,
    mockConfig
  )

  val testDay = 12
  val testMonth = 9
  val testYear = 1990
  val testDateModel = DateModel(testDay, testMonth, testYear)

  "the user is authorised" when {

    "Calling the .show action" when {

      "the user does not have a pre selected option" should {

        lazy val result = TestCeasedTradingDateController.show()(request)

        "return 200 (OK)" in {
          setupMockGetCeasedTradingDate(Right(None))
          mockAuthResult(Future.successful(mockAuthorisedIndividual))
          status(result) shouldBe Status.OK
        }

        "return HTML" in {
          contentType(result) shouldBe Some("text/html")
          charset(result) shouldBe Some("utf-8")
        }
      }

      "the user is has previously entered values" should {

        lazy val result = TestCeasedTradingDateController.show()(request)

        "return 200 (OK)" in {
          setupMockGetCeasedTradingDate(Right(Some(testDateModel)))
          mockAuthResult(Future.successful(mockAuthorisedIndividual))
          status(result) shouldBe Status.OK
        }

        "return HTML" in {
          contentType(result) shouldBe Some("text/html")
          charset(result) shouldBe Some("utf-8")
        }

        s"have the correct value '$testDay' for the day" in {
          document(result).select("#dateDay").attr("value") shouldBe testDay.toString
        }

        s"have the correct value '$testMonth' for the month" in {
          document(result).select("#dateMonth").attr("value") shouldBe testMonth.toString
        }

        s"have the correct value '$testYear' for the year" in {
          document(result).select("#dateYear").attr("value") shouldBe testYear.toString
        }
      }

      authChecks(".show", TestCeasedTradingDateController.show(), request)
    }

    "Calling the .submit action" when {

      "the user submits entering a date" should {

        lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
          FakeRequest("POST", "/").withFormUrlEncodedBody(
            ("dateDay", testDay.toString),
            ("dateMonth", testMonth.toString),
            ("dateYear", testYear.toString)
          )
        lazy val result = TestCeasedTradingDateController.submit()(request)

        "return 303 (SEE OTHER)" in {
          setupMockStoreCeasedTradingDate(testDateModel)(Right(DeregisterVatSuccess))
          mockAuthResult(Future.successful(mockAuthorisedIndividual))
          status(result) shouldBe Status.SEE_OTHER
        }

        s"redirect to the ${controllers.routes.VATAccountsController.show().url}" in {
          redirectLocation(result) shouldBe Some(controllers.routes.VATAccountsController.show().url)
        }
      }

      "the user submits entering a date and an error is returned from stored data service" should {

        lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
          FakeRequest("POST", "/").withFormUrlEncodedBody(
            ("dateDay", testDay.toString),
            ("dateMonth", testMonth.toString),
            ("dateYear", testYear.toString)
          )
        lazy val result = TestCeasedTradingDateController.submit()(request)

        "return 500 (ISE)" in {
          setupMockStoreCeasedTradingDate(testDateModel)(Left(errorModel))
          mockAuthResult(Future.successful(mockAuthorisedIndividual))
          status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        }
      }

      "the user submits without entering any dates" should {

        lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
          FakeRequest("POST", "/").withFormUrlEncodedBody(
            ("dateDay", ""),
            ("dateMonth", ""),
            ("dateYear", "")
          )
        lazy val result = TestCeasedTradingDateController.submit()(request)

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

    authChecks(".submit", TestCeasedTradingDateController.submit(), FakeRequest("POST", "/").withFormUrlEncodedBody(
      ("dateDay", "1"),
      ("dateMonth", "1"),
      ("dateYear", "2018")
    ))
  }
}
