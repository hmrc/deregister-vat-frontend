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

package connectors

import _root_.mocks.MockHttp
import assets.constants.BaseTestConstants.{vrn, _}
import models._
import models.deregistrationRequest.DeregistrationInfo
import utils.TestUtil
import assets.constants.DateModelTestConstants._


class VatSubscriptionConnectorSpec extends TestUtil with MockHttp {

  object TestVatSubscriptionConnector extends VatSubscriptionConnector(mockHttp, mockConfig)

  val deregInfoModel = DeregistrationInfo(
    deregReason = Ceased,
    deregDate = todayDate,
    deregLaterDate = None,
    turnoverBelowThreshold = None,
    optionToTax = true,
    intendSellCapitalAssets = true,
    additionalTaxInvoices = true,
    cashAccountingScheme = true,
    optionToTaxValue = None,
    stocksValue = None,
    capitalAssetsValue = None
  )

  "DeregisterVatConnector" when {

    s"given vrn: $vrn only for the url" should {

      "return the correct url" in {
        TestVatSubscriptionConnector.url(vrn) shouldBe s"${mockConfig.vatSubscriptionUrl}/vat-subscription/$vrn/deregister"
      }
    }

    s"Calling .putAnswers" when {

      "A valid response is parsed" should {

        "return a DeregisterVatSuccess object" in {
          setupMockHttpPut[DeregistrationInfo](TestVatSubscriptionConnector.url(vrn), deregInfoModel)(Right(DeregisterVatSuccess))
          await(TestVatSubscriptionConnector.submit(vrn, deregInfoModel)) shouldBe Right(DeregisterVatSuccess)
        }
      }

      "An invalid response is parsed" should {

        "return an error model" in {
          setupMockHttpPut[DeregistrationInfo](TestVatSubscriptionConnector.url(vrn), deregInfoModel)(Left(errorModel))
          await(TestVatSubscriptionConnector.submit(vrn, deregInfoModel)) shouldBe Left(errorModel)
        }
      }
    }
  }
}