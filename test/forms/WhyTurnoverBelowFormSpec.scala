/*
 * Copyright 2021 HM Revenue & Customs
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

package forms

import models.WhyTurnoverBelowModel
import org.scalatest.{Matchers, OptionValues, WordSpecLike}

class WhyTurnoverBelowFormSpec extends WordSpecLike with Matchers with OptionValues {

  "Binding a form with valid data" should {

    val data = Map(
      WhyTurnoverBelowModel.lostContract -> "true",
      WhyTurnoverBelowModel.semiRetiring -> "false",
      WhyTurnoverBelowModel.moreCompetitors -> "true",
      WhyTurnoverBelowModel.reducedTradingHours -> "false",
      WhyTurnoverBelowModel.seasonalBusiness -> "true",
      WhyTurnoverBelowModel.closedPlacesOfBusiness-> "true",
      WhyTurnoverBelowModel.turnoverLowerThanExpected-> "false"
    )
    val form = WhyTurnoverBelowForm.whyTurnoverBelowForm.bind(data)

    "result in a form with no errors" in {
      form.hasErrors shouldBe false
    }

    "generate the correct model" in {
      form.value shouldBe Some(WhyTurnoverBelowModel(
        lostContract = true,
        semiRetiring = false,
        moreCompetitors = true,
        reducedTradingHours = false,
        seasonalBusiness = true,
        closedPlacesOfBusiness = true,
        turnoverLowerThanExpected = false
      ))
    }
  }

  "Binding a form with invalid data" when {

    "no checkbox has been selected" should {

      val data: Map[String, String] = Map(
        WhyTurnoverBelowModel.lostContract -> "false",
        WhyTurnoverBelowModel.semiRetiring -> "false",
        WhyTurnoverBelowModel.moreCompetitors -> "false",
        WhyTurnoverBelowModel.reducedTradingHours -> "false",
        WhyTurnoverBelowModel.seasonalBusiness -> "false",
        WhyTurnoverBelowModel.closedPlacesOfBusiness-> "false",
        WhyTurnoverBelowModel.turnoverLowerThanExpected-> "false"
      )
      val form = WhyTurnoverBelowForm.whyTurnoverBelowForm.bind(data)

      "result in a form with errors" in {
        form.hasErrors shouldBe true
      }

      "throw one error" in {
        form.errors.size shouldBe 1
      }

      "Error message should be 'whyTurnoverBelow.error.atLeastOne'" in {
        form.errors.head.message shouldBe "whyTurnoverBelow.error.atLeastOne"
      }
    }
  }

  "A form built from a valid model" should {

    "generate the correct mapping" in {
      val model = WhyTurnoverBelowModel(
        lostContract = true,
        semiRetiring = false,
        moreCompetitors = true,
        reducedTradingHours = false,
        seasonalBusiness = true,
        closedPlacesOfBusiness = true,
        turnoverLowerThanExpected = false
      )
      val form = WhyTurnoverBelowForm.whyTurnoverBelowForm.fill(model)
      form.data shouldBe Map(
        WhyTurnoverBelowModel.lostContract -> "true",
        WhyTurnoverBelowModel.semiRetiring -> "false",
        WhyTurnoverBelowModel.moreCompetitors -> "true",
        WhyTurnoverBelowModel.reducedTradingHours -> "false",
        WhyTurnoverBelowModel.seasonalBusiness -> "true",
        WhyTurnoverBelowModel.closedPlacesOfBusiness-> "true",
        WhyTurnoverBelowModel.turnoverLowerThanExpected-> "false"
      )
    }
  }

}
