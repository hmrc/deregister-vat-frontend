/*
 * Copyright 2023 HM Revenue & Customs
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

import assets.messages.{CapitalAssetsMessages, CommonMessages}
import forms.YesNoAmountForm
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import views.html.CapitalAssets

class CapitalAssetsSpec extends ViewBaseSpec {

  lazy val capitalAssets: CapitalAssets = injector.instanceOf[CapitalAssets]

  object Selectors {
    val back = ".govuk-back-link"
    val pageHeading = "#content h1"
    val text = ".govuk-hint"
    val yesOption = "fieldset > div > div:nth-of-type(1) > label"
    val noOption = "fieldset > div > div:nth-of-type(3) > label"
    val hiddenField = "#conditional-yes_no"
    val hint = "#conditional-yes_no label"
    val button = ".govuk-button"
    val errorHeading = ".govuk-error-summary"
    val error = ".govuk-error-message"
  }

  "Rendering the option to tax page with no errors" should {

    lazy val view = capitalAssets(YesNoAmountForm.yesNoAmountForm(
      "capitalAssets.error.mandatoryRadioOption","capitalAssets.error.amount.noEntry"))
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct document title" in {
      document.title shouldBe CapitalAssetsMessages.title
    }

    "have the correct back text" in {
      elementText(Selectors.back) shouldBe CommonMessages.back
      element(Selectors.back).attr("href") shouldBe controllers.routes.OptionTaxController.show.url
    }

    "have the correct page heading" in {
      elementText(Selectors.pageHeading) shouldBe CapitalAssetsMessages.heading
    }

    "not display an error heading" in {
      document.select(Selectors.errorHeading).isEmpty shouldBe true
    }

    "have the correct content displayed" in {
      elementText(Selectors.text) shouldBe CapitalAssetsMessages.p1
    }

    "have the correct a radio button form with yes/no answers" in {
      elementText(Selectors.yesOption) shouldBe CommonMessages.yes
      elementText(Selectors.noOption) shouldBe CommonMessages.no
    }

    "have the correct hint text for the hidden field and be hidden" in {
      document.select(Selectors.hiddenField).hasClass("govuk-radios__conditional--hidden") shouldBe true
      elementText(Selectors.hint) shouldBe CapitalAssetsMessages.hint
    }

    "have the correct continue button text and url" in {
      elementText(Selectors.button) shouldBe CommonMessages.continue
    }

    "not display an error" in {
      document.select(Selectors.error).isEmpty shouldBe true
    }
  }

  "Rendering the option to tax page with errors" when {

    "nothing was entered" should {

      lazy val view = capitalAssets(YesNoAmountForm.yesNoAmountForm(
        "capitalAssets.error.mandatoryRadioOption","capitalAssets.error.amount.noEntry").bind(Map("yes_no" -> "")))
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have the correct document title" in {
        document.title shouldBe s"${CommonMessages.errorPrefix} ${CapitalAssetsMessages.title}"
      }

      "display the correct error heading" in {
        elementText(Selectors.errorHeading) shouldBe s"${CommonMessages.errorHeading} ${CapitalAssetsMessages.yesNoError}"
      }

      "display the correct error messages" in {
        elementText(Selectors.error) shouldBe s"${CommonMessages.errorPrefix} ${CapitalAssetsMessages.yesNoError}"
      }
    }

    "Yes was entered but no amount" should {

      lazy val view = capitalAssets(YesNoAmountForm.yesNoAmountForm(
        "capitalAssets.error.mandatoryRadioOption","capitalAssets.error.amount.noEntry").bind(Map(
        "yes_no" -> "yes",
        "amount" -> ""
      )))
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have the correct document title" in {
        document.title shouldBe s"${CommonMessages.errorPrefix} ${CapitalAssetsMessages.title}"
      }

      "display the correct error heading" in {
        elementText(Selectors.errorHeading) shouldBe s"${CommonMessages.errorHeading} ${CapitalAssetsMessages.emptyAmount}"
      }
    }
  }
}