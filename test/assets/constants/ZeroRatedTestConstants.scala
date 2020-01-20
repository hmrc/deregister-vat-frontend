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

import models.deregistrationRequest.ZeroRated
import play.api.libs.json.{JsObject, Json}

object ZeroRatedTestConstants {

  val zeroRatedMinModel: ZeroRated = ZeroRated(
    None,
    purchasesExceedSupplies = false,
    2000.99,
    1000.01
  )

  val zeroRatedMinJson: JsObject = Json.obj(
    "repaymentSituation" -> false,
    "zeroRatedSuppliesValue" -> 2000.99,
    "estTotalTaxTurnover" -> 1000.01
  )

  val zeroRatedMaxModel: ZeroRated = ZeroRated(
    Some("00005"),
    purchasesExceedSupplies = true,
    2000.99,
    1000.01
  )

  val zeroRatedMaxJson: JsObject = Json.obj(
    "natureOfSupplies" -> "00005",
    "repaymentSituation" -> true,
    "zeroRatedSuppliesValue" -> 2000.99,
    "estTotalTaxTurnover" -> 1000.01
  )

}
