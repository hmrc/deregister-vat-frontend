/*
 * Copyright 2021 HM Revenue & Customs
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
import assets.messages.SicCodeMessages
import play.api.i18n.Messages

class SicCodeFormSpec extends TestUtil {

  val validValue: String = "54321"

  "Binding a form with valid data" should {

    val data = Map("value" -> "54321")
    val form = SicCodeForm.sicCodeForm.bind(data)

    "result in a form with no errors" in {
      form.hasErrors shouldBe false
    }

    "generate the correct model" in {
      form.value shouldBe Some(validValue)
    }
  }

  "Binding a form with invalid data" when {

    "no amount has been input" should {

      val form = SicCodeForm.sicCodeForm.bind(Map("value" -> ""))

      "result in a form with errors" in {
        form.hasErrors shouldBe true
      }

      "throw the invalid data error" in {
        Messages(form.errors.head.message) shouldBe SicCodeMessages.invalid
      }
    }

    "non-integer input is supplied" should {

      val form = SicCodeForm.sicCodeForm.bind(Map("value" -> "ABC"))

      "result in a form with errors" in {
        form.hasErrors shouldBe true
      }

      "throw the invalid data error" in {
        Messages(form.errors.head.message) shouldBe SicCodeMessages.invalid
      }
    }

    "length is less than 5" should {

      val formFew = SicCodeForm.sicCodeForm.bind(Map("value" -> "1234"))

      "result in a form with errors" in {
        formFew.hasErrors shouldBe true
      }

      "throw the too few characters error" in {
        Messages(formFew.errors.head.message) shouldBe SicCodeMessages.tooFew
      }
    }

    "length is greater than 5" should {

      val formMany = SicCodeForm.sicCodeForm.bind(Map("value" -> "123456"))

      "result in a form with errors" in {
        formMany.hasErrors shouldBe true
      }

      "throw the too many characters error" in {
        Messages(formMany.errors.head.message) shouldBe SicCodeMessages.tooMany
      }
    }
  }
}
