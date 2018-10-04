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

      "return a valid DeregistrationInfo Model when given BelowThreshold" in {
        defaultCustomApply(deregReason = Some(BelowThreshold)) shouldBe Right(defaultDeregInfo(deregReason = BelowThreshold))
      }

      "return a valid deregistrationInfo Model when given Ceased" in {
        defaultCustomApply(deregReason = Some(Ceased)) shouldBe Right(defaultDeregInfo(deregReason = Ceased))
      }

      "return an error Model when given any other Deregistration reason" in {
        defaultCustomApply(deregReason = Some(Other)) shouldBe Left(ErrorModel(INTERNAL_SERVER_ERROR, "Invalid DeregistrationInfo model"))
        defaultCustomApply(deregReason = None) shouldBe Left(ErrorModel(INTERNAL_SERVER_ERROR, "Invalid DeregistrationInfo model"))
      }
    }

    ".deregInfoDate" should {

      "return Some(date) when Ceased DeregistrationReason and a date" in {
        deregInfoDate(deregReason = Ceased, ceasedTradingDate = Some(laterDateModel)) shouldBe Some(laterDate)
      }

      "return Some(todays date) when BelowThreshold and None is given" in {
        deregInfoDate(deregReason = BelowThreshold, ceasedTradingDate = Some(todayDateModel)) shouldBe Some(todayDate)
        deregInfoDate(deregReason = BelowThreshold, ceasedTradingDate = None) shouldBe Some(todayDate)
      }

      "return None when deregistration reason is Ceased but no date is given" in {
        deregInfoDate(deregReason = Ceased, ceasedTradingDate = None) shouldBe None
      }
    }

    ".deregLaterDate" should {

      "return a Some(date) when a deregistrationDate is supplied" in {
        deregLaterDate(deregDate = Some(deregistrationDateModel)) shouldBe Some(laterDate)
      }

      "return a None when None is supplied" in {
        deregLaterDate(deregDate = None) shouldBe None
      }
    }

    ".turnoverBelowThreshold" should {

      "return BelowPast12Months when turnover is below threshold" in {
        turnoverBelowThreshold(
          taxableTurnover = Some(taxableTurnoverBelow),
          nextTaxableTurnover = Some(taxableTurnoverBelow),
          whyTurnoverBelow = Some(whyTurnoverBelowOne),
          threshold = 9000) shouldBe Some(TurnoverBelowThresholdTestConstants.turnoverBelowThresholdMaxNextModel)
      }

      "return BelowNext12Months when turnover is above threshold" in {
        turnoverBelowThreshold(
          taxableTurnover = Some(taxableTurnoverAbove),
          nextTaxableTurnover = Some(taxableTurnoverBelow),
          whyTurnoverBelow = Some(whyTurnoverBelowOne),
          threshold = 9000) shouldBe Some(TurnoverBelowThresholdTestConstants.turnoverBelowThresholdMaxPastModel)
      }

      "return None when no turnover is supplied" in {
        turnoverBelowThreshold(
          taxableTurnover = None,
          nextTaxableTurnover = Some(taxableTurnoverBelow),
          whyTurnoverBelow = Some(whyTurnoverBelowOne),
          threshold = 9000) shouldBe None
      }

      "return None when not given a TaxableTurnoverModel" in {
        turnoverBelowThreshold(
          taxableTurnover = Some(taxableTurnoverBelow),
          nextTaxableTurnover = None,
          whyTurnoverBelow = Some(whyTurnoverBelowOne),
          threshold = 9000) shouldBe None
      }
    }

    ".retrieveYesNo" should {

      "return true when given a Yes" in {
        retrieveYesNo(Some(stocksModel)) shouldBe true
      }

      "return false when given a No or a None" in {
        retrieveYesNo(Some(yesNoAmountNo)) shouldBe false
        retrieveYesNo(None) shouldBe false
      }

      ".additionalTaxInvoices" should {

        "return true when either issueNewInvoices or outstandingInvoices are Yes" in {
          additionalTaxInvoices(issueNewInvoices = Some(Yes), outstandingInvoices = Some(Yes)) shouldBe true
          additionalTaxInvoices(issueNewInvoices = Some(Yes), outstandingInvoices = None) shouldBe true
          additionalTaxInvoices(issueNewInvoices = Some(Yes), outstandingInvoices = Some(No)) shouldBe true
          additionalTaxInvoices(issueNewInvoices = None, outstandingInvoices = Some(Yes)) shouldBe true
          additionalTaxInvoices(issueNewInvoices = Some(No), outstandingInvoices = Some(Yes)) shouldBe true
        }

        "return false when neither are Yes" in {
          additionalTaxInvoices(issueNewInvoices = None, outstandingInvoices = None) shouldBe false
          additionalTaxInvoices(issueNewInvoices = Some(No), outstandingInvoices = Some(No)) shouldBe false
          additionalTaxInvoices(issueNewInvoices = Some(No), outstandingInvoices = None) shouldBe false
          additionalTaxInvoices(issueNewInvoices = None, outstandingInvoices = Some(No)) shouldBe false
        }
      }

      ".cashAccountingScheme" should {

        "return true when Accounting Method is CashAccounting" in {
          cashAccountingScheme(accountingMethod = Some(CashAccounting)) shouldBe true
        }

        "return true when Accounting Method is not CashAccounting" in {
          cashAccountingScheme(accountingMethod = Some(StandardAccounting)) shouldBe false
          cashAccountingScheme(accountingMethod = None) shouldBe false
        }
      }

      ".retrieveAmount" should {

        "return an amount when given a YesNoAmountModel" in {
          retrieveAmount(Some(stocksModel)) shouldBe Some(stockValue)
        }

        "return an None when given a None" in {
          retrieveAmount(None) shouldBe None
        }
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

  def defaultCustomApply(deregReason: Option[DeregistrationReason] = Some(BelowThreshold),
                         ceasedTradingDate: Option[DateModel] = Some(todayDateModel),
                         taxableTurnover: Option[TaxableTurnoverModel] = Some(taxableTurnoverBelow),
                         nextTaxableTurnover: Option[TaxableTurnoverModel] = Some(taxableTurnoverBelow),
                         whyTurnoverBelow: Option[WhyTurnoverBelowModel] = Some(whyTurnoverBelowOne),
                         accountingMethod: Option[VATAccountsModel] = Some(CashAccounting),
                         optionTax: Option[YesNoAmountModel] = Some(ottModel),
                         stocks: Option[YesNoAmountModel] = Some(stocksModel),
                         capitalAssets: Option[YesNoAmountModel] = Some(assetsModel),
                         issueNewInvoices: Option[YesNo] = Some(Yes),
                         outstandingInvoices: Option[YesNo] = Some(Yes),
                         deregDate: Option[DeregistrationDateModel] = Some(deregistrationDateModel)): Either[ErrorModel, DeregistrationInfo] = {
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
                       deregInfoDate: Option[LocalDate] = Some(todayDate),
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