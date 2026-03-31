/*
 * Copyright 2026 HM Revenue & Customs
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
import messages.OptionTaxValueMessages
import models.NumberInputModel
import play.api.i18n.Messages

class OptionTaxValueFormSpec extends TestUtil {

  "Binding a form with valid data" should {
    val data = Map("amount" -> "£1000.01")
    val form = OptionTaxValueForm.optionTaxValueForm.bind(data)

    "result in a form with no errors" in {
      form.hasErrors shouldBe false
    }

    "strip generic formatting and generate the correct model" in {
      form.value shouldBe Some(NumberInputModel(BigDecimal(1000.01)))
    }

  }

  "Binding a form with invalid data" when {
    "no amount has been input" should {
      val missingInput: Map[String, String] = Map.empty
      val form = OptionTaxValueForm.optionTaxValueForm.bind(missingInput)

      "result in a form with errors" in {
        form.hasErrors shouldBe true
      }

      "throw the mandatory data error" in {
        Messages(form.errors.head.message) shouldBe OptionTaxValueMessages.mandatoryError
      }

    }

    "non-numeric input is supplied" should {
      val invalidInput: Map[String, String] = Map("amount" -> "ABC")
      val form = OptionTaxValueForm.optionTaxValueForm.bind(invalidInput)

      "result in a form with errors" in {
        form.hasErrors shouldBe true
      }

      "throw the non-numeric error" in {
        Messages(form.errors.head.message) shouldBe OptionTaxValueMessages.nonNumericError
      }
    }

    "negative input is supplied" should {
      val negativeInput: Map[String, String] = Map("amount" -> "-1")
      val form = OptionTaxValueForm.optionTaxValueForm.bind(negativeInput)


      "result in a form with errors" in {
        form.hasErrors shouldBe true
      }

      "throw the negative error" in {
        Messages(form.errors.head.message) shouldBe OptionTaxValueMessages.negativeError
      }
    }

    "too many decimal places are input" should {
      val tooManyDecimalsInput: Map[String, String] = Map("amount" -> "0.001")
      val form = OptionTaxValueForm.optionTaxValueForm.bind(tooManyDecimalsInput)

      "result in a form with errors" in {
        form.hasErrors shouldBe true
      }

      "throw the non-numeric error" in {
        Messages(form.errors.head.message) shouldBe CommonMessages.errorTooManyDecimals
      }
    }

  }

  "A form built from a valid model" should {

    "generate the correct mapping" in {
      val model = NumberInputModel(BigDecimal(1000.01))
      val form = OptionTaxValueForm.optionTaxValueForm.fill(model)
      form.data shouldBe Map("amount" -> "1000.01")
    }
  }
}
