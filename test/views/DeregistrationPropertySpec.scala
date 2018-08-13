/*
 * Copyright 2018 HM Revenue & Customs
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

import assets.messages.{CommonMessages, DeregistrationPropertyMessages}
import forms.YesNoForm
import org.jsoup.Jsoup
import org.jsoup.nodes.Document


class DeregistrationPropertySpec extends ViewBaseSpec {

  object Selectors {
    val back = ".link-back"
    val pageHeading = "#content h1"
    val button = ".button"
    val p1 = "#content p"
    val errorHeading = "#error-summary-display"
    val error = "#yes_no-error-summary"
    val yesOption = "fieldset > div > div:nth-of-type(1) > label"
    val noOption = "fieldset > div > div:nth-of-type(2) > label"
  }

  "Rendering the Deregistration property page with no errors" should {

    lazy val view = views.html.deregistrationProperty(YesNoForm.yesNoForm)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct document title" in {
      document.title shouldBe DeregistrationPropertyMessages.title
    }

    s"have the correct back text" in {
      elementText(Selectors.back) shouldBe CommonMessages.back
      element(Selectors.back).attr("href") shouldBe "#"
    }

    s"have the correct page heading" in {
      elementText(Selectors.pageHeading) shouldBe DeregistrationPropertyMessages.title
    }

    s"have the correct paragraph text" in {
      elementText(Selectors.p1) shouldBe DeregistrationPropertyMessages.p1
    }

    s"have the correct radio button form with yes/no answers" in {
      elementText(Selectors.yesOption) shouldBe CommonMessages.yes
      elementText(Selectors.noOption) shouldBe CommonMessages.no
    }

    s"have the correct continue button text and url" in {
      elementText(Selectors.button) shouldBe CommonMessages.continue
    }
  }

  "Rendering the Deregistration property page with errors" should {

    lazy val view = views.html.deregistrationProperty(YesNoForm.yesNoForm.bind(Map("yes_no" -> "")))
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct document title" in {
      document.title shouldBe DeregistrationPropertyMessages.title
    }

    s"have the correct back text" in {
      elementText(Selectors.back) shouldBe CommonMessages.back
      element(Selectors.back).attr("href") shouldBe "#"
    }

    s"have the correct page heading" in {
      elementText(Selectors.pageHeading) shouldBe DeregistrationPropertyMessages.title
    }

    "display the correct error heading" in {
      elementText(Selectors.errorHeading) shouldBe s"${CommonMessages.errorHeading} ${CommonMessages.errorMandatoryRadioOption}"
    }

    s"have the correct paragraph text" in {
      elementText(Selectors.p1) shouldBe DeregistrationPropertyMessages.p1
    }

    s"have the correct radio button form with yes/no answers" in {
      elementText(Selectors.yesOption) shouldBe CommonMessages.yes
      elementText(Selectors.noOption) shouldBe CommonMessages.no
    }

    s"have the correct continue button text and url" in {
      elementText(Selectors.button) shouldBe CommonMessages.continue
    }

    "display the correct error messages" in {
      elementText(Selectors.error) shouldBe CommonMessages.errorMandatoryRadioOption
    }
  }

}