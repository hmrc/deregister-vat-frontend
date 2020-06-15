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

package controllers.zeroRated

import assets.constants.BaseTestConstants.errorModel
import controllers.ControllerBaseSpec
import forms.YesNoForm.yesNo
import models.{DeregisterVatSuccess, No, Yes}
import play.api.http.Status
import play.api.mvc.AnyContentAsFormUrlEncoded
import play.api.test.FakeRequest
import play.api.test.Helpers.{charset, contentType}
import services.mocks.MockPurchasesExceedSuppliesAnswerService
import play.api.test.Helpers._
import views.html.PurchasesExceedSupplies

import scala.concurrent.Future

class PurchasesExceedSuppliesControllerSpec extends ControllerBaseSpec with MockPurchasesExceedSuppliesAnswerService {

  lazy val purchasesExceedSupplies: PurchasesExceedSupplies = injector.instanceOf[PurchasesExceedSupplies]

  object TestController extends PurchasesExceedSuppliesController(
    purchasesExceedSupplies,
    mcc,
    mockAuthPredicate,
    mockRegistrationStatusPredicate,
    mockPurchasesExceedSuppliesAnswerService,
    serviceErrorHandler,
    ec,
    mockConfig
  )

  "The PurchasesExceedSuppliesController" when {

    "calling .show" when {

      "the zero rated journey feature switch on" when {

        "the user has stored information for the page" should {

          lazy val result = {
            mockConfig.features.zeroRatedJourney(true)
            mockAuthResult(Future.successful(mockAuthorisedIndividual))
            TestController.show()(request)
          }

          "return a 200" in {
            setupMockGetPurchasesExceedSuppliesAnswer(Right(Some(Yes)))
            status(result) shouldBe Status.OK
          }

          "return HTML" in {
            contentType(result) shouldBe Some("text/html")
            charset(result) shouldBe Some("utf-8")
          }

          "have the 'Yes' radio option checked" in {
            document(result).select(s"#$yesNo-yes").hasAttr("checked") shouldBe true
          }
        }

        "the user has no stored information for the page" should {
          lazy val result = {
            mockConfig.features.zeroRatedJourney(true)
            mockAuthResult(Future.successful(mockAuthorisedIndividual))
            TestController.show()(request)
          }

          "return a 200" in {
            setupMockGetPurchasesExceedSuppliesAnswer(Right(None))
            status(result) shouldBe Status.OK
          }

          "return HTML" in {
            contentType(result) shouldBe Some("text/html")
            charset(result) shouldBe Some("utf-8")
          }
        }

        "the storedAnswerService returns an error" should {

          lazy val result = {
            mockConfig.features.zeroRatedJourney(true)
            mockAuthResult(Future.successful(mockAuthorisedIndividual))
            TestController.show()(request)
          }

          "return a 500" in {
            setupMockGetPurchasesExceedSuppliesAnswer(Left(errorModel))
            status(result) shouldBe Status.INTERNAL_SERVER_ERROR
          }
        }
      }

      "zero rated journey feature switch is off" should {

        lazy val result = {
          mockConfig.features.zeroRatedJourney(false)
          mockAuthResult(Future.successful(mockAuthorisedIndividual))
          TestController.show()(request)
        }

        "return a 400" in {
          status(result) shouldBe Status.BAD_REQUEST
        }

        "return HTML" in {
          contentType(result) shouldBe Some("text/html")
          charset(result) shouldBe Some("utf-8")
        }
      }

      authChecks(".show", TestController.show(), request)
    }

    "calling .submit" when {

      "the zero rated journey feature switch is on " when {

        "the user submits 'yes'" should {

          lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
            FakeRequest("POST", "/").withFormUrlEncodedBody((yesNo, "yes"))

          lazy val result = {
            mockConfig.features.zeroRatedJourney(true)
            setupMockStorePurchasesExceedSuppliesAnswer(Yes)(Right(DeregisterVatSuccess))
            mockAuthResult(Future.successful(mockAuthorisedIndividual))
            TestController.submit()(request)
          }

          "return a 303" in {
            status(result) shouldBe Status.SEE_OTHER
          }

          s"redirect to the correct url of ${controllers.routes.VATAccountsController.show.url}" in {
            redirectLocation(result) shouldBe Some(controllers.routes.VATAccountsController.show.url)
          }
        }

        "the user submits 'no'" should {

          lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
            FakeRequest("POST", "/").withFormUrlEncodedBody((yesNo, "no"))

          lazy val result = {
            mockConfig.features.zeroRatedJourney(true)
            setupMockStorePurchasesExceedSuppliesAnswer(No)(Right(DeregisterVatSuccess))
            mockAuthResult(Future.successful(mockAuthorisedIndividual))
            TestController.submit()(request)
          }

          "return a 303" in {
            status(result) shouldBe Status.SEE_OTHER
          }

          s"redirect to the correct url of ${controllers.routes.VATAccountsController.show.url}" in {
            redirectLocation(result) shouldBe Some(controllers.routes.VATAccountsController.show.url)
          }
        }

        "the user submits with nothing selected" should {

          lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
            FakeRequest("POST", "/").withFormUrlEncodedBody((yesNo, ""))

          lazy val result = {
            mockConfig.features.zeroRatedJourney(true)
            mockAuthResult(Future.successful(mockAuthorisedIndividual))
            TestController.submit()(request)
          }

          "return a 400" in {
            status(result) shouldBe Status.BAD_REQUEST
          }
        }

        "user submits and the service returns an error" should {

          lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
            FakeRequest("POST", "/").withFormUrlEncodedBody((yesNo, "yes"))

          lazy val result = {
            mockConfig.features.zeroRatedJourney(true)
            mockAuthResult(Future.successful(mockAuthorisedIndividual))
            setupMockStorePurchasesExceedSuppliesAnswer(Yes)(Left(errorModel))
            TestController.submit()(request)
          }

          "return a 500" in {
            status(result) shouldBe Status.INTERNAL_SERVER_ERROR
          }
        }
      }

      "the zero rated journey feature switch off" should {

        lazy val result = {
          mockConfig.features.zeroRatedJourney(false)
          mockAuthResult(Future.successful(mockAuthorisedIndividual))
          TestController.submit()(request)
        }

        "return a 400" in {
          status(result) shouldBe Status.BAD_REQUEST
        }

        "return HTML" in {
          contentType(result) shouldBe Some("text/html")
          charset(result) shouldBe Some("utf-8")
        }
      }

      authChecks(".submit", TestController.submit(), request)
    }
  }
}
