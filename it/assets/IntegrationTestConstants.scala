/*
 * Copyright 2019 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http -> //www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package assets

import models._
import play.api.libs.json.Json

object IntegrationTestConstants {

  val vrn = "968501689"

  val whyTurnoverBelowModel = WhyTurnoverBelowModel(true,true,true,true,true,true,true)

  val whyTurnoverBelowJson = Json.obj(
    "lostContract" -> true,
    "semiRetiring" -> true,
    "moreCompetitors" -> true,
    "reducedTradingHours" -> true,
    "seasonalBusiness" -> true,
    "closedPlacesOfBusiness" -> true,
    "turnoverLowerThanExpected" -> true
  )

  val capitalAssetsYesModel = YesNoAmountModel(Yes, Some(12))
  val capitalAssetsYesJson = Json.toJson(capitalAssetsYesModel)

  val capitalAssetsNoModel = YesNoAmountModel(No, None)
  val capitalAssetsNoJson = Json.toJson(capitalAssetsNoModel)

  val yesNoAmountYesModel = models.YesNoAmountModel(Yes, Some(1))
  val yesNoAmountYesJson = Json.toJson(yesNoAmountYesModel)

  val yesNoAmountNoModel = models.YesNoAmountModel(No, None)
  val yesNoAmountNoJson = Json.toJson(yesNoAmountYesModel)

  val sicCodeValue: BigDecimal = 12345
  val sicCodeModel: NumberInputModel = NumberInputModel(sicCodeValue)

}
