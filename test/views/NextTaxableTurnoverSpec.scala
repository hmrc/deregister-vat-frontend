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

import assets.messages.{CommonMessages, NextTaxableTurnoverMessages}
import forms.NextTaxableTurnoverForm
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import views.html.NextTaxableTurnover

class NextTaxableTurnoverSpec extends ViewBaseSpec {

  lazy val nextTaxableTurnover: NextTaxableTurnover = injector.instanceOf[NextTaxableTurnover]

  object Selectors {
    val back = ".link-back"
    val pageHeading = "#content h1"

    val button = ".button"
    val errorHeading = "#error-summary-display"
    val error = "#value-error-summary"
  }

  val backLink = controllers.routes.TaxableTurnoverController.show().url

  "Rendering the option to tax page with no errors" should {

    lazy val view = nextTaxableTurnover(NextTaxableTurnoverForm.taxableTurnoverForm,backLink)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct document title" in {
      document.title shouldBe NextTaxableTurnoverMessages.title
    }

    "have the correct back text" in {
      elementText(Selectors.back) shouldBe CommonMessages.back
      element(Selectors.back).attr("href") shouldBe controllers.routes.TaxableTurnoverController.show().url
    }

    "have the correct page heading" in {
      elementText(Selectors.pageHeading) shouldBe NextTaxableTurnoverMessages.heading
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

  "Rendering the option to tax page with no errors when the user selected no on the business activity page" should {

    lazy val view = nextTaxableTurnover(NextTaxableTurnoverForm.taxableTurnoverForm,
      controllers.zeroRated.routes.BusinessActivityController.show().url)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct document title" in {
      document.title shouldBe NextTaxableTurnoverMessages.title
    }

    s"have the correct back text" in {
      elementText(Selectors.back) shouldBe CommonMessages.back
      element(Selectors.back).attr("href") shouldBe controllers.zeroRated.routes.BusinessActivityController.show().url
    }
  }

  "Rendering the option to tax page with no errors when the user selected yes on the business activity page" should {

    lazy val view = nextTaxableTurnover(NextTaxableTurnoverForm.taxableTurnoverForm,
      controllers.zeroRated.routes.SicCodeController.show().url)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct document title" in {
      document.title shouldBe NextTaxableTurnoverMessages.title
    }

    s"have the correct back text" in {
      elementText(Selectors.back) shouldBe CommonMessages.back
      element(Selectors.back).attr("href") shouldBe controllers.zeroRated.routes.SicCodeController.show().url
    }
  }

  "Rendering the option to tax page with errors" should {

    lazy val view = nextTaxableTurnover(NextTaxableTurnoverForm.taxableTurnoverForm.bind(Map("turnover" -> "")),backLink)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct document title" in {
      document.title shouldBe s"${CommonMessages.errorTitlePrefix} ${NextTaxableTurnoverMessages.title}"
    }

    "have the correct back text" in {
      elementText(Selectors.back) shouldBe CommonMessages.back
      element(Selectors.back).attr("href") shouldBe controllers.routes.TaxableTurnoverController.show().url
    }

    "have the correct page heading" in {
      elementText(Selectors.pageHeading) shouldBe NextTaxableTurnoverMessages.heading
    }

    "display the correct error heading" in {
      elementText(Selectors.errorHeading) shouldBe s"${CommonMessages.errorHeading} ${NextTaxableTurnoverMessages.mandatory}"
    }

    "have the correct continue button text and url" in {
      elementText(Selectors.button) shouldBe CommonMessages.continue
    }

    "display the correct error messages" in {
      elementText(Selectors.error) shouldBe NextTaxableTurnoverMessages.mandatory
    }
  }

}
