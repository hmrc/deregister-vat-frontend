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

package services

import assets.constants.BaseTestConstants.{errorModel, vrn}
import assets.constants.CustomerDetailsTestConstants.customerDetailsMax
import connectors.mocks.MockVatSubscriptionConnector
import models.{CustomerDetails, ErrorModel}
import org.scalatest.concurrent.ScalaFutures.convertScalaFuture
import utils.TestUtil

import scala.concurrent.Future

class CustomerDetailsServiceSpec extends TestUtil with MockVatSubscriptionConnector {

  object TestCustomerCircumstanceDetailsService extends CustomerDetailsService(mockVatSubscriptionConnector)

  "CustomerDetailsService" should {

    def result: Future[Either[ErrorModel, CustomerDetails]] = TestCustomerCircumstanceDetailsService.getCustomerDetails(vrn)

    "for getCustomerDetails method" when {

      "called for a Right with CustomerDetails" should {

        "return a CustomerDetailsModel" in {
          setupMockGetCustomerCircumstanceDetails(vrn)(Future.successful(Right(customerDetailsMax)))
          result.futureValue shouldBe Right(customerDetailsMax)
        }
      }

      "given an error should" should {

        "return a Left with an ErrorModel" in {
          setupMockGetCustomerCircumstanceDetails(vrn)(Future.successful(Left(errorModel)))
          result.futureValue shouldBe Left(errorModel)
        }
      }
    }
  }
}
