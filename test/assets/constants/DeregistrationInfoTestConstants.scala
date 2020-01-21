/*
 * Copyright 2020 HM Revenue & Customs
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

package assets.constants

import assets.constants.BaseTestConstants.agentEmail
import assets.constants.DateModelTestConstants._
import assets.constants.YesNoAmountTestConstants._
import models.deregistrationRequest.DeregistrationInfo
import models.{BelowThreshold, Ceased}
import play.api.libs.json.{JsObject, Json}

object DeregistrationInfoTestConstants {

  val deregistrationInfoMinModel: DeregistrationInfo = DeregistrationInfo(
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

  val deregistrationInfoMinJson: JsObject = Json.obj(
    "deregReason" -> Ceased.value,
    "deregDate" -> todayDate,
    "optionToTax" -> true,
    "intendSellCapitalAssets" -> true,
    "additionalTaxInvoices" -> true,
    "cashAccountingScheme" -> true
  )

  val deregistrationInfoMaxModel: DeregistrationInfo = DeregistrationInfo(
    deregReason = BelowThreshold,
    deregDate = todayDate,
    deregLaterDate = Some(laterDate),
    turnoverBelowThreshold = Some(TurnoverBelowThresholdTestConstants.turnoverBelowThresholdNextModel),
    zeroRated = None,
    optionToTax = true,
    intendSellCapitalAssets = true,
    additionalTaxInvoices = true,
    cashAccountingScheme = true,
    optionToTaxValue = Some(ottValue),
    stocksValue = Some(stockValue),
    capitalAssetsValue = Some(assetsValue),
    transactorOrCapacitorEmail = Some(agentEmail)
  )

  val deregistrationInfoMaxJson: JsObject = Json.obj(
    "deregReason" -> BelowThreshold.value,
    "deregDate" -> todayDate.toString,
    "deregLaterDate" -> laterDate.toString,
    "turnoverBelowThreshold" -> TurnoverBelowThresholdTestConstants.turnoverBelowThresholdNextJson,
    "optionToTax" -> true,
    "intendSellCapitalAssets" -> true,
    "additionalTaxInvoices" -> true,
    "cashAccountingScheme" -> true,
    "optionToTaxValue" -> ottValue,
    "stocksValue" -> stockValue,
    "capitalAssetsValue" -> assetsValue,
    "transactorOrCapacitorEmail" -> agentEmail
  )

}
