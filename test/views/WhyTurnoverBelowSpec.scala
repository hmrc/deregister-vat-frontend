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

import assets.messages.{CommonMessages, WhyTurnoverBelowMessages}
import forms.WhyTurnoverBelowForm
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import views.html.WhyTurnoverBelow


class WhyTurnoverBelowSpec extends ViewBaseSpec {

  lazy val whyTurnoverBelow: WhyTurnoverBelow = injector.instanceOf[WhyTurnoverBelow]

  "Rendering the Why is the Turnover Below page" when {

    object Selectors {
      val back = ".link-back"
      val pageHeading = "#content h1"
      val text1 = "#reason > div:nth-child(1) > fieldset:nth-child(1) > p:nth-child(3)"
      val checkboxOption: Int => String = (number: Int) => s"fieldset > div > div:nth-of-type($number) > label"
      val button = ".button"
      val errorHeading = "#error-summary-display"
      val error = ".error-message"
    }

    "has no errors" should {

      lazy val view = whyTurnoverBelow(WhyTurnoverBelowForm.whyTurnoverBelowForm)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have the correct document title" in {
        document.title shouldBe WhyTurnoverBelowMessages.title
      }

      "have the correct back text" in {
        elementText(Selectors.back) shouldBe CommonMessages.back
        element(Selectors.back).attr("href") shouldBe controllers.routes.NextTaxableTurnoverController.show().url
      }

      "have the correct page heading" in {
        elementText(Selectors.pageHeading) shouldBe WhyTurnoverBelowMessages.heading
      }

      "have the correct first paragraph" in {
        elementText(Selectors.text1) shouldBe WhyTurnoverBelowMessages.text1
      }

      "have the correct a radio button form with the correct 7 options" in {
        elementText(Selectors.checkboxOption(1)) shouldBe WhyTurnoverBelowMessages.reason1
        elementText(Selectors.checkboxOption(2)) shouldBe WhyTurnoverBelowMessages.reason2
        elementText(Selectors.checkboxOption(3)) shouldBe WhyTurnoverBelowMessages.reason3
        elementText(Selectors.checkboxOption(4)) shouldBe WhyTurnoverBelowMessages.reason4
        elementText(Selectors.checkboxOption(5)) shouldBe WhyTurnoverBelowMessages.reason5
        elementText(Selectors.checkboxOption(6)) shouldBe WhyTurnoverBelowMessages.reason6
        elementText(Selectors.checkboxOption(7)) shouldBe WhyTurnoverBelowMessages.reason7
      }

      "have the correct continue button text and url" in {
        elementText(Selectors.button) shouldBe CommonMessages.continue
      }
    }

    "with errors" should {

      lazy val view = whyTurnoverBelow(WhyTurnoverBelowForm.whyTurnoverBelowForm.bind(
        Map("" -> "")
      ))
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "display the correct error heading" in {
        elementText(Selectors.errorHeading) shouldBe s"${CommonMessages.errorHeading} ${WhyTurnoverBelowMessages.errorSummary}"
      }

      "display the error message above the checkbox fields" in {
        elementText(Selectors.error) shouldBe WhyTurnoverBelowMessages.error
      }

    }
  }
}
