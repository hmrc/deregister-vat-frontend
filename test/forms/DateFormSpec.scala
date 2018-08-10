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

package forms

import models.DateModel
import uk.gov.hmrc.play.test.UnitSpec

class DateFormSpec extends UnitSpec {

  "Binding a form with valid date" should {

    val data = Map(
      "dateDay" -> "1",
      "dateMonth" -> "1",
      "dateYear" -> "2018"
    )
    val form = DateForm.dateForm.bind(data)

    "result in a form with no errors" in {
      form.hasErrors shouldBe false
    }

    "generate a DateModel" in {
      form.value shouldBe Some(DateModel(1,1,2018))
    }
  }

  "Binding a form with invalid data" when {

    "no date has been entered" should {

      val missingOption: Map[String, String] = Map.empty
      val form = DateForm.dateForm.bind(missingOption)

      "result in a form with errors" in {
        form.hasErrors shouldBe true
      }

      "have three error" in {
        form.errors.size shouldBe 3
      }
    }

    "an invalid date has been entered" should {

      val data = Map(
        "dateDay" -> "99",
        "dateMonth" -> "99",
        "dateYear" -> "9999"
      )
      val form = DateForm.dateForm.bind(data)

      "result in a form with errors" in {
        form.hasErrors shouldBe true
      }

      "have three error" in {
        form.errors.size shouldBe 1
      }
    }
  }

  "A form built from a valid model" should {

    "generate the correct mapping" in {
      val model = DateModel(1,1,2018)
      val form = DateForm.dateForm.fill(model)
      form.data shouldBe Map(
        "dateDay" -> "1",
        "dateMonth" -> "1",
        "dateYear" -> "2018"
      )
    }
  }

}
