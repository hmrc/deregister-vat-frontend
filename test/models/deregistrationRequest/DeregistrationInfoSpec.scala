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

package models.deregistrationRequest

import java.time.LocalDate

import assets.constants.DateModelTestConstants._
import assets.constants.NextTaxableTurnoverTestConstants._
import assets.constants.DeregistrationInfoTestConstants._
import assets.constants.TurnoverBelowThresholdTestConstants
import assets.constants.WhyTurnoverBelowTestConstants.whyTurnoverBelowOne
import assets.constants.YesNoAmountTestConstants._
import assets.constants.BaseTestConstants.agentEmail
import models._
import models.deregistrationRequest.DeregistrationInfo._
import play.api.libs.json.Json
import utils.TestUtil

class DeregistrationInfoSpec extends TestUtil {

  "DeregistrationInfo" when {

    ".customApply" should {

      "return a valid DeregistrationInfo Model when given maximum data" in {

        val expected = DeregistrationInfo(
          deregReason = BelowThreshold,
          deregDate = todayDate,
          deregLaterDate = Some(laterDate),
          turnoverBelowThreshold = Some(TurnoverBelowThresholdTestConstants.turnoverBelowThresholdPastModel),
          optionToTax = true,
          intendSellCapitalAssets = true,
          additionalTaxInvoices = true,
          cashAccountingScheme = true,
          optionToTaxValue = Some(ottValue),
          stocksValue = Some(stockValue),
          capitalAssetsValue = Some(assetsValue),
          transactorOrCapacitorEmail = Some(agentEmail)
        )
        val actual = DeregistrationInfo.customApply(
          deregReason = BelowThreshold,
          ceasedTradingDate = Some(todayDateModel),
          taxableTurnover = Some(No),
          nextTaxableTurnover = Some(nextTaxableTurnoverBelow),
          whyTurnoverBelow = None,
          accountingMethod = CashAccounting,
          optionTax = ottModel,
          stocks = stocksModel,
          capitalAssets = assetsModel,
          issueNewInvoices = Yes,
          outstandingInvoices = Some(Yes),
          deregDate = Some(deregistrationDateModel),
          transactorOrCapacitorEmail = Some(agentEmail)
        )

        actual shouldBe expected
      }

      "return a valid DeregistrationInfo Model when given minimum data" in {

        val expected = DeregistrationInfo(
          deregReason = BelowThreshold,
          deregDate = todayDate,
          deregLaterDate = None,
          turnoverBelowThreshold = None,
          optionToTax = false,
          intendSellCapitalAssets = false,
          additionalTaxInvoices = true,
          cashAccountingScheme = true,
          optionToTaxValue = None,
          stocksValue = None,
          capitalAssetsValue = None,
          transactorOrCapacitorEmail = None
        )
        val actual = DeregistrationInfo.customApply(
          deregReason = BelowThreshold,
          ceasedTradingDate = None,
          taxableTurnover = None,
          nextTaxableTurnover = None,
          whyTurnoverBelow = None,
          accountingMethod = CashAccounting,
          optionTax = yesNoAmountNo,
          stocks = yesNoAmountNo,
          capitalAssets = yesNoAmountNo,
          issueNewInvoices = Yes,
          outstandingInvoices = None,
          deregDate = None,
          transactorOrCapacitorEmail = None
        )

        actual shouldBe expected
      }
    }

    ".deregInfoDate" should {

      "return date when a CeasedTradingDate is passed to it" in {
        deregInfoDate(Some(laterDateModel)) shouldBe laterDate
      }

      "return todays date when no CeasedTradingDate is passed to it" in {
        deregInfoDate(None) shouldBe LocalDate.now
      }
    }

    ".deregLaterDate" should {

      "return a Some(date) when a deregistrationDateModel is supplied" in {
        deregLaterDate(Some(deregistrationDateModel)) shouldBe Some(laterDate)
      }

      "return a None when None is supplied" in {
        deregLaterDate(None) shouldBe None
      }
    }

    ".belowThresholdReason" should {

      "return BelowPast12Months when given a Yes" in {
        taxableTurnoverBelowReason(No) shouldBe BelowPast12Months
      }

      "return BelowNext12Months when given a No" in {
        taxableTurnoverBelowReason(Yes) shouldBe BelowNext12Months
      }
    }

    ".nextTwelveMonthsTurnover" should {

      "return Some amount when given a TaxableTurnoverModel" in {
        nextTwelveMonthsTurnover(Some(nextTaxableTurnoverAbove)) shouldBe Some(nextTaxableTurnoverAbove.turnover)
      }

      "return None when given a None" in {
        nextTwelveMonthsTurnover(None) shouldBe None
      }
    }

    ".turnoverBelowThreshold" should {

      "return a TaxableTurnoverModel containing BelowPast12Months when turnover is below threshold" in {
        turnoverBelowThreshold(
          taxableTurnover = Some(No),
          nextTaxableTurnover = Some(nextTaxableTurnoverBelow),
          whyTurnoverBelow = None) shouldBe Some(TurnoverBelowThresholdTestConstants.turnoverBelowThresholdPastModel)
      }

      "return BelowNext12Months when turnover is above threshold" in {
        turnoverBelowThreshold(
          taxableTurnover = Some(Yes),
          nextTaxableTurnover = Some(nextTaxableTurnoverBelow),
          whyTurnoverBelow = Some(whyTurnoverBelowOne)) shouldBe Some(TurnoverBelowThresholdTestConstants.turnoverBelowThresholdNextModel)
      }

      "return None when no turnover is supplied" in {
        turnoverBelowThreshold(
          taxableTurnover = None,
          nextTaxableTurnover = Some(nextTaxableTurnoverBelow),
          whyTurnoverBelow = Some(whyTurnoverBelowOne)) shouldBe None
      }

      "return None when not given a TaxableTurnoverModel" in {
        turnoverBelowThreshold(
          taxableTurnover = Some(Yes),
          nextTaxableTurnover = None,
          whyTurnoverBelow = Some(whyTurnoverBelowOne)) shouldBe None
      }
    }

    ".taxInvoices" should {

      "return true when either issueNewInvoices or outstandingInvoices are Yes" in {
        taxInvoices(Yes,Option(Yes)) shouldBe true
        taxInvoices(Yes,Option(No)) shouldBe true
        taxInvoices(No,Option(Yes)) shouldBe true
      }

      "return false when both are No" in {
        taxInvoices(No,Option(No)) shouldBe false
      }
    }

    ".isCashAccount" should {

      "return true when VatAccountsModel is CashAccounting" in {
        isCashAccounting(CashAccounting) shouldBe true
      }

      "return false when VatAccountsModel is NOT CashAccounting" in {
        isCashAccounting(StandardAccounting) shouldBe false
      }
    }

    "serializing to JSON" should {

      "for the minimum amount of data should output the correct JSON'" in {
        Json.toJson(deregistrationInfoMinModel) shouldBe deregistrationInfoMinJson
      }

      "for the maximum amount of data should output the correct JSON'" in {
        Json.toJson(deregistrationInfoMaxModel) shouldBe deregistrationInfoMaxJson
      }
    }
  }
}
