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

package models

import play.api.libs.json.{Format, Json}

case class WhyTurnoverBelowModel(lostContract: Boolean = false,
                                 semiRetiring: Boolean = false,
                                 moreCompetitors: Boolean = false,
                                 reducedTradingHours: Boolean = false,
                                 seasonalBusiness: Boolean = false,
                                 closedPlacesOfBusiness: Boolean = false,
                                 turnoverLowerThanExpected: Boolean = false,
                                 alreadyBelow: Boolean = false) {

  val hasAtLeastOneSelected: Boolean = lostContract || semiRetiring || moreCompetitors || reducedTradingHours ||
    seasonalBusiness || closedPlacesOfBusiness || turnoverLowerThanExpected || alreadyBelow

  val asSequence = Seq(
    lostContract -> WhyTurnoverBelowModel.lostContract,
    semiRetiring -> WhyTurnoverBelowModel.semiRetiring,
    moreCompetitors -> WhyTurnoverBelowModel.moreCompetitors,
    reducedTradingHours -> WhyTurnoverBelowModel.reducedTradingHours,
    seasonalBusiness -> WhyTurnoverBelowModel.seasonalBusiness,
    closedPlacesOfBusiness -> WhyTurnoverBelowModel.closedPlacesOfBusiness,
    turnoverLowerThanExpected -> WhyTurnoverBelowModel.turnoverLowerThanExpected
  )

}

object TurnoverAlreadyBelow extends WhyTurnoverBelowModel(alreadyBelow = true)

object WhyTurnoverBelowModel {

  val lostContract: String = "lostContract"
  val semiRetiring: String = "semiRetiring"
  val moreCompetitors: String = "moreCompetitors"
  val reducedTradingHours: String = "reducedTradingHours"
  val seasonalBusiness: String = "seasonalBusiness"
  val closedPlacesOfBusiness: String = "closedPlacesOfBusiness"
  val turnoverLowerThanExpected: String = "turnoverLowerThanExpected"
  val alreadyBelow: String = "alreadyBelow"

  def formApply(lostContract: Boolean,
                semiRetiring: Boolean,
                moreCompetitors: Boolean,
                reducedTradingHours: Boolean,
                seasonalBusiness: Boolean,
                closedPlacesOfBusiness: Boolean,
                turnoverLowerThanExpected: Boolean): WhyTurnoverBelowModel =
    WhyTurnoverBelowModel(
      lostContract,
      semiRetiring,
      moreCompetitors,
      reducedTradingHours,
      seasonalBusiness,
      closedPlacesOfBusiness,
      turnoverLowerThanExpected
    )


  def formUnapply(whyTurnoverBelowModel: WhyTurnoverBelowModel): Option[(Boolean, Boolean, Boolean, Boolean, Boolean, Boolean, Boolean)] =
    Some(
      whyTurnoverBelowModel.lostContract,
      whyTurnoverBelowModel.semiRetiring,
      whyTurnoverBelowModel.moreCompetitors,
      whyTurnoverBelowModel.reducedTradingHours,
      whyTurnoverBelowModel.seasonalBusiness,
      whyTurnoverBelowModel.closedPlacesOfBusiness,
      whyTurnoverBelowModel.turnoverLowerThanExpected
    )


  implicit val format: Format[WhyTurnoverBelowModel] = Json.format[WhyTurnoverBelowModel]
}

