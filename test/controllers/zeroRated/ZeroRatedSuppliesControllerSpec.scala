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

package controllers.zeroRated

import assets.constants.BaseTestConstants.errorModel
import controllers.ControllerBaseSpec
import models.{DeregisterVatSuccess, NumberInputModel}
import play.api.http.Status
import play.api.mvc.AnyContentAsFormUrlEncoded
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.mocks.MockZeroRatedSuppliesValueService
import views.html.ZeroRatedSupplies

import scala.concurrent.Future

class ZeroRatedSuppliesControllerSpec extends ControllerBaseSpec with MockZeroRatedSuppliesValueService {

  lazy val zeroRatedSupplies: ZeroRatedSupplies = injector.instanceOf[ZeroRatedSupplies]

  object TestZeroRatedSuppliesController extends ZeroRatedSuppliesController(
    zeroRatedSupplies,
    mcc,
    mockAuthPredicate,
    mockRegistrationStatusPredicate,
    mockZeroRatedSuppliesValueService,
    serviceErrorHandler,
    mockConfig,
    ec
  )

  val testZeroRatedSuppliesAmt = 500
  val testZeroRatedSuppliesAmtAsString: String = testZeroRatedSuppliesAmt.toString
  val testZeroRatedSuppliesModel: NumberInputModel = NumberInputModel(testZeroRatedSuppliesAmt)

  "The zero rated journey feature switch is on" when {

      "The user is authorised" when {

        "calling .show action" when {

          "the user does not have a pre selected amount" should {

           lazy val result = TestZeroRatedSuppliesController.show()(request)

            "return 200 (OK)" in {
              setupMockGetZeroRatedSupplies(Right(None))
              mockAuthResult(Future.successful(mockAuthorisedIndividual))
              status(result) shouldBe Status.OK
            }

          "return HTML" in {
            contentType(result) shouldBe Some("text/html")
            charset(result) shouldBe Some("utf-8")
          }
        }

        "the user has a pre selected amount" should {

          lazy val result = TestZeroRatedSuppliesController.show()(request)

          "return 200 (OK)" in {
            setupMockGetZeroRatedSupplies(Right(Some(testZeroRatedSuppliesModel)))
            mockAuthResult(Future.successful(mockAuthorisedIndividual))
            status(result) shouldBe Status.OK
          }

          "return HTML" in {
            contentType(result) shouldBe Some("text/html")
            charset(result) shouldBe Some("utf-8")
          }

          "have the value pre-populated" in {
            document(result).select(s"#amount").attr("value") shouldBe testZeroRatedSuppliesAmtAsString
          }
        }
      }

      "calling the .submit action" when {

          "the user submits after inputting an amount" should {

            lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
              requestPost.withFormUrlEncodedBody(("amount",testZeroRatedSuppliesAmtAsString))
            lazy val result = TestZeroRatedSuppliesController.submit()(request)

            "return 303 (SEE_OTHER)" in {
              setupMockStoreZeroRatedSupplies(testZeroRatedSuppliesModel)(Right(DeregisterVatSuccess))
              mockAuthResult(Future.successful(mockAuthorisedIndividual))
              status(result) shouldBe Status.SEE_OTHER
            }

            s"Redirect to the '${controllers.zeroRated.routes.PurchasesExceedSuppliesController.show().url}'" in {
              redirectLocation(result) shouldBe Some(controllers.zeroRated.routes.PurchasesExceedSuppliesController.show().url)
            }

          }

        "the user submits but does not input an amount" should {

          lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
            requestPost.withFormUrlEncodedBody(("amount",""))
          lazy val result = TestZeroRatedSuppliesController.submit()(request)

          "return a 400" in {
            mockAuthResult(Future.successful(mockAuthorisedIndividual))
            status(result) shouldBe Status.BAD_REQUEST
          }
        }

        "the user submits after inputting an amount and an error is returned when storing the answer" should {

          lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
            requestPost.withFormUrlEncodedBody(("amount",testZeroRatedSuppliesAmtAsString))
          lazy val result = TestZeroRatedSuppliesController.submit()(request)

          "return a 500" in {
            setupMockStoreZeroRatedSupplies(testZeroRatedSuppliesModel)(Left(errorModel))
            mockAuthResult(Future.successful(mockAuthorisedIndividual))
            status(result) shouldBe Status.INTERNAL_SERVER_ERROR
          }
        }
      }
    }

    "The user is unauthorised" when {
      authChecks(".show", TestZeroRatedSuppliesController.show(), request)
    }

  }
}



