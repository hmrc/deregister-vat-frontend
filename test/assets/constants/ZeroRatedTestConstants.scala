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

package assets.constants

import models.NumberInputModel
import models.deregistrationRequest.ZeroRated
import play.api.libs.json.{JsObject, Json}

object ZeroRatedTestConstants {

  private val zeroRatedSupplies: Double = 2000.99
  private val totalTaxTurnover: Int = 2000

  val zeroRatedSuppliesValue: NumberInputModel = NumberInputModel(BigDecimal(zeroRatedSupplies))

  val zeroRatedMinModel: ZeroRated = ZeroRated(purchasesExceedSupplies = false, None, zeroRatedSupplies, totalTaxTurnover)

  val zeroRatedMinJson: JsObject = Json.obj(
    "repaymentSituation" -> false,
    "zeroRatedSuppliesValue" -> zeroRatedSupplies,
    "estTotalTaxTurnover" -> totalTaxTurnover
  )

  val zeroRatedMaxModel: ZeroRated = ZeroRated(purchasesExceedSupplies = true, Some("00005"), zeroRatedSupplies, totalTaxTurnover)

  val zeroRatedMaxJson: JsObject = Json.obj(
    "repaymentSituation" -> true,
    "natureOfSupplies" -> "00005",
    "zeroRatedSuppliesValue" -> zeroRatedSupplies,
    "estTotalTaxTurnover" -> totalTaxTurnover
  )

}
