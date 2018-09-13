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

import play.api.http.Status
import play.api.mvc.AnyContentAsFormUrlEncoded
import play.api.test.FakeRequest
import play.api.test.Helpers.{contentType, _}
import services.mocks.MockOptionTaxAnswerService
import forms.YesNoForm._
import forms.YesNoAmountForm._
import models.{DeregisterVatSuccess, No, Yes, YesNoAmountModel}
import assets.constants.BaseTestConstants._

import scala.concurrent.Future

class OptionTaxControllerSpec extends ControllerBaseSpec {

  object TestOptionTaxController extends OptionTaxController(
    messagesApi, mockAuthPredicate, MockOptionTaxAnswerService.mockStoredAnswersService, mockConfig
  )

  val testAmt = 500
  val testYesModel = YesNoAmountModel(Yes, Some(testAmt))
  val testNoModel = YesNoAmountModel(No, None)

  "the user is authorised" when {

    "Calling the .show action" when {

      "the user does not have a pre selected option" should {

        lazy val result = TestOptionTaxController.show()(request)

        "return 200 (OK)" in {
          MockOptionTaxAnswerService.setupMockGetAnswers(Right(None))
          mockAuthResult(Future.successful(mockAuthorisedIndividual))
          status(result) shouldBe Status.OK
        }

        "return HTML" in {
          contentType(result) shouldBe Some("text/html")
          charset(result) shouldBe Some("utf-8")
        }
      }

      "the user has a pre selected option" should {

        lazy val result = TestOptionTaxController.show()(request)

        "return 200 (OK)" in {
          MockOptionTaxAnswerService.setupMockGetAnswers(Right(Some(testYesModel)))
          mockAuthResult(Future.successful(mockAuthorisedIndividual))
          status(result) shouldBe Status.OK
        }

        "return HTML" in {
          contentType(result) shouldBe Some("text/html")
          charset(result) shouldBe Some("utf-8")
        }

        "have the yes radio option checked" in {
          document(result).select(s"#$yesNo-yes").hasAttr("checked") shouldBe true
        }

        "have the correct value in the amount field" in {
          document(result).select("#amount").attr("value") shouldBe testAmt.toString
        }
      }

      authChecks(".show", TestOptionTaxController.show(), request)
    }

    "Calling the .submit action" when {

      "the user submits after selecting an 'Yes' option" should {

        lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
          FakeRequest("POST", "/").withFormUrlEncodedBody(
            (yesNo, "yes"),
            (amount, testAmt.toString)
          )
        lazy val result = TestOptionTaxController.submit()(request)

        "return 303 (SEE OTHER)" in {
          MockOptionTaxAnswerService.setupMockStoreAnswers(testYesModel)(Right(DeregisterVatSuccess))
          mockAuthResult(Future.successful(mockAuthorisedIndividual))
          status(result) shouldBe Status.SEE_OTHER
        }

        s"redirect to '${controllers.routes.OptionStocksToSellController.show().url}'" in {
          redirectLocation(result) shouldBe Some(controllers.routes.OptionStocksToSellController.show().url)
        }
      }

      "the user submits after selecting the 'No' option" should {

        lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
          FakeRequest("POST", "/").withFormUrlEncodedBody((yesNo, "no"))
        lazy val result = TestOptionTaxController.submit()(request)

        "return 303 (SEE OTHER)" in {
          MockOptionTaxAnswerService.setupMockStoreAnswers(testNoModel)(Right(DeregisterVatSuccess))
          mockAuthResult(Future.successful(mockAuthorisedIndividual))
          status(result) shouldBe Status.SEE_OTHER
        }

        s"redirect to '${controllers.routes.OptionStocksToSellController.show().url}'" in {
          redirectLocation(result) shouldBe Some(controllers.routes.OptionStocksToSellController.show().url)
        }
      }

      "the user submits after selecting an option but an error is returned when storing answer" should {

        lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
          FakeRequest("POST", "/").withFormUrlEncodedBody((yesNo, "no"))
        lazy val result = TestOptionTaxController.submit()(request)

        "return 500 (ISE)" in {
          MockOptionTaxAnswerService.setupMockStoreAnswers(testNoModel)(Left(errorModel))
          mockAuthResult(Future.successful(mockAuthorisedIndividual))
          status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        }
      }

      "the user submits without selecting an option" should {

        lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
          FakeRequest("POST", "/").withFormUrlEncodedBody(("yes_no", ""))
        lazy val result = TestOptionTaxController.submit()(request)

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

    authChecks(".submit", TestOptionTaxController.submit(), FakeRequest("POST", "/").withFormUrlEncodedBody(("yes_no", "no")))
  }
}
