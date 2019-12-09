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

package controllers.ZeroRated

import controllers.ControllerBaseSpec
import play.api.http.Status
import services.mocks.MockDeleteAllStoredAnswersService

import scala.concurrent.Future

class ZeroRatedSuppliesControllerSpec extends ControllerBaseSpec with MockDeleteAllStoredAnswersService {

  object TestController extends ZeroRatedSuppliesController(
    messagesApi,
    mockAuthPredicate,
    mockPendingDeregPredicate,
    serviceErrorHandler,
    mockConfig
  )

  "The ZeroRatedSuppliesController" when {

    "calling .show with the zero rated journey feature switch on" should {

      "return a 200" in {

        mockConfig.features.zeroRatedJourney(true)
        lazy val result = TestController.show()(request)
        mockAuthResult(Future.successful(mockAuthorisedIndividual))
        status(result) shouldBe Status.OK
      }
    }

    "calling .show with an unauthorised user" should {

      "return a 303" in {
        lazy val result = TestController.show()(request)
        mockAuthResult(Future.successful(mockUnauthorisedIndividual))
        status(result) shouldBe Status.FORBIDDEN
      }
    }

    "calling .show with the zero rated journey feature switch off" should {

      "return a 400" in {
        mockConfig.features.zeroRatedJourney(false)
        lazy val result = TestController.show()(request)
        mockAuthResult(Future.successful(mockAuthorisedIndividual))
        status(result) shouldBe Status.BAD_REQUEST
      }
    }

    "calling .submit with the zero rated journey feature switch on" should {

      "return a 200" in {
        mockConfig.features.zeroRatedJourney(true)
        lazy val result = TestController.submit()(request)
        mockAuthResult(Future.successful(mockAuthorisedIndividual))
        status(result) shouldBe Status.OK
      }

      "calling .submit with the zero rated journey feature switch off" should {

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
