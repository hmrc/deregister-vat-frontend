/*
 * Copyright 2022 HM Revenue & Customs
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
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import views.html.DeregistrationReason


class DeregistrationReasonSpec extends ViewBaseSpec {

  lazy val deregistrationReason: DeregistrationReason = injector.instanceOf[DeregistrationReason]

  object Selectors {
    val back = ".govuk-back-link"
    val pageHeading = "#content h1"
    val reasonOption: Int => String = (number: Int) => s"fieldset > div > div:nth-of-type($number) > label"
    val button = ".govuk-button"
    val errorHeading = ".govuk-error-summary"
    val error = ".govuk-error-message"
  }

  "Rendering the Deregistration reason page" when {

    lazy val view = deregistrationReason(DeregistrationReasonForm.deregistrationReasonForm)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct document title" in {
      document.title shouldBe DeregistrationReasonMessages.title
    }

    "have the correct back text" in {
      elementText(Selectors.back) shouldBe CommonMessages.back
      element(Selectors.back).attr("href") shouldBe controllers.routes.DeregisterForVATController.show.url
    }

    "have the correct page heading" in {
      elementText(Selectors.pageHeading) shouldBe DeregistrationReasonMessages.heading
    }

    "display the correct error heading" in {
      document.select(Selectors.errorHeading).isEmpty shouldBe true
    }

    "have the correct a radio button form with the correct 5 options" in {
      elementText(Selectors.reasonOption(1)) shouldBe DeregistrationReasonMessages.reason1
      elementText(Selectors.reasonOption(2)) shouldBe DeregistrationReasonMessages.reason2
      elementText(Selectors.reasonOption(3)) shouldBe DeregistrationReasonMessages.reason3
      elementText(Selectors.reasonOption(4)) shouldBe DeregistrationReasonMessages.reason4
      elementText(Selectors.reasonOption(5)) shouldBe DeregistrationReasonMessages.reason5
    }

    "have the correct continue button text and url" in {
      elementText(Selectors.button) shouldBe CommonMessages.continue
    }

    "display the correct error messages" in {
      document.select(Selectors.error).isEmpty shouldBe true
    }
  }

  "Rendering the Deregistration reason page with errors" should {

    lazy val view = deregistrationReason(DeregistrationReasonForm.deregistrationReasonForm.bind(Map("" -> "")))
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct document title" in {
      document.title shouldBe s"${CommonMessages.errorPrefix} ${DeregistrationReasonMessages.title}"
    }

    "have the correct back text" in {
      elementText(Selectors.back) shouldBe CommonMessages.back
      element(Selectors.back).attr("href") shouldBe controllers.routes.DeregisterForVATController.show.url
    }

    "have the correct page heading" in {
      elementText(Selectors.pageHeading) shouldBe DeregistrationReasonMessages.heading
    }

    "display the correct error heading" in {
      elementText(Selectors.errorHeading) shouldBe s"${CommonMessages.errorHeading} ${DeregistrationReasonMessages.yesNoError}"
    }

    "have the correct radio button form with the correct 5 options" in {
      elementText(Selectors.reasonOption(1)) shouldBe DeregistrationReasonMessages.reason1
      elementText(Selectors.reasonOption(2)) shouldBe DeregistrationReasonMessages.reason2
      elementText(Selectors.reasonOption(3)) shouldBe DeregistrationReasonMessages.reason3
      elementText(Selectors.reasonOption(4)) shouldBe DeregistrationReasonMessages.reason4
      elementText(Selectors.reasonOption(5)) shouldBe DeregistrationReasonMessages.reason5
    }

    "have the correct continue button text and url" in {
      elementText(Selectors.button) shouldBe CommonMessages.continue
    }

    "display the correct error messages" in {
      elementText(Selectors.error) shouldBe s"${CommonMessages.errorPrefix} ${DeregistrationReasonMessages.yesNoError}"
    }
  }
}
