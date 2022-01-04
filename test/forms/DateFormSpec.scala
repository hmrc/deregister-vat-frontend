/*
 * Copyright 2022 HM Revenue & Customs
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

import _root_.utils.TestUtil
import assets.messages.{CeasedTradingDateMessages, CommonMessages}
import models.DateModel
import play.api.i18n.Messages

class DateFormSpec extends TestUtil {

  "Binding a form with valid date" when {

    "max values are given" should {
      val data = Map(
        DateForm.day -> "31",
        DateForm.month -> "12",
        DateForm.year -> "9999"
      )
      val form = DateForm.dateForm.bind(data)

      "result in a form with no errors" in {
        form.hasErrors shouldBe false
      }

      "generate a DateModel" in {
        form.value shouldBe Some(DateModel(31, 12, 9999))
      }
    }

    "minimum values are given" should {
      val data = Map(
        DateForm.day -> "1",
        DateForm.month -> "1",
        DateForm.year -> "1000"
      )
      val form = DateForm.dateForm.bind(data)

      "result in a form with no errors" in {
        form.hasErrors shouldBe false
      }

      "generate a DateModel" in {
        form.value shouldBe Some(DateModel(1, 1, 1000))
      }
    }
  }

  "Binding a form with invalid data" when {

    "no date has been entered" should {

      val missingOption: Map[String, String] = Map.empty
      val form = DateForm.dateForm.bind(missingOption)

      "result in a form with errors" in {
        form.hasErrors shouldBe true
      }

      "have three errors" in {
        form.errors.size shouldBe 3
        Messages(form.errors.head.message) shouldBe CommonMessages.errorDateDay
        Messages(form.errors(1).message) shouldBe CommonMessages.errorDateMonth
        Messages(form.errors(2).message) shouldBe CommonMessages.errorDateYear
      }
    }

    "values given are too high" should {

      val data = Map(
        DateForm.day -> "32",
        DateForm.month -> "13",
        DateForm.year -> "10000"
      )
      val form = DateForm.dateForm.bind(data)

      "result in a form with errors" in {
        form.hasErrors shouldBe true
      }

      "have three error" in {
        form.errors.size shouldBe 3
        Messages(form.errors.head.message) shouldBe CommonMessages.errorDateDay
        Messages(form.errors(1).message) shouldBe CommonMessages.errorDateMonth
        Messages(form.errors(2).message) shouldBe CommonMessages.errorDateYear
      }
    }

    "values given are too low" should {

      val data = Map(
        DateForm.day -> "0",
        DateForm.month -> "0",
        DateForm.year -> "999"
      )
      val form = DateForm.dateForm.bind(data)

      "result in a form with errors" in {
        form.hasErrors shouldBe true
      }

      "have three error" in {
        form.errors.size shouldBe 3
        Messages(form.errors.head.message) shouldBe CommonMessages.errorDateDay
        Messages(form.errors(1).message) shouldBe CommonMessages.errorDateMonth
        Messages(form.errors(2).message) shouldBe CommonMessages.errorDateYear
      }
    }

    "an invalid date has been entered" should {

      val data = Map(
        DateForm.day -> "31",
        DateForm.month -> "2",
        DateForm.year -> "2018"
      )
      val form = DateForm.dateForm.bind(data)

      "result in a form with errors" in {
        form.hasErrors shouldBe true
      }

      "have three error" in {
        form.errors.size shouldBe 1
        Messages(form.errors.head.message) shouldBe CeasedTradingDateMessages.errorNoEntry
      }
    }
  }

  "A form built from a valid model" should {

    "generate the correct mapping" in {
      val model = DateModel(1,1,2018)
      val form = DateForm.dateForm.fill(model)
      form.data shouldBe Map(
        DateForm.day -> "1",
        DateForm.month -> "1",
        DateForm.year -> "2018"
      )
    }
  }

}
