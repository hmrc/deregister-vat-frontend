/*
 * Copyright 2024 HM Revenue & Customs
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

import assets.messages.{CommonMessages, SicCodeMessages}
import forms.SicCodeForm
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import views.html.SicCode

class SicCodeSpec extends ViewBaseSpec {

  lazy val sicCode: SicCode = injector.instanceOf[SicCode]

  object Selectors {
    val back = ".govuk-back-link"
    val pageHeading = "#content h1"
    val explanation = "p.govuk-body:nth-child(1)"
    val sicCodeLink = "#find-sic-code"
    val button = ".govuk-button"
    val errorHeading = ".govuk-error-summary"
    val error = ".govuk-error-message"
  }

  "Rendering the SIC Code page with no errors" should {

    lazy val view = sicCode(SicCodeForm.sicCodeForm)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct document title" in {
      document.title shouldBe SicCodeMessages.title
    }

    "have the correct back text" in {
      elementText(Selectors.back) shouldBe CommonMessages.back
      element(Selectors.back).attr("href") shouldBe controllers.zeroRated.routes.BusinessActivityController.show.url
    }

    "have the correct page heading" in {
      elementText(Selectors.pageHeading) shouldBe SicCodeMessages.heading
    }

    "not display an error heading" in {
      document.select(Selectors.errorHeading).isEmpty shouldBe true
    }

    "have the correct explanation text" in {
      elementText(Selectors.explanation) shouldBe SicCodeMessages.explanation
    }

    "have the correct 'find SIC code' text and link" in {
      elementText(Selectors.sicCodeLink) shouldBe SicCodeMessages.findCode
      element(Selectors.sicCodeLink).attr("href") shouldBe mockConfig.govUkFindSicCode
    }

    "have the correct continue button text" in {
      elementText(Selectors.button) shouldBe CommonMessages.continue
    }

    "not display an error" in {
      document.select(Selectors.error).isEmpty shouldBe true
    }
  }

  "Rendering the SIC Code page with errors" should {

    lazy val view = sicCode(SicCodeForm.sicCodeForm.bind(Map("value" -> "")))
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct document title" in {
      document.title shouldBe s"${CommonMessages.errorPrefix} ${SicCodeMessages.title}"
    }

    "have the correct back text" in {
      elementText(Selectors.back) shouldBe CommonMessages.back
      element(Selectors.back).attr("href") shouldBe controllers.zeroRated.routes.BusinessActivityController.show.url
    }

    "have the correct page heading" in {
      elementText(Selectors.pageHeading) shouldBe SicCodeMessages.heading
    }

    "display the correct error heading" in {
      elementText(Selectors.errorHeading) shouldBe s"${CommonMessages.errorHeading} ${SicCodeMessages.invalid}"
    }

    "have the correct explanation text" in {
      elementText(Selectors.explanation) shouldBe SicCodeMessages.explanation
    }

    "have the correct 'find SIC code' text and link" in {
      elementText(Selectors.sicCodeLink) shouldBe SicCodeMessages.findCode
      element(Selectors.sicCodeLink).attr("href") shouldBe mockConfig.govUkFindSicCode
    }

    "have the correct continue button text" in {
      elementText(Selectors.button) shouldBe CommonMessages.continue
    }

    "display the correct error messages" in {
      elementText(Selectors.error) shouldBe s"${CommonMessages.errorPrefix} ${SicCodeMessages.invalid}"
    }
  }
}
