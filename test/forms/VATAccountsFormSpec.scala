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

import assets.messages.CommonMessages
import models.VATAccountsModel
import play.api.i18n.Messages
import utils.TestUtil

class VATAccountsFormSpec extends TestUtil {

  "Binding a form with valid data" should {

    val data = Map("accountingMethod" -> "Reason")
    val form = VATAccountsForm.vatAccountsForm.bind(data)

    "result in a form with no errors" in {
      form.hasErrors shouldBe false
    }

    "generate the correct model" in {
      form.value shouldBe Some(VATAccountsModel("Reason"))
    }
  }

  "Binding a form with invalid data" when {

    "the no option has been selected" should {

      val missingOption: Map[String, String] = Map.empty
      val form = VATAccountsForm.vatAccountsForm.bind(missingOption)

      "result in a form with errors" in {
        form.hasErrors shouldBe true
      }

      "throw the correct error message" in {
        Messages(form.errors.head.message) shouldBe CommonMessages.errorMandatoryRadioOption
      }
    }
  }

  "A form built from a valid model" should {

    "generate the correct mapping" in {
      val model = VATAccountsModel("Another Reason")
      val form = VATAccountsForm.vatAccountsForm.fill(model)
      form.data shouldBe Map("accountingMethod" -> "Another Reason")
    }
  }

}
