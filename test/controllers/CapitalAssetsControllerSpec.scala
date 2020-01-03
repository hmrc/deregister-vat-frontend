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
import forms.YesNoAmountForm.{amount => amountField}
import forms.YesNoForm.{no => noChecked, yes => yesChecked, yesNo => yesNoField}
import models.{DeregisterVatSuccess, No, Yes, YesNoAmountModel}
import org.jsoup.Jsoup
import play.api.http.Status
import play.api.mvc.AnyContentAsFormUrlEncoded
import play.api.test.FakeRequest
import play.api.test.Helpers.{contentType, _}
import services.mocks.{MockCapitalAssetsAnswerService, MockWipeRedundantDataService}

class CapitalAssetsControllerSpec extends ControllerBaseSpec with MockWipeRedundantDataService with MockCapitalAssetsAnswerService {

  object TestCapitalAssetsController extends CapitalAssetsController(
    messagesApi,
    mockAuthPredicate,
    mockPendingDeregPredicate,
    mockCapitalAssetsAnswerService,
    mockWipeRedundantDataService,
    serviceErrorHandler,
    mockConfig
  )

  val amount = 12345

  "the user is authorised" when {

    "Calling the .show action" when {

      "the user does not have a pre selected option" should {

        lazy val result = TestCapitalAssetsController.show()(request)

        "return 200 (OK)" in {
          setupMockGetCapitalAssets(Right(None))
          mockAuthResult(mockAuthorisedIndividual)
          status(result) shouldBe Status.OK
        }

        "return HTML" in {
          contentType(result) shouldBe Some("text/html")
          charset(result) shouldBe Some("utf-8")
        }
      }

      "the user is has pre selected option" should {

        lazy val result = TestCapitalAssetsController.show()(request)

        "return 200 (OK)" in {
          setupMockGetCapitalAssets(Right(Some(YesNoAmountModel(Yes,Some(amount)))))
          mockAuthResult(mockAuthorisedIndividual)
          status(result) shouldBe Status.OK
        }

        "return HTML" in {
          contentType(result) shouldBe Some("text/html")
          charset(result) shouldBe Some("utf-8")
        }

        "should have the 'Yes' option checked and an amount already entered" in {
          Jsoup.parse(bodyOf(result)).select("#yes_no-yes").hasAttr("checked") shouldBe true
          Jsoup.parse(bodyOf(result)).select("#amount").attr("value") shouldBe amount.toString
        }
      }

      authChecks(".show", TestCapitalAssetsController.show(), request)

    }

    "Calling the .submit action" when {

      "a success response is returned from the Wipe Redundant Data service and" when {

        "the user submits after selecting an 'Yes' option and entering an amount" should {

          lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
            FakeRequest("POST", "/").withFormUrlEncodedBody((yesNoField, yesChecked), (amountField, amount.toString))
          lazy val result = TestCapitalAssetsController.submit()(request)


          "return 303 (SEE_OTHER)" in {
            setupMockStoreCapitalAssets(YesNoAmountModel(Yes, Some(amount)))(Right(DeregisterVatSuccess))
            setupMockWipeRedundantData(Right(DeregisterVatSuccess))
            mockAuthResult(mockAuthorisedIndividual)
            status(result) shouldBe Status.SEE_OTHER
          }

          s"redirect to '${controllers.routes.OptionStocksToSellController.show().url}'" in {
            redirectLocation(result) shouldBe Some(controllers.routes.OptionStocksToSellController.show().url)
          }
        }

        "the user submits after selecting the 'No' option" should {

          lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
            FakeRequest("POST", "/").withFormUrlEncodedBody((yesNoField, noChecked))
          lazy val result = TestCapitalAssetsController.submit()(request)

          "return 303 (SEE OTHER)" in {
            setupMockStoreCapitalAssets(YesNoAmountModel(No, None))(Right(DeregisterVatSuccess))
            setupMockWipeRedundantData(Right(DeregisterVatSuccess))
            mockAuthResult(mockAuthorisedIndividual)
            status(result) shouldBe Status.SEE_OTHER
          }

          s"redirect to '${controllers.routes.OptionStocksToSellController.show().url}'" in {
            redirectLocation(result) shouldBe Some(controllers.routes.OptionStocksToSellController.show().url)
          }
        }

        "an error is returned when storing the data" should {

          lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
            FakeRequest("POST", "/").withFormUrlEncodedBody((yesNoField, noChecked))
          lazy val result = TestCapitalAssetsController.submit()(request)

          "return 500 (ISE)" in {
            setupMockStoreCapitalAssets(YesNoAmountModel(No, None))(Left(errorModel))
            mockAuthResult(mockAuthorisedIndividual)
            status(result) shouldBe Status.INTERNAL_SERVER_ERROR
          }
        }
      }

      "an error response is returned from Wipe Redundant Data service" should {

        lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
          FakeRequest("POST", "/").withFormUrlEncodedBody((yesNoField, noChecked))
        lazy val result = TestCapitalAssetsController.submit()(request)

        "return 500 (ISE)" in {
          setupMockStoreCapitalAssets(YesNoAmountModel(No, None))(Right(DeregisterVatSuccess))
          setupMockWipeRedundantData(Left(errorModel))
          mockAuthResult(mockAuthorisedIndividual)
          status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        }
      }

      "the user submits after selecting an 'Yes' option without entering an amount" should {

        lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
          FakeRequest("POST", "/").withFormUrlEncodedBody((yesNoField, yesChecked),(amountField, ""))
        lazy val result = TestCapitalAssetsController.submit()(request)


        "return 400 (BAD_REQUEST)" in {
          mockAuthResult(mockAuthorisedIndividual)
          status(result) shouldBe Status.BAD_REQUEST
        }
      }

      "the user submits without selecting an option" should {

        lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
          FakeRequest("POST", "/").withFormUrlEncodedBody((yesNoField, ""))
        lazy val result = TestCapitalAssetsController.submit()(request)

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

    authChecks(".submit", TestCapitalAssetsController.submit(), FakeRequest("POST", "/").withFormUrlEncodedBody((yesNoField, yesChecked)))

  }
}
