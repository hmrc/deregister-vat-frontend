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

import _root_.utils.TestUtil
import assets.messages.CommonMessages
import models.{DateModel, DeregistrationDateModel, No, Yes}
import play.api.i18n.Messages

class DeregistrationDateFormSpec extends TestUtil {

  "Binding a form with valid Yes data" should {

    val data = Map(
      YesNoForm.yesNo -> "yes",
      DateForm.day -> "1",
      DateForm.month -> "1",
      DateForm.year -> "2018"
    )
    val form = DeregistrationDateForm.deregistrationDateForm.bind(data)

    "result in a form with no errors" in {
      form.hasErrors shouldBe false
    }

    "generate a DateModel" in {
      form.value shouldBe Some(DeregistrationDateModel(Yes,Some(DateModel(1,1,2018))))
    }
  }

  "Binding a form with No and a date" should {

    val data = Map(
      YesNoForm.yesNo -> "no",
      DateForm.day -> "1",
      DateForm.month -> "1",
      DateForm.year -> "2018"
    )
    val form = DeregistrationDateForm.deregistrationDateForm.bind(data)

    "result in a form with no errors" in {
      form.hasErrors shouldBe false
    }

    "generate a DateModel" in {
      form.value shouldBe Some(DeregistrationDateModel(No,None))
    }
  }

  "Binding a form with No and an invalid date" should {

    val data = Map(
      YesNoForm.yesNo -> "no",
      DateForm.day -> "99",
      DateForm.month -> "99",
      DateForm.year -> "9999"
    )
    val form = DeregistrationDateForm.deregistrationDateForm.bind(data)

    "result in a form with no errors" in {
      form.hasErrors shouldBe false
    }

    "generate a DateModel" in {
      form.value shouldBe Some(DeregistrationDateModel(No,None))
    }
  }

  "Binding a form with No and no date" should {

    val data = Map(
      YesNoForm.yesNo -> "no"
    )
    val form = DeregistrationDateForm.deregistrationDateForm.bind(data)

    "result in a form with no errors" in {
      form.hasErrors shouldBe false
    }

    "generate a DateModel" in {
      form.value shouldBe Some(DeregistrationDateModel(No,None))
    }
  }

  "Binding a form with invalid data" when {

    "no date has been entered" should {

      val missingOption: Map[String, String] = Map.empty
      val form = DeregistrationDateForm.deregistrationDateForm.bind(missingOption)

      "result in a form with errors" in {
        form.hasErrors shouldBe true
      }

      "have three error" in {
        form.errors.size shouldBe 1
      }
    }

    "an invalid date has been entered" should {

      val data = Map(
        YesNoForm.yesNo -> "yes",
        DateForm.day -> "99",
        DateForm.month -> "99",
        DateForm.year -> "99999"
      )
      val form = DeregistrationDateForm.deregistrationDateForm.bind(data)

      "result in a form with errors" in {
        form.hasErrors shouldBe true
      }

      "have three errors" in {
        Messages(form.errors.head.message) shouldBe CommonMessages.errorDateDay
        Messages(form.errors(1).message) shouldBe CommonMessages.errorDateMonth
        Messages(form.errors(2).message) shouldBe CommonMessages.errorDateYear
        form.errors.size shouldBe 3
      }
    }
  }

  "A form built from a valid model" should {

    "generate the correct mapping" in {
      val model = DeregistrationDateModel(Yes,Some(DateModel(1,1,2018)))
      val form = DeregistrationDateForm.deregistrationDateForm.fill(model)
      form.data shouldBe Map(
        YesNoForm.yesNo -> "yes",
        DateForm.day -> "1",
        DateForm.month -> "1",
        DateForm.year -> "2018"
      )
    }
  }

}
