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

import _root_.utils.TestUtil
import assets.messages.CommonMessages
import models.DateModel
import play.api.i18n.Messages

class DeregistrationDateFormSpec extends TestUtil {

  "Binding a form with valid data" should {

    val data = Map(
      DateForm.day -> "31",
      DateForm.month -> "12",
      DateForm.year -> "2020"
    )
    val form = DeregistrationDateForm.form.bind(data)

    "return a DateModel" in {
      form.value shouldBe Some(DateModel(31, 12, 2020))
    }

    "have no errors" in {
      form.hasErrors shouldBe false
    }
  }

  "Binding a form with invalid data" when {

    "individual fields are invalid" should {

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
  }
}
