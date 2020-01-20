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

import assets.messages.{CommonMessages, DeregistrationReasonMessages}
import forms.DeregistrationReasonForm


class DeregistrationReasonSpec extends ViewBaseSpec {

  object Selectors {
    val back = ".link-back"
    val pageHeading = "#content h1"
    val reasonOption: Int => String = (number: Int) => s"fieldset > div > div:nth-of-type($number) > label"
    val button = ".button"
    val errorHeading = "#error-summary-display"
    val error = "#content > article > form > div > div > fieldset > span"
  }

  "Rendering the Deregistration reason page" when {

    "zeroRated feature switch is enabled" should {

      lazy val view = {
        mockConfig.features.zeroRatedJourney(true)
        views.html.deregistrationReason(DeregistrationReasonForm.deregistrationReasonForm)
      }
      lazy implicit val document: Document = Jsoup.parse(view.body)

      s"have the correct document title" in {
        document.title shouldBe DeregistrationReasonMessages.title
      }

      s"have the correct back text" in {
        elementText(Selectors.back) shouldBe CommonMessages.back
        element(Selectors.back).attr("href") shouldBe controllers.routes.DeregisterForVATController.redirect().url
      }

      s"have the correct page heading" in {
        elementText(Selectors.pageHeading) shouldBe DeregistrationReasonMessages.heading
      }

      "display the correct error heading" in {
        document.select(Selectors.errorHeading).isEmpty shouldBe true
      }

      s"have the correct a radio button form with the correct 5 options" in {
        elementText(Selectors.reasonOption(1)) shouldBe DeregistrationReasonMessages.reason1
        elementText(Selectors.reasonOption(2)) shouldBe DeregistrationReasonMessages.reason2
        elementText(Selectors.reasonOption(3)) shouldBe DeregistrationReasonMessages.reason3
        elementText(Selectors.reasonOption(4)) shouldBe DeregistrationReasonMessages.reason4
        elementText(Selectors.reasonOption(5)) shouldBe DeregistrationReasonMessages.reason5
      }

      s"have the correct continue button text and url" in {
        elementText(Selectors.button) shouldBe CommonMessages.continue
      }

      "display the correct error messages" in {
        document.select(Selectors.error).isEmpty shouldBe true
      }
    }

    "zeroRated feature switch is disabled" should {

      lazy val view = {
        mockConfig.features.zeroRatedJourney(false)
        views.html.deregistrationReason(DeregistrationReasonForm.deregistrationReasonForm)
      }
      lazy implicit val document: Document = Jsoup.parse(view.body)

      s"have the correct a radio button form with the correct 3 options" in {
        elementText(Selectors.reasonOption(1)) shouldBe DeregistrationReasonMessages.reason1
        elementText(Selectors.reasonOption(2)) shouldBe DeregistrationReasonMessages.reason2
        elementText(Selectors.reasonOption(3)) shouldBe DeregistrationReasonMessages.reason5
      }
    }
  }

  "Rendering the Deregistration reason page with errors" should {

    lazy val view = views.html.deregistrationReason(DeregistrationReasonForm.deregistrationReasonForm.bind(Map("" -> "")))
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct document title" in {
      document.title shouldBe s"${CommonMessages.errorTitlePrefix} ${DeregistrationReasonMessages.title}"
    }

    s"have the correct back text" in {
      elementText(Selectors.back) shouldBe CommonMessages.back
      element(Selectors.back).attr("href") shouldBe controllers.routes.DeregisterForVATController.redirect().url
    }

    s"have the correct page heading" in {
      elementText(Selectors.pageHeading) shouldBe DeregistrationReasonMessages.heading
    }

    "display the correct error heading" in {
      elementText(Selectors.errorHeading) shouldBe s"${CommonMessages.errorHeading} ${CommonMessages.errorMandatoryRadioOption}"
    }

    s"have the correct a radio button form with the correct 5 options" in {
      elementText(Selectors.reasonOption(1)) shouldBe DeregistrationReasonMessages.reason1
      elementText(Selectors.reasonOption(2)) shouldBe DeregistrationReasonMessages.reason2
      elementText(Selectors.reasonOption(3)) shouldBe DeregistrationReasonMessages.reason3
      elementText(Selectors.reasonOption(4)) shouldBe DeregistrationReasonMessages.reason4
      elementText(Selectors.reasonOption(5)) shouldBe DeregistrationReasonMessages.reason5
    }

    s"have the correct continue button text and url" in {
      elementText(Selectors.button) shouldBe CommonMessages.continue
    }

    "display the correct error messages" in {
      elementText(Selectors.error) shouldBe CommonMessages.errorMandatoryRadioOption
    }
  }
}
