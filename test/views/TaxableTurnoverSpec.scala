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

import assets.messages.{CommonMessages, TaxableTurnoverMessages}
import forms.YesNoForm
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import views.html.TaxableTurnover

class TaxableTurnoverSpec extends ViewBaseSpec {

  lazy val taxableTurnover: TaxableTurnover = injector.instanceOf[TaxableTurnover]

  object Selectors {
    val back = ".govuk-back-link"
    val pageHeading = "#content h1"
    val button = ".govuk-button"
    val errorHeading = ".govuk-error-summary"
    val error = ".govuk-error-message"
  }

  "Rendering the option to tax page with no errors" should {

    lazy val view = taxableTurnover(YesNoForm.yesNoForm(
      "taxableTurnover.error.mandatoryRadioOption"))(user,messages,mockConfig)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct document title" in {
      document.title shouldBe TaxableTurnoverMessages.title
    }

    "have the correct back text" in {
      elementText(Selectors.back) shouldBe CommonMessages.back
      element(Selectors.back).attr("href") shouldBe controllers.routes.DeregistrationReasonController.show().url
    }

    "have the correct page heading" in {
      elementText(Selectors.pageHeading) shouldBe TaxableTurnoverMessages.heading
    }

    "not display an error heading" in {
      document.select(Selectors.errorHeading).isEmpty shouldBe true
    }

    "have the correct continue button text and url" in {
      elementText(Selectors.button) shouldBe CommonMessages.continue
    }

    "not display an error" in {
      document.select(Selectors.error).isEmpty shouldBe true
    }
  }

  "Rendering the option to tax page with errors" should {

    lazy val view = taxableTurnover(YesNoForm.yesNoForm(
      "taxableTurnover.error.mandatoryRadioOption","83,000").bind(Map("yes_no" -> "")))(agentUserPrefYes,messages,mockConfig)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct document title" in {
      document.title shouldBe s"${CommonMessages.errorPrefix} ${TaxableTurnoverMessages.agentTitle}"
    }

    "have the correct back text" in {
      elementText(Selectors.back) shouldBe CommonMessages.back
      element(Selectors.back).attr("href") shouldBe controllers.routes.DeregistrationReasonController.show().url
    }

    "have the correct page heading" in {
      elementText(Selectors.pageHeading) shouldBe TaxableTurnoverMessages.heading
    }

    "display the correct error heading" in {
      elementText(Selectors.errorHeading) shouldBe s"${CommonMessages.errorHeading} ${TaxableTurnoverMessages.mandatory}"
    }

    "have the correct continue button text and url" in {
      elementText(Selectors.button) shouldBe CommonMessages.continue
    }

    "display the correct error messages" in {
      elementText(Selectors.error) shouldBe s"${CommonMessages.errorPrefix} ${TaxableTurnoverMessages.mandatory}"
    }
  }
}
