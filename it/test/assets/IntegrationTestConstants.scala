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

package test.assets

import models._
import play.api.libs.json.{JsObject, JsValue, Json}

import java.time.LocalDate

object IntegrationTestConstants {

  val vrn = "968501689"

  val whyTurnoverBelowModel = WhyTurnoverBelowModel(
    lostContract = true,
    semiRetiring = true,
    moreCompetitors = true,
    reducedTradingHours = true,
    seasonalBusiness = true,
    closedPlacesOfBusiness = true,
    turnoverLowerThanExpected = true
  )

  val whyTurnoverBelowJson: JsObject = Json.obj(
    "lostContract" -> true,
    "semiRetiring" -> true,
    "moreCompetitors" -> true,
    "reducedTradingHours" -> true,
    "seasonalBusiness" -> true,
    "closedPlacesOfBusiness" -> true,
    "turnoverLowerThanExpected" -> true
  )

  val capitalAssetsYesModel: YesNoAmountModel = YesNoAmountModel(Yes, Some(12))
  val capitalAssetsYesJson: JsValue = Json.toJson(capitalAssetsYesModel)

  val capitalAssetsNoModel: YesNoAmountModel = YesNoAmountModel(No, None)
  val capitalAssetsNoJson: JsValue = Json.toJson(capitalAssetsNoModel)

  val yesNoAmountYesModel: YesNoAmountModel = models.YesNoAmountModel(Yes, Some(1))
  val yesNoAmountYesJson: JsValue = Json.toJson(yesNoAmountYesModel)

  val yesNoAmountNoModel: YesNoAmountModel = models.YesNoAmountModel(No, None)
  val yesNoAmountNoJson: JsValue = Json.toJson(yesNoAmountYesModel)

  val sicCodeValue: String = "12345"

  val zeroRatedSuppliesValue: BigDecimal= 12345.67
  val zeroRatedSuppliesModel: NumberInputModel = NumberInputModel(zeroRatedSuppliesValue)

  val zeroRatedFullPayloadJson: JsObject = Json.obj(
      "deregReason" -> "zeroRated",
      "deregDate" -> LocalDate.now(),
      "deregLaterDate" -> "2018-01-01",
      "zeroRatedExmpApplication" -> Json.obj(
        "repaymentSituation" -> true,
        "natureOfSupplies" -> "12345",
        "zeroRatedSuppliesValue" -> 1000,
        "estTotalTaxTurnover" -> 200
      ),
      "optionToTax" -> true,
      "intendSellCapitalAssets" -> true,
      "additionalTaxInvoices" -> true,
      "cashAccountingScheme" -> true,
      "optionToTaxValue" -> 1000,
      "capitalAssetsValue" -> 1000,
      "stocksValue" -> 1000
  )

  val belowThresholdFullPayloadJson: JsObject = Json.obj(
    "deregReason" -> "belowThreshold",
    "deregDate" -> LocalDate.now(),
    "deregLaterDate" -> "2018-01-01",
    "turnoverBelowThreshold" -> Json.obj(
      "belowThreshold" -> "belowNext12Months",
      "nextTwelveMonthsTurnover" -> 200,
      "whyTurnoverBelow" -> Json.obj(
        "lostContract" -> true,
        "semiRetiring" -> true,
        "moreCompetitors" -> true,
        "reducedTradingHours" -> true,
        "seasonalBusiness" -> true,
        "closedPlacesOfBusiness" -> true,
        "turnoverLowerThanExpected" -> true
      )
    ),
    "optionToTax" -> true,
    "intendSellCapitalAssets" -> true,
    "additionalTaxInvoices" -> true,
    "cashAccountingScheme" -> true,
    "optionToTaxValue" -> 1000,
    "capitalAssetsValue" -> 1000,
    "stocksValue" -> 1000
  )

  val ceasedTradingFullPayloadJson: JsObject = Json.obj(
    "deregReason" -> "ceased",
    "deregDate" -> LocalDate.now(),
    "deregLaterDate" -> "2018-01-01",
    "optionToTax" -> true,
    "intendSellCapitalAssets" -> true,
    "additionalTaxInvoices" -> true,
    "cashAccountingScheme" -> true,
    "optionToTaxValue" -> 1000,
    "capitalAssetsValue" -> 1000,
    "stocksValue" -> 1000
  )

  val exemptOnlyFullPayloadJson: JsObject = Json.obj(
    "deregReason" -> "exemptOnly",
    "deregDate" -> LocalDate.now(),
    "deregLaterDate" -> "2018-01-01",
    "optionToTax" -> true,
    "intendSellCapitalAssets" -> true,
    "additionalTaxInvoices" -> true,
    "cashAccountingScheme" -> true,
    "optionToTaxValue" -> 1000,
    "capitalAssetsValue" -> 1000,
    "stocksValue" -> 1000
  )
}
