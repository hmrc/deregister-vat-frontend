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

package Forms

import forms.CeasedTradingDateForm
import models.CeasedTradingDateModel
import uk.gov.hmrc.play.test.UnitSpec

class CeasedTradingDateFormSpec extends UnitSpec {

  "Binding a form with valid data" should {

    val data = Map(
      "day" -> "1",
      "month" -> "1",
      "year" -> "2018"
    )
    val form = CeasedTradingDateForm.ceasedTradingDateForm.bind(data)

    "result in a form with no errors" in {
      form.hasErrors shouldBe false
    }

    "generate the correct model" in {
      form.value shouldBe Some(CeasedTradingDateModel(1,1,2018))
    }
  }

  "Binding a form with invalid data" when {

    "the no option has been selected" should {

      val missingOption: Map[String, String] = Map.empty
      val form = CeasedTradingDateForm.ceasedTradingDateForm.bind(missingOption)

      "result in a form with errors" in {
        form.hasErrors shouldBe true
      }

      "throw one error" in {
        form.errors.size shouldBe 3
      }
    }
  }

  "A form built from a valid model" should {

    "generate the correct mapping" in {
      val model = CeasedTradingDateModel(1,1,2018)
      val form = CeasedTradingDateForm.ceasedTradingDateForm.fill(model)
      form.data shouldBe Map(
        "day" -> "1",
        "month" -> "1",
        "year" -> "2018"
      )
    }
  }

}
