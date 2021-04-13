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

package views

import assets.messages.{CommonMessages, PurchasesExceedSuppliesMessages}
import forms.PurchasesExceedSuppliesForm
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import views.html.PurchasesExceedSupplies

class PurchasesExceedSuppliesSpec extends ViewBaseSpec {

  lazy val purchasesExceedSupplies: PurchasesExceedSupplies = injector.instanceOf[PurchasesExceedSupplies]

  object Selectors {
    val back = ".govuk-back-link"
    val pageHeading = "#content h1"
    val forExample = ".govuk-hint"
    val yesOption = "div.govuk-radios__item:nth-child(1) > label:nth-child(2)"
    val noOption = "div.govuk-radios__item:nth-child(2) > label:nth-child(2)"
    val button = ".govuk-button"
    val errorHeading = ".govuk-error-summary"
    val error = ".govuk-error-message"
  }

  "Rendering the Purchases Exceed Supplies page with no errors" should {

    lazy val view = purchasesExceedSupplies(PurchasesExceedSuppliesForm.purchasesExceedSuppliesForm)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct document title" in {
      document.title shouldBe PurchasesExceedSuppliesMessages.title
    }

    "have the correct back text" in {
      elementText(Selectors.back) shouldBe CommonMessages.back
      element(Selectors.back).attr("href") shouldBe controllers.zeroRated.routes.ZeroRatedSuppliesController.show().url
    }

    "have the correct page heading" in {
      elementText(Selectors.pageHeading) shouldBe PurchasesExceedSuppliesMessages.heading
    }

    "not display an error heading" in {
      document.select(Selectors.errorHeading).isEmpty shouldBe true
    }

    "have the correct explanation text" in {
      elementText(Selectors.forExample) shouldBe PurchasesExceedSuppliesMessages.explanation
    }

    "have the correct a radio button form with yes/no answers" in {
      elementText(Selectors.yesOption) shouldBe CommonMessages.yes
      elementText(Selectors.noOption) shouldBe CommonMessages.no
    }

    "have the correct continue button text" in {
      elementText(Selectors.button) shouldBe CommonMessages.continue
    }

    "not display an error" in {
      document.select(Selectors.error).isEmpty shouldBe true
    }
  }

  "Rendering the Purchases Exceed Supplies page with errors" should {

    lazy val view = purchasesExceedSupplies(PurchasesExceedSuppliesForm.purchasesExceedSuppliesForm.bind(Map("yes_no" -> "")))
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct document title" in {
      document.title shouldBe s"${CommonMessages.errorPrefix} ${PurchasesExceedSuppliesMessages.title}"
    }

    "have the correct back text" in {
      elementText(Selectors.back) shouldBe CommonMessages.back
      element(Selectors.back).attr("href") shouldBe controllers.zeroRated.routes.ZeroRatedSuppliesController.show().url
    }

    "have the correct page heading" in {
      elementText(Selectors.pageHeading) shouldBe PurchasesExceedSuppliesMessages.heading
    }

    "display the correct error heading" in {
      elementText(Selectors.errorHeading) shouldBe
        s"${CommonMessages.errorHeading} " +
        s"${PurchasesExceedSuppliesMessages.purchasesExceedSuppliesError}"
    }

    "have the correct explanation text" in {
      elementText(Selectors.forExample) shouldBe PurchasesExceedSuppliesMessages.explanation
    }

    "have the correct a radio button form with yes/no answers" in {
      elementText(Selectors.yesOption) shouldBe CommonMessages.yes
      elementText(Selectors.noOption) shouldBe CommonMessages.no
    }

    "have the correct continue button text" in {
      elementText(Selectors.button) shouldBe CommonMessages.continue
    }

    "display the correct error messages" in {
      elementText(Selectors.error) shouldBe s"${CommonMessages.errorPrefix} ${PurchasesExceedSuppliesMessages.purchasesExceedSuppliesError}"
    }
  }
}
