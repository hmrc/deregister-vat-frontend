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

import assets.messages.{CommonMessages, ZeroRatedSuppliesMessages}
import forms.ZeroRatedSuppliesForm
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import views.html.ZeroRatedSupplies



class ZeroRatedSuppliesSpec extends ViewBaseSpec {

  lazy val zeroRatedSupplies: ZeroRatedSupplies = injector.instanceOf[ZeroRatedSupplies]

  object Selectors {
    val back = ".link-back"
    val pageHeading = "#content h1"

    val button = ".button"
    val errorHeading = "#error-summary-display"
    val error = ".error-message"
  }

  "Rendering the zero rated supplies page with no errors" should {

    lazy val view = zeroRatedSupplies(ZeroRatedSuppliesForm.zeroRatedSuppliesForm)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct document title" in {
      document.title shouldBe ZeroRatedSuppliesMessages.title
    }

    "have the correct back text" in {
      elementText(Selectors.back) shouldBe CommonMessages.back
      element(Selectors.back).attr("href") shouldBe controllers.routes.NextTaxableTurnoverController.show().url
    }

    "have the correct page heading" in {
      elementText(Selectors.pageHeading) shouldBe ZeroRatedSuppliesMessages.heading
    }

    "not display an error heading" in {
      document.select(Selectors.errorHeading).isEmpty shouldBe true
    }

    "have the correct continue button text" in {
      elementText(Selectors.button) shouldBe CommonMessages.continue
    }

    "not display an error" in {
      document.select(Selectors.error).isEmpty shouldBe true
    }
  }

  "Rendering the zero rated supplies page with errors" should {

    lazy val view = zeroRatedSupplies(ZeroRatedSuppliesForm.zeroRatedSuppliesForm.bind(Map("value" -> "")))
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct document title" in {
      document.title shouldBe s"${CommonMessages.errorTitlePrefix} ${ZeroRatedSuppliesMessages.title}"
    }

    "have the correct back text" in {
      elementText(Selectors.back) shouldBe CommonMessages.back
      element(Selectors.back).attr("href") shouldBe controllers.routes.NextTaxableTurnoverController.show().url
    }

    "have the correct page heading" in {
      elementText(Selectors.pageHeading) shouldBe ZeroRatedSuppliesMessages.heading
    }

    "display the correct error heading" in {
      elementText(Selectors.errorHeading) shouldBe s"${CommonMessages.errorHeading} ${ZeroRatedSuppliesMessages.mandatory}"
    }

    "have the correct continue button text" in {
      elementText(Selectors.button) shouldBe CommonMessages.continue
    }

    "display the correct error messages" in {
      elementText(Selectors.error) shouldBe ZeroRatedSuppliesMessages.mandatory
    }
  }
}
