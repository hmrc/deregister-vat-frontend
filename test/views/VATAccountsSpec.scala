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

package views

import assets.messages.{CommonMessages, VATAccountsMessages}
import forms.VATAccountsForm
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class VATAccountsSpec extends ViewBaseSpec {

  object Selectors {
    val back = ".link-back"
    val pageHeading = "#content h1"
    val methodOption: Int => String = (number: Int) => s"fieldset > div:nth-of-type($number) > label"
    val button = ".button"
    val error = "#accountingMethod-error-summary"
    val accountantsContent = "#content p"
  }

  "Rendering the VAT Accounts page with no errors" should {

    lazy val view = views.html.vatAccounts(VATAccountsForm.vatAccountsForm)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct document title" in {
      document.title shouldBe VATAccountsMessages.title
    }

    s"have the correct back text" in {
      elementText(Selectors.back) shouldBe CommonMessages.back
      element(Selectors.back).attr("href") shouldBe "#"
    }

    s"have the correct page heading" in {
      elementText(Selectors.pageHeading) shouldBe VATAccountsMessages.title
    }

    s"have a sentence regarding users with accountants" in {
      elementText(Selectors.accountantsContent) shouldBe VATAccountsMessages.accountant
    }

    s"have the correct a radio button form with the correct 2 options" in {
      elementText(Selectors.methodOption(1)) shouldBe VATAccountsMessages.standard + " " + VATAccountsMessages.invoice
      elementText(Selectors.methodOption(2)) shouldBe VATAccountsMessages.cash + " " + VATAccountsMessages.payment
    }

    s"have the correct continue button text and url" in {
      elementText(Selectors.button) shouldBe CommonMessages.continue
    }

    "not display an error" in {
      document.select(Selectors.error).isEmpty shouldBe true
    }

  }

  "Rendering the VAT Accounts page with errors" should {

    lazy val view = views.html.vatAccounts(VATAccountsForm.vatAccountsForm.bind(Map("accountingMethod" -> "")))
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct document title" in {
      document.title shouldBe VATAccountsMessages.title
    }

    s"have the correct back text" in {
      elementText(Selectors.back) shouldBe CommonMessages.back
      element(Selectors.back).attr("href") shouldBe "#"
    }

    s"have the correct page heading" in {
      elementText(Selectors.pageHeading) shouldBe VATAccountsMessages.title
    }

    s"have a sentence regarding users with accountants" in {
      elementText(Selectors.accountantsContent) shouldBe VATAccountsMessages.accountant
    }

    s"have the correct a radio button form with the correct 2 options" in {
      elementText(Selectors.methodOption(1)) shouldBe VATAccountsMessages.standard + " " + VATAccountsMessages.invoice
      elementText(Selectors.methodOption(2)) shouldBe VATAccountsMessages.cash + " " + VATAccountsMessages.payment
    }

    s"have the correct continue button text and url" in {
      elementText(Selectors.button) shouldBe CommonMessages.continue
    }

    "display the correct error message" in {
      elementText(Selectors.error) shouldBe VATAccountsMessages.error
    }
  }
}
