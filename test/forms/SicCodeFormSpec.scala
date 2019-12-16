/*
 * Copyright 2019 HM Revenue & Customs
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

import play.api.i18n.Messages
import _root_.utils.TestUtil
import assets.messages.{CommonMessages, SicCodeMessages}
import models.NumberInputModel

class SicCodeFormSpec extends TestUtil {

  val validValue: BigDecimal = 54321

  "Binding a form with valid data" should {

    val data = Map("value" -> "54321")
    val form = SicCodeForm.sicCodeForm.bind(data)

    "result in a form with no errors" in {
      form.hasErrors shouldBe false
    }

    "generate the correct model" in {
      form.value shouldBe Some(NumberInputModel(validValue))
    }
  }

  "Binding a form with invalid data" when {

    "no amount has been input" should {

      val missingInput: Map[String, String] = Map.empty
      val form = SicCodeForm.sicCodeForm.bind(missingInput)

      "result in a form with errors" in {
        form.hasErrors shouldBe true
      }

      "throw the mandatory data error" in {
        Messages(form.errors.head.message) shouldBe SicCodeMessages.mandatory
      }
    }

    "non-integer input is supplied" should {

      val form = SicCodeForm.sicCodeForm.bind(Map("value" -> "ABC"))

      "result in a form with errors" in {
        form.hasErrors shouldBe true
      }

      "throw the non-integer error" in {
        Messages(form.errors.head.message) shouldBe CommonMessages.errorMandatoryAmount
      }
    }

    "SicCode length is not equal to 5" should {

      val formMany = SicCodeForm.sicCodeForm.bind(Map("value" -> "123456"))
      val formFew = SicCodeForm.sicCodeForm.bind(Map("value" -> "1234"))

      "result in a form with errors" in {
        formMany.hasErrors shouldBe true
        formFew.hasErrors shouldBe true
      }

      "throw the wrong character length errors" in {
        Messages(formMany.errors.head.message) shouldBe SicCodeMessages.tooMany
        Messages(formFew.errors.head.message) shouldBe SicCodeMessages.tooFew
      }
    }

    "negative input is supplied" should {

      val form = SicCodeForm.sicCodeForm.bind(Map("value" -> "-1234"))

      "result in a form with errors" in {
        form.hasErrors shouldBe true
      }

      "throw the negative error" in {
        Messages(form.errors.head.message) shouldBe CommonMessages.errorNegative
      }
    }
  }

  "A form built from a valid model" should {

    "generate the correct mapping" in {
      val form = SicCodeForm.sicCodeForm.fill(NumberInputModel(validValue))
      form.data shouldBe Map("value" -> "54321")
    }
  }

}
