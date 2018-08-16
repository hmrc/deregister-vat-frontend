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

import models._
import uk.gov.hmrc.play.test.UnitSpec

class YesNoAmountFormSpec extends UnitSpec {

  "Binding a form with valid Yes data" should {

    val data = Map(
      "yes_no" -> "yes",
      "amount" -> "1000.01"
    )
    val form = YesNoAmountForm.yesNoAmountForm.bind(data)

    "result in a form with no errors" in {
      form.hasErrors shouldBe false
    }

    "generate a YesNoAmountModel" in {
      form.value shouldBe Some(YesNoAmountModel(Yes,Some(BigDecimal(1000.01))))
    }
  }

  "Binding a form with No" when {

    "no amount has been given" should {

      val data = Map(
        "yes_no" -> "no",
        "amount" -> ""
      )
      val form = YesNoAmountForm.yesNoAmountForm.bind(data)

      "result in a form with no errors" in {
        form.hasErrors shouldBe false
      }

      "generate a YesNoAmountModel" in {
        form.value shouldBe Some(YesNoAmountModel(No, None))
      }
    }

    "an amount has been given" should {

      val data = Map(
        "yes_no" -> "no",
        "amount" -> ""
      )
      val form = YesNoAmountForm.yesNoAmountForm.bind(data)

      "result in a form with no errors" in {
        form.hasErrors shouldBe false
      }

      "generate a YesNoAmountModel" in {
        form.value shouldBe Some(YesNoAmountModel(No, None))
      }
    }
  }

  "Binding a form with invalid data" when {

    "no data has been entered" should {

      val missingOption: Map[String, String] = Map.empty
      val form = YesNoAmountForm.yesNoAmountForm.bind(missingOption)

      "result in a form with errors" in {
        form.hasErrors shouldBe true
      }

      "have an error" in {
        form.errors.size shouldBe 1
      }
    }

    "Yes but no amount has been entered" should {

      val missingOption: Map[String, String] = Map(
        "yes_no" -> "yes",
        "amount" -> ""
      )
      val form = YesNoAmountForm.yesNoAmountForm.bind(missingOption)

      "result in a form with errors" in {
        form.hasErrors shouldBe true
      }

      "have an error" in {
        form.errors.size shouldBe 1
      }
    }
  }

  "A form built from a valid model" should {

    "generate the correct mapping" in {
      val model = YesNoAmountModel(Yes,Some(BigDecimal(1000.01)))
      val form = YesNoAmountForm.yesNoAmountForm.fill(model)
      form.data shouldBe Map(
        "yes_no" -> "yes",
        "amount" -> "1000.01"
      )
    }
  }
}
