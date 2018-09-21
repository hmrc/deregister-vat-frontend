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
import models.{DeregisterVatSuccess, No, Yes, YesNoAmountModel}
import play.api.http.Status
import play.api.mvc.AnyContentAsFormUrlEncoded
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.mocks.MockStocksAnswerService

import scala.concurrent.Future


class OptionStocksToSellControllerSpec extends ControllerBaseSpec {

  object TestOptionStocksToSellController extends OptionStocksToSellController(
    messagesApi, mockAuthPredicate, MockStocksAnswerService.mockStoredAnswersService, mockConfig
  )

  val testAmt = 500
  val testYesStocksModel = YesNoAmountModel(Yes, Some(testAmt))
  val testNoStocksModel = YesNoAmountModel(No, None)

  "the user is authorised" when {

    "Calling the .show action" when {

      "the user does not have a pre selected option" should {

        lazy val result = TestOptionStocksToSellController.show()(request)

        "return 200 (OK)" in {
          MockStocksAnswerService.setupMockGetAnswers(Right(None))
          mockAuthResult(Future.successful(mockAuthorisedIndividual))
          status(result) shouldBe Status.OK
        }

        "return HTML" in {
          contentType(result) shouldBe Some("text/html")
          charset(result) shouldBe Some("utf-8")
        }
      }

      "the user has a pre selected option" should {

        lazy val result = TestOptionStocksToSellController.show()(request)

        "return 200 (OK)" in {
          MockStocksAnswerService.setupMockGetAnswers(Right(Some(testYesStocksModel)))
          mockAuthResult(Future.successful(mockAuthorisedIndividual))
          status(result) shouldBe Status.OK
        }

        "return HTML" in {
          contentType(result) shouldBe Some("text/html")
          charset(result) shouldBe Some("utf-8")
        }

        "prepopulate the radio option" in {
          document(result).select("#yes_no-yes").hasAttr("checked") shouldBe true
        }

        "prepopulate the amount" in {
          document(result).select("#amount").attr("value") shouldBe testAmt.toString
        }
      }

      authChecks(".show", TestOptionStocksToSellController.show(), request)
    }

    "Calling the .submit action" when {

      "the user submits after selecting an 'Yes' option" should {

        lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
          FakeRequest("POST", "/").withFormUrlEncodedBody(
            ("yes_no", "yes"),
            ("amount", testAmt.toString)
          )
        lazy val result = TestOptionStocksToSellController.submit()(request)

        "return 303 (SEE OTHER)" in {
          MockStocksAnswerService.setupMockStoreAnswers(testYesStocksModel)(Right(DeregisterVatSuccess))
          mockAuthResult(Future.successful(mockAuthorisedIndividual))
          status(result) shouldBe Status.SEE_OTHER
        }

        s"redirect to '${controllers.routes.CapitalAssetsController.show().url}'" in {
          redirectLocation(result) shouldBe Some(controllers.routes.CapitalAssetsController.show().url)
        }
      }

      "the user submits after selecting the 'No' option" should {

        lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
          FakeRequest("POST", "/").withFormUrlEncodedBody(("yes_no", "no"))
        lazy val result = TestOptionStocksToSellController.submit()(request)

        "return 303 (SEE OTHER)" in {
          MockStocksAnswerService.setupMockStoreAnswers(testNoStocksModel)(Right(DeregisterVatSuccess))
          mockAuthResult(Future.successful(mockAuthorisedIndividual))
          status(result) shouldBe Status.SEE_OTHER
        }

        s"redirect to '${controllers.routes.CapitalAssetsController.show().url}'" in {
          redirectLocation(result) shouldBe Some(controllers.routes.CapitalAssetsController.show().url)
        }
      }

      "the user submits after selecting the 'No' option but an error is returned when storing" should {

        lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
          FakeRequest("POST", "/").withFormUrlEncodedBody(("yes_no", "no"))
        lazy val result = TestOptionStocksToSellController.submit()(request)

        "return 500 (ISE)" in {
          MockStocksAnswerService.setupMockStoreAnswers(testNoStocksModel)(Left(errorModel))
          mockAuthResult(Future.successful(mockAuthorisedIndividual))
          status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        }
      }

      "the user submits without selecting an option" should {

        lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
          FakeRequest("POST", "/").withFormUrlEncodedBody(("yes_no", ""))
        lazy val result = TestOptionStocksToSellController.submit()(request)

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

    authChecks(".submit", TestOptionStocksToSellController.submit(), FakeRequest("POST", "/").withFormUrlEncodedBody(("yes_no", "no")))
  }

}
