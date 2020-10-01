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
import assets.messages.{CommonMessages, NextTaxableTurnoverMessages}
import common.Constants
import models.NumberInputModel
import play.api.i18n.Messages

class NextTaxableTurnoverFormSpec extends TestUtil {

  "Binding a form with valid data" should {

    val data = Map("amount" -> "1000.01")
    val form = NextTaxableTurnoverForm.taxableTurnoverForm.bind(data)

    "result in a form with no errors" in {
      form.hasErrors shouldBe false
    }

    "generate the correct model" in {
      form.value shouldBe Some(NumberInputModel(BigDecimal(1000.01)))
    }
  }

  "Binding a form with invalid data" when {

    "no amount has been input" should {

      val missingInput: Map[String, String] = Map.empty
      val form = NextTaxableTurnoverForm.taxableTurnoverForm.bind(missingInput)

      "result in a form with errors" in {
        form.hasErrors shouldBe true
      }

      "throw the mandatory data error" in {
        Messages(form.errors.head.message) shouldBe NextTaxableTurnoverMessages.mandatory
      }
    }

    "non-numeric input is supplied" should {

      val form = NextTaxableTurnoverForm.taxableTurnoverForm.bind(Map("amount" -> "ABC"))

      "result in a form with errors" in {
        form.hasErrors shouldBe true
      }

      "throw the non-numeric error" in {
        Messages(form.errors.head.message) shouldBe NextTaxableTurnoverMessages.nonNumeric
      }
    }

    "negative input is supplied" should {

      val form = NextTaxableTurnoverForm.taxableTurnoverForm.bind(Map("amount" -> "-1"))

      "result in a form with errors" in {
        form.hasErrors shouldBe true
      }

      "throw the non-numeric error" in {
        Messages(form.errors.head.message) shouldBe CommonMessages.errorNegative
      }
    }

    "too many decimal places are input" should {

      val form = NextTaxableTurnoverForm.taxableTurnoverForm.bind(Map("amount" -> "0.001"))

      "result in a form with errors" in {
        form.hasErrors shouldBe true
      }

      "throw the non-numeric error" in {
        Messages(form.errors.head.message) shouldBe CommonMessages.errorTooManyDecimals
      }
    }

    "exceeds the maximum" should {

      val form = NextTaxableTurnoverForm.taxableTurnoverForm.bind(Map("amount" -> (Constants.maxAmount + 0.01).toString))

      "result in a form with errors" in {
        form.hasErrors shouldBe true
      }

      "throw a maximum value exceeded error" in {
        Messages(form.errors.head.message, form.errors.head.args: _*) shouldBe CommonMessages.errorTooManyNumbersBeforeDecimal
      }
    }
  }

  "A form built from a valid model" should {

    "generate the correct mapping" in {
      val model = NumberInputModel(BigDecimal(1000.01))
      val form = NextTaxableTurnoverForm.taxableTurnoverForm.fill(model)
      form.data shouldBe Map("amount" -> "1000.01")
    }
  }

}
