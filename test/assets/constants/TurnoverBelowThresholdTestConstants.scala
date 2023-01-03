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

import assets.constants.WhyTurnoverBelowTestConstants._
import models.deregistrationRequest.{BelowNext12Months, BelowPast12Months, TurnoverBelowThreshold}
import play.api.libs.json.{JsObject, Json}

object TurnoverBelowThresholdTestConstants {

  val turnoverBelowThresholdPastModel: TurnoverBelowThreshold = TurnoverBelowThreshold(
    BelowPast12Months,
    2000,
    None
  )

  val turnoverBelowThresholdPastJson: JsObject = Json.obj(
    "belowThreshold" -> "belowPast12Months",
    "nextTwelveMonthsTurnover" -> 2000
  )

  val turnoverBelowThresholdNextModel: TurnoverBelowThreshold = TurnoverBelowThreshold(
    BelowNext12Months,
    2000,
    Some(whyTurnoverBelowOne)
  )

  val turnoverBelowThresholdNextJson: JsObject = Json.obj(
    "belowThreshold" -> "belowNext12Months",
    "nextTwelveMonthsTurnover" -> 2000,
    "whyTurnoverBelow" -> whyTurnoverBelowOneJson
  )

}
