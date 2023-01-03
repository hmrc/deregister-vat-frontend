/*
 * Copyright 2023 HM Revenue & Customs
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
import assets.constants.CustomerDetailsTestConstants.customerDetailsJsonMin
import assets.constants.DateModelTestConstants._
import models.{CustomerDetails, _}
import models.deregistrationRequest.DeregistrationInfo
import org.scalatest.concurrent.ScalaFutures.convertScalaFuture
import utils.TestUtil

import scala.concurrent.Future

class VatSubscriptionConnectorSpec extends TestUtil with MockHttp {

  object TestVatSubscriptionConnector extends VatSubscriptionConnector(mockHttp, mockConfig)

  val deregInfoModel: DeregistrationInfo = DeregistrationInfo(
    deregReason = Ceased,
    deregDate = todayDate,
    deregLaterDate = None,
    turnoverBelowThreshold = None,
    zeroRated = None,
    optionToTax = true,
    intendSellCapitalAssets = true,
    additionalTaxInvoices = true,
    cashAccountingScheme = true,
    optionToTaxValue = None,
    stocksValue = None,
    capitalAssetsValue = None,
    transactorOrCapacitorEmail = None
  )

  "VatSubscriptionConnector" when {

    "Calling .submit" should {

      "A valid response is parsed" should {

        "return a DeregisterVatSuccess object if a valid response is parsed" in {
          setupMockHttpPut[DeregistrationInfo](TestVatSubscriptionConnector.url(vrn), deregInfoModel)(Future.successful(Right(DeregisterVatSuccess)))
          TestVatSubscriptionConnector.submit(vrn, deregInfoModel).futureValue shouldBe Right(DeregisterVatSuccess)
        }

        "return an error model if an invalid response is parsed" in {
          setupMockHttpPut[DeregistrationInfo](TestVatSubscriptionConnector.url(vrn), deregInfoModel)(Future.successful(Left(errorModel)))
          TestVatSubscriptionConnector.submit(vrn, deregInfoModel).futureValue shouldBe Left(errorModel)
        }
      }

      "calling .getCustomerDetails" when {

        def result: Future[Either[ErrorModel, CustomerDetails]] = TestVatSubscriptionConnector.getCustomerDetails(vrn)

        "called for a Right with CustomerDetails" should {

          "return a CustomerDetailsModel" in {
            setupMockHttpGet(TestVatSubscriptionConnector.getFullDetailsUrl(vrn))(Future.successful(Right(customerDetailsJsonMin)))
            result.futureValue shouldBe Right(customerDetailsJsonMin)
          }
        }

        "given an error should" should {

          "return a Left with an ErrorModel" in {
            setupMockHttpGet(TestVatSubscriptionConnector.getFullDetailsUrl(vrn))(Future.successful(Left(errorModel)))
            result.futureValue shouldBe Left(errorModel)
          }
        }
      }
    }
  }
}
