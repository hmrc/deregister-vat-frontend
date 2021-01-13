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

import assets.messages.VATAccountsMessages
import models.{CashAccounting, StandardAccounting, VATAccountsModel}
import play.api.i18n.Messages
import _root_.utils.TestUtil

class VATAccountsFormSpec extends TestUtil {

  "Binding a form with valid data" should {

    val data = Map(VATAccountsModel.id -> StandardAccounting.value)
    val form = VATAccountsForm.vatAccountsForm.bind(data)

    "result in a form with no errors" in {
      form.hasErrors shouldBe false
    }

    "generate the correct model" in {
      form.value shouldBe Some(StandardAccounting)
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
        Messages(form.errors.head.message) shouldBe VATAccountsMessages.yesNoError
      }
    }
  }

  "A form built from a valid model" when {

    "Standard Accounting" should {

      "generate the correct mapping" in {
        val form = VATAccountsForm.vatAccountsForm.fill(StandardAccounting)
        form.data shouldBe Map(VATAccountsModel.id -> StandardAccounting.value)
      }
    }

    "Cash Accounting" should {

      "generate the correct mapping" in {
        val form = VATAccountsForm.vatAccountsForm.fill(CashAccounting)
        form.data shouldBe Map(VATAccountsModel.id -> CashAccounting.value)
      }
    }
  }
}
