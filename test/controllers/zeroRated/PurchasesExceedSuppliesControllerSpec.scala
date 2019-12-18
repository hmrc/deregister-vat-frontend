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

package controllers.zeroRated

import controllers.ControllerBaseSpec
import play.api.http.Status
import play.api.test.Helpers.{charset, contentType}
import services.mocks.{MockDeleteAllStoredAnswersService, MockPurchasesExceedSuppliesAnswerService}
import play.api.test.Helpers._

import scala.concurrent.Future

class PurchasesExceedSuppliesControllerSpec extends ControllerBaseSpec
  with MockDeleteAllStoredAnswersService
  with MockPurchasesExceedSuppliesAnswerService {

  object TestController extends PurchasesExceedSuppliesController(
    messagesApi,
    mockAuthPredicate,
    mockPendingDeregPredicate,
    mockPurchasesExceedSuppliesAnswerService,
    serviceErrorHandler,
    mockConfig
  )

  "The ZeroRatedSuppliesController" when {

    "calling .show" when {

      "the zero rated journey feature switch on" should {

        "return a 200" in {
          mockConfig.features.zeroRatedJourney(true)
          lazy val result = TestController.show()(request)
          mockAuthResult(Future.successful(mockAuthorisedIndividual))
          status(result) shouldBe Status.OK
        }

//        "return HTML" in {
//          lazy val result = TestController.show()(request)
//          mockAuthResult(Future.successful(mockAuthorisedIndividual))
//          contentType(result) shouldBe Some("text/html")
//          charset(result) shouldBe Some("utf-8")
//        }
      }

      "unauthorised user" should {

        "return a 303" in {
          lazy val result = TestController.show()(request)
          mockAuthResult(Future.successful(mockUnauthorisedIndividual))
          status(result) shouldBe Status.FORBIDDEN
        }
      }

      "zero rated journey feature switch is off" should {

        "return a 400" in {
          mockConfig.features.zeroRatedJourney(false)
          lazy val result = TestController.show()(request)
          mockAuthResult(Future.successful(mockAuthorisedIndividual))
          status(result) shouldBe Status.BAD_REQUEST
        }
      }
    }

    "calling .submit" when {

      "the zero rated journey feature switch on" should {

        "return a 200" in {
          mockConfig.features.zeroRatedJourney(true)
          lazy val result = TestController.submit()(request)
          mockAuthResult(Future.successful(mockAuthorisedIndividual))
          status(result) shouldBe Status.OK
        }

        "the zero rated journey feature switch off" should {

          "return a 400" in {
            mockConfig.features.zeroRatedJourney(false)
            lazy val result = TestController.submit()(request)
            mockAuthResult(Future.successful(mockAuthorisedIndividual))
            status(result) shouldBe Status.BAD_REQUEST
          }
        }
      }
    }
  }
}
