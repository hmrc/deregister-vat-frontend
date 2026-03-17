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

package views

import assets.messages.CommonMessages
import forms.OptionTaxValueForm
import messages.OptionTaxValueMessages
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import views.html.OptionTaxValue

class OptionTaxValueSpec extends ViewBaseSpec {
  lazy val optionTaxValue: OptionTaxValue = injector.instanceOf[OptionTaxValue]

  object Selectors {
    val back = ".govuk-back-link"
    val pageHeading = "#content h1"
    val prefix = ".govuk-input__prefix"
    val inputBox = "#amount"
    val text = ".govuk-body"
    val button = ".govuk-button"
    val errorHeading = ".govuk-error-summary"
    val error = ".govuk-error-message"
  }

  "Rendering the option tax value page with no errors" should {
    lazy val view = optionTaxValue(OptionTaxValueForm.optionTaxValueForm)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct document title" in {
      document.title shouldBe OptionTaxValueMessages.title
    }

    "have the correct back text" in {
      elementText(Selectors.back) shouldBe CommonMessages.back
      element(Selectors.back).attr("href") shouldBe controllers.routes.OTTNotificationController.show.url
    }

    "have the correct page heading" in {
      elementText(Selectors.pageHeading) shouldBe OptionTaxValueMessages.heading
    }

    "not display an error heading" in {
      document.select(Selectors.errorHeading).isEmpty shouldBe true
    }

    "have the correct text" in {
      elementText(Selectors.text) shouldBe OptionTaxValueMessages.text
    }

    "have the correct prefix to the input box" in {
      elementText(Selectors.prefix) shouldBe "£"
    }

    "have an empty the input box" in {
      element(Selectors.inputBox).attr("value").isEmpty shouldBe true
    }

    "have the correct continue button text and url" in {
      elementText(Selectors.button) shouldBe CommonMessages.continue
    }

    "not display an error" in {
      document.select(Selectors.error).isEmpty shouldBe true
    }
  }

  "Rendering the option tax value page with no errors when the user inserted a value" should {
    lazy val amount = "1000"
    lazy val view = optionTaxValue(OptionTaxValueForm.optionTaxValueForm.bind(Map("amount" -> amount)))
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct document title" in {
      document.title shouldBe OptionTaxValueMessages.title
    }

    "have the correct back text" in {
      elementText(Selectors.back) shouldBe CommonMessages.back
      element(Selectors.back).attr("href") shouldBe controllers.routes.OTTNotificationController.show.url
    }

    "have the correct page heading" in {
      elementText(Selectors.pageHeading) shouldBe OptionTaxValueMessages.heading
    }

    "not display an error heading" in {
      document.select(Selectors.errorHeading).isEmpty shouldBe true
    }

    "have the correct text" in {
      elementText(Selectors.text) shouldBe OptionTaxValueMessages.text
    }

    "have the correct prefix to the input box" in {
      elementText(Selectors.prefix) shouldBe "£"
    }

    "have the correct amount in the input box" in {
      element(Selectors.inputBox).attr("value") shouldBe amount
    }

    "have the correct continue button text and url" in {
      elementText(Selectors.button) shouldBe CommonMessages.continue
    }

    "not display an error" in {
      document.select(Selectors.error).isEmpty shouldBe true
    }
  }

    "Rendering the option to tax page with errors" should {
      lazy val view = optionTaxValue(OptionTaxValueForm.optionTaxValueForm.bind(Map("amount" -> "amount")))
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have the correct document title" in {
        document.title shouldBe s"${CommonMessages.errorPrefix} ${OptionTaxValueMessages.title}"
      }

      "have the correct back text" in {
        elementText(Selectors.back) shouldBe CommonMessages.back
        element(Selectors.back).attr("href") shouldBe controllers.routes.OTTNotificationController.show.url
      }

      "have the correct prefix to the input box" in {
        elementText(Selectors.prefix) shouldBe "£"
      }

      "have the correct amount in the input box" in {
        element(Selectors.inputBox).attr("value") shouldBe "amount"
      }

      "display the correct error heading" in {
        elementText(Selectors.errorHeading) shouldBe s"${CommonMessages.errorHeading} ${OptionTaxValueMessages.nonNumericError}"
      }

      "have the correct continue button text and url" in {
        elementText(Selectors.button) shouldBe CommonMessages.continue
      }

      "display the correct error messages" in {
        elementText(Selectors.error) shouldBe s"${CommonMessages.errorPrefix} ${OptionTaxValueMessages.nonNumericError}"
      }

    }
}
