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

package assets.constants

import java.time.LocalDate

import models.deregistrationRequest.DeregistrationInfo
import models.{BelowThreshold, Ceased}
import play.api.libs.json.{JsObject, Json}

object DeregistrationInfoTestConstants {

  val deregDate: LocalDate = LocalDate.now()
  val deregLaterDate: LocalDate = deregDate.plusDays(7)
  val stockValue = 1000.0
  val ottValue = 2000.0
  val assetsValue = 3000.50

  val deregistrationInfoMinModel: DeregistrationInfo = DeregistrationInfo(
    deregReason = Ceased,
    deregDate = None,
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

  val deregistrationInfoMinJson: JsObject = Json.obj(
    "deregReason" -> Ceased.value,
    "optionToTax" -> true,
    "intendSellCapitalAssets" -> true,
    "additionalTaxInvoices" -> true,
    "cashAccountingScheme" -> true
  )

  val deregistrationInfoMaxModel: DeregistrationInfo = DeregistrationInfo(
    deregReason = BelowThreshold,
    deregDate = Some(deregDate),
    deregLaterDate = Some(deregLaterDate),
    turnoverBelowThreshold = Some(TurnoverBelowThresholdTestConstants.turnoverBelowThresholdMaxModel),
    optionToTax = true,
    intendSellCapitalAssets = true,
    additionalTaxInvoices = true,
    cashAccountingScheme = true,
    optionToTaxValue = Some(ottValue),
    stocksValue = Some(stockValue),
    capitalAssetsValue = Some(assetsValue)
  )

  val deregistrationInfoMaxJson: JsObject = Json.obj(
    "deregReason" -> BelowThreshold.value,
    "deregDate" -> deregDate.toString,
    "deregLaterDate" -> deregLaterDate.toString,
    "turnoverBelowThreshold" -> TurnoverBelowThresholdTestConstants.turnoverBelowThresholdMaxJson,
    "optionToTax" -> true,
    "intendSellCapitalAssets" -> true,
    "additionalTaxInvoices" -> true,
    "cashAccountingScheme" -> true,
    "optionToTaxValue" -> ottValue,
    "stocksValue" -> stockValue,
    "capitalAssetsValue" -> assetsValue
  )

}
