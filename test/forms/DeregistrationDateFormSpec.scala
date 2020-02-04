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

package forms

import java.time.LocalDate

import _root_.utils.TestUtil
import assets.messages.{CommonMessages, DeregistrationDateMessages}
import models.{DateModel, DeregistrationDateModel, No, Yes}
import play.api.i18n.Messages

class DeregistrationDateFormSpec extends TestUtil {

  "Binding a form with valid Yes data" when {

    "the current date is entered" should {

      val data = Map(
        YesNoForm.yesNo -> YesNoForm.yes,
        DateForm.day -> LocalDate.now.getDayOfMonth.toString,
        DateForm.month -> LocalDate.now.getMonthValue.toString,
        DateForm.year -> LocalDate.now.getYear.toString
      )
      val form = DeregistrationDateForm.deregistrationDateForm("Select yes if the business wants to choose the cancellation date").bind(data)

      "result in a form with no errors" in {
        form.hasErrors shouldBe false
      }

      "generate a DateModel" in {
        form.value shouldBe Some(DeregistrationDateModel(Yes,Some(
          DateModel(LocalDate.now.getDayOfMonth,LocalDate.now.getMonthValue,LocalDate.now.getYear)
        )))
      }
    }

    "a valid future date is entered" should {

      val testDate = LocalDate.now.plusMonths(3)
      val data = Map(
        YesNoForm.yesNo -> YesNoForm.yes,
        DateForm.day -> testDate.getDayOfMonth.toString,
        DateForm.month -> testDate.getMonthValue.toString,
        DateForm.year -> testDate.getYear.toString
      )
      val form = DeregistrationDateForm.deregistrationDateForm("Select yes if the business wants to choose the cancellation date").bind(data)

      "result in a form with no errors" in {
        form.hasErrors shouldBe false
      }

      "generate a DateModel" in {
        form.value shouldBe Some(DeregistrationDateModel(Yes,Some(
          DateModel(testDate.getDayOfMonth,testDate.getMonthValue,testDate.getYear)
        )))
      }
    }
  }

  "Binding a form with No and no date" should {

    val data = Map(
      YesNoForm.yesNo -> "no"
    )
    val form = DeregistrationDateForm.deregistrationDateForm("Select yes if the business wants to choose the cancellation date").bind(data)

    "result in a form with no errors" in {
      form.hasErrors shouldBe false
    }

    "generate a DateModel" in {
      form.value shouldBe Some(DeregistrationDateModel(No,None))
    }
  }

  "Binding a form with No and a date" should {

    val data = Map(
      YesNoForm.yesNo -> YesNoForm.no,
      DateForm.day -> LocalDate.now.getDayOfMonth.toString,
      DateForm.month -> LocalDate.now.getMonthValue.toString,
      DateForm.year -> LocalDate.now.getYear.toString
    )
    val form = DeregistrationDateForm.deregistrationDateForm("Select yes if the business wants to choose the cancellation date").bind(data)

    "result in a form with no errors" in {
      form.hasErrors shouldBe false
    }

    "generate a DateModel" in {
      form.value shouldBe Some(DeregistrationDateModel(No,None))
    }
  }

  "Binding a form with No and an invalid date" should {

    val data = Map(
      YesNoForm.yesNo -> YesNoForm.no,
      DateForm.day -> "99",
      DateForm.month -> "99",
      DateForm.year -> "99999"
    )
    val form = DeregistrationDateForm.deregistrationDateForm("Select yes if the business wants to choose the cancellation date").bind(data)

    "result in a form with no errors" in {
      form.hasErrors shouldBe false
    }

    "generate a DateModel" in {
      form.value shouldBe Some(DeregistrationDateModel(No,None))
    }
  }

  "Binding a form with No and an invalid characters" should {

    val data = Map(
      YesNoForm.yesNo -> YesNoForm.no,
      DateForm.day -> "a",
      DateForm.month -> "b",
      DateForm.year -> "c"
    )
    val form = DeregistrationDateForm.deregistrationDateForm("Select yes if the business wants to choose the cancellation date").bind(data)

    "result in a form with no errors" in {
      form.hasErrors shouldBe false
    }

    "generate a DateModel" in {
      form.value shouldBe Some(DeregistrationDateModel(No,None))
    }
  }

  "Binding a form with No and an impossible date" should {

    val data = Map(
      YesNoForm.yesNo -> YesNoForm.no,
      DateForm.day -> "31",
      DateForm.month -> "2",
      DateForm.year -> "2018"
    )
    val form = DeregistrationDateForm.deregistrationDateForm("Select yes if the business wants to choose the cancellation date").bind(data)

    "result in a form with no errors" in {
      form.hasErrors shouldBe false
    }

    "generate a DateModel" in {
      form.value shouldBe Some(DeregistrationDateModel(No,None))
    }
  }

  "Binding a form with No and a past date" should {

    val testDate = LocalDate.now.minusDays(1)
    val data = Map(
      YesNoForm.yesNo -> YesNoForm.no,
      DateForm.day -> testDate.getDayOfMonth.toString,
      DateForm.month -> testDate.getMonthValue.toString,
      DateForm.year -> testDate.getYear.toString
    )
    val form = DeregistrationDateForm.deregistrationDateForm("Select yes if the business wants to choose the cancellation date").bind(data)

    "result in a form with no errors" in {
      form.hasErrors shouldBe false
    }

    "generate a DateModel" in {
      form.value shouldBe Some(DeregistrationDateModel(No,None))
    }
  }

  "Binding a form with No and a future date" should {

    val testDate = LocalDate.now.plusDays(31)
    val data = Map(
      YesNoForm.yesNo -> YesNoForm.no,
      DateForm.day -> testDate.getDayOfMonth.toString,
      DateForm.month -> testDate.getMonthValue.toString,
      DateForm.year -> testDate.getYear.toString
    )
    val form = DeregistrationDateForm.deregistrationDateForm("Select yes if the business wants to choose the cancellation date").bind(data)

    "result in a form with no errors" in {
      form.hasErrors shouldBe false
    }

    "generate a DateModel" in {
      form.value shouldBe Some(DeregistrationDateModel(No,None))
    }
  }

  "Binding a form with invalid data" when {

    "no data has been entered" should {

      val missingOption: Map[String, String] = Map.empty
      val form = DeregistrationDateForm.deregistrationDateForm("Select yes if the business wants to choose the cancellation date").bind(missingOption)

      "result in a form with errors" in {
        form.hasErrors shouldBe true
      }

      "have three error" in {
        form.errors.size shouldBe 1
      }
    }

    "Yes and no date" should {

      val data = Map(
        YesNoForm.yesNo -> "yes",
        DateForm.day -> "",
        DateForm.month -> "",
        DateForm.year -> ""
      )
      val form = DeregistrationDateForm.deregistrationDateForm("Select yes if the business wants to choose the cancellation date").bind(data)

      "result in a form with errors" in {
        form.hasErrors shouldBe true
      }

      "have the correct errors" in {
        Messages(form.errors.head.message) shouldBe CommonMessages.errorDateDay
        Messages(form.errors(1).message) shouldBe CommonMessages.errorDateMonth
        Messages(form.errors(2).message) shouldBe CommonMessages.errorDateYear
        form.errors.size shouldBe 3
      }
    }

    "Yes and no date with a None" should {

      val data = Map(
        YesNoForm.yesNo -> "yes"
      )
      val form = DeregistrationDateForm.deregistrationDateForm("Select yes if the business wants to choose the cancellation date").bind(data)

      "result in a form with errors" in {
        form.hasErrors shouldBe true
      }

      "have the correct errors" in {
        Messages(form.errors.head.message) shouldBe DeregistrationDateMessages.errorInvalidDate
        form.errors.size shouldBe 1
      }
    }

    "Yes and an invalid date" should {

      val data = Map(
        YesNoForm.yesNo -> YesNoForm.yes,
        DateForm.day -> "99",
        DateForm.month -> "99",
        DateForm.year -> "99999"
      )
      val form = DeregistrationDateForm.deregistrationDateForm("Select yes if the business wants to choose the cancellation date").bind(data)

      "result in a form with errors" in {
        form.hasErrors shouldBe true
      }

      "have the correct errors" in {
        Messages(form.errors.head.message) shouldBe CommonMessages.errorDateDay
        Messages(form.errors(1).message) shouldBe CommonMessages.errorDateMonth
        Messages(form.errors(2).message) shouldBe CommonMessages.errorDateYear
        form.errors.size shouldBe 3
      }
    }

    "Yes and invalid characters" should {

      val data = Map(
        YesNoForm.yesNo -> YesNoForm.yes,
        DateForm.day -> "a",
        DateForm.month -> "b",
        DateForm.year -> "c"
      )
      val form = DeregistrationDateForm.deregistrationDateForm("Select yes if the business wants to choose the cancellation date").bind(data)

      "result in a form with no errors" in {
        form.hasErrors shouldBe true
      }

      "generate a DateModel" in {
        Messages(form.errors.head.message) shouldBe CommonMessages.errorDateInvalidCharacters
        Messages(form.errors(1).message) shouldBe CommonMessages.errorDateInvalidCharacters
        Messages(form.errors(2).message) shouldBe CommonMessages.errorDateInvalidCharacters
        form.errors.size shouldBe 3
      }
    }

    "yes and an impossible date" should {
      val data = Map(
        YesNoForm.yesNo -> YesNoForm.yes,
        DateForm.day -> "31",
        DateForm.month -> "2",
        DateForm.year -> "2018"
      )
      val form = DeregistrationDateForm.deregistrationDateForm("Select yes if the business wants to choose the cancellation date").bind(data)

      "result in a form with no errors" in {
        form.hasErrors shouldBe true
      }

      "have the correct errors" in {
        Messages(form.errors.head.message) shouldBe DeregistrationDateMessages.errorInvalidDate
        form.errors.size shouldBe 1
      }
    }

    "Yes and a past" should {

      val testDate = LocalDate.now.minusDays(1)
      val data = Map(
        YesNoForm.yesNo -> YesNoForm.yes,
        DateForm.day -> testDate.getDayOfMonth.toString,
        DateForm.month -> testDate.getMonthValue.toString,
        DateForm.year -> testDate.getYear.toString
      )
      val form = DeregistrationDateForm.deregistrationDateForm("Select yes if the business wants to choose the cancellation date").bind(data)

      "result in a form with no errors" in {
        form.hasErrors shouldBe true
      }

      "have the correct errors" in {
        Messages(form.errors.head.message) shouldBe DeregistrationDateMessages.errorPast
        form.errors.size shouldBe 1
      }
    }

    "Yes and a future date" should {

      val testDate = LocalDate.now.plusMonths(3).plusDays(1)
      val data = Map(
        YesNoForm.yesNo -> YesNoForm.yes,
        DateForm.day -> testDate.getDayOfMonth.toString,
        DateForm.month -> testDate.getMonthValue.toString,
        DateForm.year -> testDate.getYear.toString
      )
      val form = DeregistrationDateForm.deregistrationDateForm("Select yes if the business wants to choose the cancellation date").bind(data)

      "result in a form with no errors" in {
        form.hasErrors shouldBe true
      }

      "have the correct errors" in {
        Messages(form.errors.head.message) shouldBe DeregistrationDateMessages.errorFuture
        form.errors.size shouldBe 1
      }
    }
  }

  "A form built from a valid model" should {

    "generate the correct mapping" in {
      val model = DeregistrationDateModel(Yes,Some(DateModel(
        LocalDate.now.getDayOfMonth,
        LocalDate.now.getMonthValue,
        LocalDate.now.getYear
      )))
      val form = DeregistrationDateForm.deregistrationDateForm("Select yes if the business wants to choose the cancellation date").fill(model)
      form.data shouldBe Map(
        YesNoForm.yesNo -> YesNoForm.yes,
        DateForm.day -> LocalDate.now.getDayOfMonth.toString,
        DateForm.month -> LocalDate.now.getMonthValue.toString,
        DateForm.year -> LocalDate.now.getYear.toString
      )
    }
  }

}
