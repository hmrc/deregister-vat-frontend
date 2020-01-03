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

package views

import assets.messages.{CommonMessages, VATAccountsMessages}
import forms.VATAccountsForm
import models.{BelowThreshold, Ceased}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class VATAccountsSpec extends ViewBaseSpec {

  object Selectors {
    val back = ".link-back"
    val pageHeading = "#content h1"
    val methodOption: Int => String = (number: Int) => s"fieldset > div:nth-of-type($number) > label"
    val button = ".button"
    val error = "#accountingMethod-error-summary"
    val p1 = "#content > article > p"
    val bullet: Int => String = i => s"#content > article > ul > li:nth-child($i)"
  }

  "Rendering the VAT Accounts page with no errors from the ceased journey" should {

    lazy val view = views.html.vatAccounts(
      controllers.routes.CeasedTradingDateController.show().url,
      VATAccountsForm.vatAccountsForm
    )
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct document title" in {
      document.title shouldBe VATAccountsMessages.title
    }

    s"have the correct back text" in {
      elementText(Selectors.back) shouldBe CommonMessages.back
      element(Selectors.back).attr("href") shouldBe controllers.routes.CeasedTradingDateController.show().url
    }

    s"have the correct page heading" in {
      elementText(Selectors.pageHeading) shouldBe VATAccountsMessages.heading
    }

    s"has the paragraph '${VATAccountsMessages.p1}'" in {
      elementText(Selectors.p1) shouldBe VATAccountsMessages.p1
    }

    s"has the bullet1 '${VATAccountsMessages.bullet1}'" in {
      elementText(Selectors.bullet(1)) shouldBe VATAccountsMessages.bullet1
    }

    s"has the bullet2 '${VATAccountsMessages.bullet2}'" in {
      elementText(Selectors.bullet(2)) shouldBe VATAccountsMessages.bullet2
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

  "Rendering the VAT Accounts page from the BelowThreshold journey with errors" should {

    lazy val view = views.html.vatAccounts(
      controllers.routes.WhyTurnoverBelowController.show().url,
      VATAccountsForm.vatAccountsForm.bind(Map("accountingMethod" -> ""))
    )
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct document title" in {
      document.title shouldBe s"${CommonMessages.errorTitlePrefix} ${VATAccountsMessages.title}"
    }

    s"have the correct back text" in {
      elementText(Selectors.back) shouldBe CommonMessages.back
      element(Selectors.back).attr("href") shouldBe controllers.routes.WhyTurnoverBelowController.show().url
    }

    s"have the correct page heading" in {
      elementText(Selectors.pageHeading) shouldBe VATAccountsMessages.heading
    }

    s"have the correct a radio button form with the correct 2 options" in {
      elementText(Selectors.methodOption(1)) shouldBe VATAccountsMessages.standard + " " + VATAccountsMessages.invoice
      elementText(Selectors.methodOption(2)) shouldBe VATAccountsMessages.cash + " " + VATAccountsMessages.payment
    }

    "display the correct error message" in {
      elementText(Selectors.error) shouldBe CommonMessages.errorMandatoryRadioOption
    }
  }
}
