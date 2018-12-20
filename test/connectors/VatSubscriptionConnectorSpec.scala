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

package connectors

import _root_.mocks.MockHttp
import assets.constants.BaseTestConstants.{vrn, _}
import models._
import models.deregistrationRequest.DeregistrationInfo
import utils.TestUtil
import assets.constants.DateModelTestConstants._
import assets.constants.CustomerDetailsTestConstants.customerDetailsJsonMin
import connectors.httpParsers.ResponseHttpParser.HttpGetResult
import models.CustomerDetails

import scala.concurrent.Future


class VatSubscriptionConnectorSpec extends TestUtil with MockHttp {

  object TestVatSubscriptionConnector extends VatSubscriptionConnector(mockHttp, mockConfig)

  "VatSubscriptionConnector" when {

    "calling .getCustomerDetailsUrl" should {

      "format the url correctly" in {
        val testUrl = TestVatSubscriptionConnector.getCustomerDetailsUrl(vrn)
        testUrl shouldBe s"${mockConfig.vatSubscriptionUrl}/vat-subscription/$vrn/customer-details"
      }
    }

    "calling .getCustomerDetails" when {

      def result: Future[HttpGetResult[CustomerDetails]] = TestVatSubscriptionConnector.getCustomerDetails(vrn)

      "called for a Right with CustomerDetails" should {

        "return a CustomerDetailsModel" in {
          setupMockHttpGet(TestVatSubscriptionConnector.getCustomerDetailsUrl(vrn))(Right(customerDetailsJsonMin))
          await(result) shouldBe Right(customerDetailsJsonMin)
        }
      }

      "given an error should" should {

        "return a Left with an ErrorModel" in {
          setupMockHttpGet(TestVatSubscriptionConnector.getCustomerDetailsUrl(vrn))(Left(errorModel))
          await(result) shouldBe Left(errorModel)
        }
      }
    }
  }
}
