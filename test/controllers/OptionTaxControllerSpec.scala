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

import assets.constants.BaseTestConstants._
import forms.YesNoAmountForm._
import forms.YesNoForm._
import models.{DeregisterVatSuccess, No, Yes, YesNoAmountModel}
import play.api.http.Status
import play.api.mvc.AnyContentAsFormUrlEncoded
import play.api.test.FakeRequest
import play.api.test.Helpers.{contentType, _}
import services.mocks.MockOptionTaxAnswerService
import views.html.OptionTax


class OptionTaxControllerSpec extends ControllerBaseSpec with MockOptionTaxAnswerService {

  lazy val optionTax: OptionTax = injector.instanceOf[OptionTax]

  object TestOptionTaxController extends OptionTaxController(
    optionTax,
    mcc,
    mockAuthPredicate,
    mockRegistrationStatusPredicate,
    mockOptionTaxAnswerService,
    serviceErrorHandler,
    ec,
    mockConfig
  )

  val testAmt = 500
  val testYesModel = YesNoAmountModel(Yes, Some(testAmt))
  val testNoModel = YesNoAmountModel(No, None)

  "the user is authorised" when {

    "Calling the .show action" when {

      "the user does not have a pre selected option" should {

        lazy val result = TestOptionTaxController.show()(request)

        "return 200 (OK)" in {
          setupMockGetOptionTax(Right(None))
          mockAuthResult(mockAuthorisedIndividual)
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
          setupMockGetOptionTax(Right(Some(testYesModel)))
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

        "have the correct value in the amount field" in {
          document(result).select("#amount").attr("value") shouldBe testAmt.toString
        }
      }

      authChecks(".show", TestOptionTaxController.show(), request)
    }

    "Calling the .submit action" when {

      "the user submits after selecting an 'Yes' option" should {

        lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
          requestPost.withFormUrlEncodedBody(
            (yesNo, "yes"),
            (amount, testAmt.toString)
          )
        lazy val result = TestOptionTaxController.submit()(request)

        "return 303 (SEE OTHER)" in {
          setupMockStoreOptionTax(testYesModel)(Right(DeregisterVatSuccess))
          mockAuthResult(mockAuthorisedIndividual)
          status(result) shouldBe Status.SEE_OTHER
        }

        s"redirect to '${controllers.routes.CapitalAssetsController.show().url}'" in {
          redirectLocation(result) shouldBe Some(controllers.routes.CapitalAssetsController.show().url)
        }
      }

      "the user submits after selecting the 'No' option" should {

        lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
          requestPost.withFormUrlEncodedBody((yesNo, "no"))
        lazy val result = TestOptionTaxController.submit()(request)

        "return 303 (SEE OTHER)" in {
          setupMockStoreOptionTax(testNoModel)(Right(DeregisterVatSuccess))
          mockAuthResult(mockAuthorisedIndividual)
          status(result) shouldBe Status.SEE_OTHER
        }

        s"redirect to '${controllers.routes.CapitalAssetsController.show().url}'" in {
          redirectLocation(result) shouldBe Some(controllers.routes.CapitalAssetsController.show().url)
        }
      }

      "the user submits after selecting an option but an error is returned when storing answer" should {

        lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
          requestPost.withFormUrlEncodedBody((yesNo, "no"))
        lazy val result = TestOptionTaxController.submit()(request)

        "return 500 (ISE)" in {
          setupMockStoreOptionTax(testNoModel)(Left(errorModel))
          mockAuthResult(mockAuthorisedIndividual)
          status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        }
      }

      "the user submits without selecting an option" should {

        lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
          requestPost.withFormUrlEncodedBody(("yes_no", ""))
        lazy val result = TestOptionTaxController.submit()(request)

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

    authChecks(".submit", TestOptionTaxController.submit(), requestPost.withFormUrlEncodedBody(("yes_no", "no")))
  }
}
