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
import assets.constants.TaxableTurnoverTestConstants._
import assets.constants.DeregistrationInfoTestConstants._
import assets.constants.TurnoverBelowThresholdTestConstants
import assets.constants.WhyTurnoverBelowTestConstants.whyTurnoverBelowOne
import assets.constants.YesNoAmountTestConstants._
import models._
import models.deregistrationRequest.DeregistrationInfo._
import play.api.http.Status.INTERNAL_SERVER_ERROR
import play.api.libs.json.Json
import utils.TestUtil

class DeregistrationInfoSpec extends TestUtil {

  "DeregistrationInfo" when {

    ".customApply" should {

      "return a valid DeregistrationInfo Model when given maximum data" in {
        DeregistrationInfo.customApply(
          deregReason = BelowThreshold,
          ceasedTradingDate = Some(todayDateModel),
          taxableTurnover = Some(taxableTurnoverBelow),
          nextTaxableTurnover = Some(taxableTurnoverBelow),
          whyTurnoverBelow = Some(whyTurnoverBelowOne),
          accountingMethod = CashAccounting,
          optionTax = ottModel,
          stocks = stocksModel,
          capitalAssets = assetsModel,
          issueNewInvoices = Yes,
          outstandingInvoices = Some(Yes),
          deregDate = Some(deregistrationDateModel)
        ) shouldBe DeregistrationInfo(
          deregReason = BelowThreshold,
          deregDate = todayDate,
          deregLaterDate = Some(laterDate),
          turnoverBelowThreshold = Some(TurnoverBelowThresholdTestConstants.turnoverBelowThresholdMaxPastModel),
          optionToTax = true,
          intendSellCapitalAssets = true,
          additionalTaxInvoices = true,
          cashAccountingScheme = true,
          optionToTaxValue = Some(ottValue),
          stocksValue = Some(stockValue),
          capitalAssetsValue = Some(assetsValue)
        )
      }

//      "return a valid deregistrationInfo Model when given minimum" in {
//        defaultCustomApply(Ceased) shouldBe defaultDeregInfo(deregReason = Ceased)
//      }
    }

    ".deregInfoDate" should {

      "return Some(date) when Ceased DeregistrationReason and a date" in {
        deregInfoDate(Some(laterDateModel)) shouldBe laterDate
      }

      "return None when deregistration reason is Ceased but no date is given" in {
        deregInfoDate(None) shouldBe LocalDate.now
      }
    }

    ".deregLaterDate" should {

      "return a Some(date) when a deregistrationDate is supplied" in {
        deregLaterDate(Some(deregistrationDateModel)) shouldBe Some(laterDate)
      }

      "return a None when None is supplied" in {
        deregLaterDate(None) shouldBe None
      }
    }

    ".belowThresholdReason" should {

      "return BelowPast12Months when given a taxable turnover below threshold" in {
        belowThresholdReason(taxableTurnoverBelow) shouldBe BelowPast12Months
      }

      "return BelowNext12Months when given a taxable turnover below threshold" in {
        belowThresholdReason(taxableTurnoverAbove) shouldBe BelowNext12Months
      }
    }

    ".nextTwelveMonthsTurnover" should {

      "return Some amount when given a TaxableTurnoverModel" in {
        nextTwelveMonthsTurnover(Some(taxableTurnoverAbove)) shouldBe Some(taxableTurnoverAbove.turnover)
      }

      "return None when given a None" in {
        nextTwelveMonthsTurnover(None) shouldBe None
      }
    }

    ".turnoverBelowThreshold" should {

      "return a TaxableTurnoverModel containing BelowPast12Months when turnover is below threshold" in {
        turnoverBelowThreshold(
          taxableTurnover = Some(taxableTurnoverBelow),
          nextTaxableTurnover = Some(taxableTurnoverBelow),
          whyTurnoverBelow = Some(whyTurnoverBelowOne)) shouldBe Some(TurnoverBelowThresholdTestConstants.turnoverBelowThresholdMaxPastModel)
      }

      "return BelowNext12Months when turnover is above threshold" in {
        turnoverBelowThreshold(
          taxableTurnover = Some(taxableTurnoverAbove),
          nextTaxableTurnover = Some(taxableTurnoverBelow),
          whyTurnoverBelow = Some(whyTurnoverBelowOne)) shouldBe Some(TurnoverBelowThresholdTestConstants.turnoverBelowThresholdMaxNextModel)
      }

      "return None when no turnover is supplied" in {
        turnoverBelowThreshold(
          taxableTurnover = None,
          nextTaxableTurnover = Some(taxableTurnoverBelow),
          whyTurnoverBelow = Some(whyTurnoverBelowOne)) shouldBe None
      }

      "return None when not given a TaxableTurnoverModel" in {
        turnoverBelowThreshold(
          taxableTurnover = Some(taxableTurnoverBelow),
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

      "return true when both are No" in {
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

  def defaultCustomApply(deregReason: DeregistrationReason = BelowThreshold,
                         ceasedTradingDate: Option[DateModel] = Some(todayDateModel),
                         taxableTurnover: Option[TaxableTurnoverModel] = Some(taxableTurnoverBelow),
                         nextTaxableTurnover: Option[TaxableTurnoverModel] = Some(taxableTurnoverBelow),
                         whyTurnoverBelow: Option[WhyTurnoverBelowModel] = Some(whyTurnoverBelowOne),
                         accountingMethod: VATAccountsModel = CashAccounting,
                         optionTax: YesNoAmountModel = ottModel,
                         stocks: YesNoAmountModel = stocksModel,
                         capitalAssets: YesNoAmountModel = assetsModel,
                         issueNewInvoices: YesNo = Yes,
                         outstandingInvoices: Option[YesNo] = Some(Yes),
                         deregDate: Option[DeregistrationDateModel] = Some(deregistrationDateModel)): DeregistrationInfo = {
    DeregistrationInfo.customApply(
      deregReason,
      ceasedTradingDate,
      taxableTurnover,
      nextTaxableTurnover,
      whyTurnoverBelow,
      accountingMethod,
      optionTax,
      stocks,
      capitalAssets,
      issueNewInvoices,
      outstandingInvoices,
      deregDate
    )
  }

  def defaultDeregInfo(deregReason: DeregistrationReason = BelowThreshold,
                       deregInfoDate: LocalDate = todayDate,
                       deregLaterDate: Option[LocalDate] = Some(laterDate),
                       turnoverBelowThreshold: Option[TurnoverBelowThreshold] = Some(TurnoverBelowThresholdTestConstants.turnoverBelowThresholdMaxNextModel),
                       optionToTax: Boolean = true,
                       intendSellCapitalAssets: Boolean = true,
                       additionalTaxInvoices: Boolean = true,
                       cashAccountingScheme: Boolean = true,
                       optionToTaxValue: Option[BigDecimal] = Some(ottValue),
                       stocksValue: Option[BigDecimal] = Some(stockValue),
                       capitalAssetsValue: Option[BigDecimal] = Some(assetsValue)): DeregistrationInfo = {
    DeregistrationInfo(deregReason,
      deregInfoDate,
      deregLaterDate,
      turnoverBelowThreshold,
      optionToTax,
      intendSellCapitalAssets,
      additionalTaxInvoices,
      cashAccountingScheme,
      optionToTaxValue,
      stocksValue,
      capitalAssetsValue
    )
  }

}