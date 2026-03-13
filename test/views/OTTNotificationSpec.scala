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
import forms.YesNoForm
import messages.OTTNotificationMessages
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import views.html.OTTNotification

class OTTNotificationSpec extends ViewBaseSpec {

  lazy val ottNotification:OTTNotification = injector.instanceOf[OTTNotification]

  object Selectors {
    val back = ".govuk-back-link"
    val pageHeading = "#content h1"
    val text = "p:nth-of-type(1)"
    val inset1 = ".govuk-inset-text > p:nth-of-type(1)"
    val inset2 = ".govuk-inset-text > p:nth-of-type(2)"
    val yesOption = "fieldset > div > div:nth-of-type(1) > label"
    val noOption = "fieldset > div > div:nth-of-type(2) > label"
    val button = "#main-content .govuk-button"
    val errorHeading = ".govuk-error-summary"
    val error = ".govuk-error-message"
  }

  "Rendering the option to tax page with no errors" should {
    lazy val view = ottNotification(YesNoForm.yesNoForm("ottNotification.error.mandatoryRadioOption"))
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct document title" in {
      document.title() shouldBe OTTNotificationMessages.title
    }

    "have the correct back text" in {
      elementText(Selectors.back) shouldBe CommonMessages.back
      element(Selectors.back).attr("href") shouldBe controllers.routes.OptionTaxController.show.url
    }

    "have the correct page heading" in {
      elementText(Selectors.pageHeading) shouldBe OTTNotificationMessages.heading
    }

    "have the correct page text" in {
      elementText(Selectors.text) shouldBe OTTNotificationMessages.text
    }

    "have the correct first inset paragraph" in {
      elementText(Selectors.inset1) shouldBe OTTNotificationMessages.inset1
    }

    "have the correct second inset paragraph" in {
      elementText(Selectors.inset2) shouldBe OTTNotificationMessages.inset2
    }

    "have the correct a radio button form with yes/no answers" in {
      elementText(Selectors.yesOption) shouldBe CommonMessages.yes
      elementText(Selectors.noOption) shouldBe CommonMessages.no
    }

    "have the correct continue button text and url" in {
      elementText(Selectors.button) shouldBe CommonMessages.continue
    }

    "not display an error" in {
      document.select(Selectors.error).isEmpty shouldBe true
    }
  }

  "Rendering the option to tax page with errors" should {
    lazy val view = ottNotification(YesNoForm.yesNoForm("ottNotification.error.mandatoryRadioOption")
      .bind(Map("yes_no" -> ""))
    )
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct document title" in {
      document.title shouldBe s"${CommonMessages.errorPrefix} ${OTTNotificationMessages.title}"
    }

    "display the correct error heading" in {
      elementText(Selectors.errorHeading) shouldBe s"${CommonMessages.errorHeading} ${OTTNotificationMessages.yesNoError}"
    }

    "display the correct error messages" in {
      elementText(Selectors.error) shouldBe s"${CommonMessages.errorPrefix} ${OTTNotificationMessages.yesNoError}"
    }
  }
}
