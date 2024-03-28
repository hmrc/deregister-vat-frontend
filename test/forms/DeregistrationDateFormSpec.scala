/*
 * Copyright 2024 HM Revenue & Customs
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
import assets.messages.CommonMessages
import models.DateModel
import play.api.i18n.Messages

import java.time.LocalDate

class DeregistrationDateFormSpec extends TestUtil {

  "Binding a form with valid data" when {

    "using today's date" should {

      val testDate = LocalDate.now
      val data = Map(
        DateForm.day -> testDate.getDayOfMonth.toString,
        DateForm.month -> testDate.getMonthValue.toString,
        DateForm.year -> testDate.getYear.toString
      )
      val form = DeregistrationDateForm.form.bind(data)

      "return a DateModel" in {
        form.value shouldBe Some(DateModel(testDate.getDayOfMonth, testDate.getMonthValue, testDate.getYear))
      }

      "have no errors" in {
        form.hasErrors shouldBe false
      }
    }

    "a valid future date is entered" should {

      val testDate = LocalDate.now.plusMonths(3)
      val data = Map(
        DateForm.day -> testDate.getDayOfMonth.toString,
        DateForm.month -> testDate.getMonthValue.toString,
        DateForm.year -> testDate.getYear.toString
      )
      val form = DeregistrationDateForm.form.bind(data)

      "result in a form with no errors" in {
        form.hasErrors shouldBe false
      }

      "generate a DateModel" in {
        form.value shouldBe Some(
          DateModel(testDate.getDayOfMonth, testDate.getMonthValue, testDate.getYear)
        )
      }
    }
  }

  "Binding a form with invalid data" when {

    "individual fields are empty" should {

      val data: Map[String, String] = Map.empty
      val form = DeregistrationDateForm.form.bind(data)

      "return field errors" in {
        Messages(form.errors.head.message) shouldBe CommonMessages.errorDateDay
        Messages(form.errors(1).message) shouldBe CommonMessages.errorDateMonth
        Messages(form.errors(2).message) shouldBe CommonMessages.errorDateYear
      }
    }

    "date is not a valid date" should {

      val data = Map(
        DateForm.day -> "31",
        DateForm.month -> "2",
        DateForm.year -> "2018"
      )
      val form = DeregistrationDateForm.form.bind(data)

      "return global error" in {
        form.hasGlobalErrors shouldBe true
      }

      "return an error message" in {
        Messages(form.errors.head.message) shouldBe "Enter a valid cancellation date"
      }
    }

    "day is not valid" should {

      val data = Map(
        DateForm.day -> "32",
        DateForm.month -> "1",
        DateForm.year -> "2018"
      )
      val form = DeregistrationDateForm.form.bind(data)

      "return an error" in {
        form.hasErrors shouldBe true
      }

      "return an error message" in {
        Messages(form.errors.head.message) shouldBe CommonMessages.errorDateDay
      }
    }

    "month is not valid" should {

      val data = Map(
        DateForm.day -> "1",
        DateForm.month -> "13",
        DateForm.year -> "2018"
      )
      val form = DeregistrationDateForm.form.bind(data)

      "return an error" in {
        form.hasErrors shouldBe true
      }

      "return an error message" in {
        Messages(form.errors.head.message) shouldBe CommonMessages.errorDateMonth
      }
    }

    "year is not a valid amount of characters" should {

      val data = Map(
        DateForm.day -> "1",
        DateForm.month -> "1",
        DateForm.year -> "2"
      )
      val form = DeregistrationDateForm.form.bind(data)

      "return an error" in {
        form.hasErrors shouldBe true
      }

      "return an error message" in {
        Messages(form.errors.head.message) shouldBe CommonMessages.errorDateYear
      }
    }

    "a date too far in the future is entered" should {

      val testDate = LocalDate.now.plusMonths(3).plusDays(1)
      val data = Map(
        DateForm.day -> testDate.getDayOfMonth.toString,
        DateForm.month -> testDate.getMonthValue.toString,
        DateForm.year -> testDate.getYear.toString
      )
      val form = DeregistrationDateForm.form.bind(data)

      "result in a form with no errors" in {
        form.hasErrors shouldBe true
      }

      "return an error message" in {
        Messages(form.errors.head.message) shouldBe "The cancellation date must not be more than 3 months from today"
      }
    }
  }

  "a date in the past is entered" should {

    val testDate = LocalDate.now.minusDays(1)
    val data = Map(
      DateForm.day -> testDate.getDayOfMonth.toString,
      DateForm.month -> testDate.getMonthValue.toString,
      DateForm.year -> testDate.getYear.toString
    )
    val form = DeregistrationDateForm.form.bind(data)

    "result in a form with no errors" in {
      form.hasErrors shouldBe true
    }

    "return an error message" in {
      Messages(form.errors.head.message) shouldBe "The cancellation date must be in the future"
    }
  }
}