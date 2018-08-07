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

import assets.messages.{CommonMessages, OptionTaxMessages}
import forms.YesNoForm
import org.jsoup.Jsoup
import org.jsoup.nodes.Document


class OptionTaxSpec extends ViewBaseSpec {

  "Rendering the option to tax page" should {

    object Selectors {
      val back = ".link-back"
      val pageHeading = "#content h1"
      val text1 = "#content > article > p:nth-child(3)"
      val text2 = "#content > article > p:nth-child(4)"
      val yesOption = "fieldset > div:nth-of-type(1) > label"
      val noOption = "fieldset > div:nth-of-type(2) > label"
      val button = ".button"
    }


    lazy val view = views.html.optionTax(YesNoForm.yesNoForm)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct document title" in {
      document.title shouldBe OptionTaxMessages.title
    }

    s"have the correct back text" in {
      elementText(Selectors.back) shouldBe CommonMessages.back
      element(Selectors.back).attr("href") shouldBe "#"
    }

    s"have the correct page heading" in {
      elementText(Selectors.pageHeading) shouldBe OptionTaxMessages.title
    }

    s"have the correct content displayed" in {
      elementText(Selectors.text1) shouldBe OptionTaxMessages.text1
      elementText(Selectors.text2) shouldBe OptionTaxMessages.text2
    }

    s"have the correct a radio button form with yes/no answers" in {
      elementText(Selectors.yesOption) shouldBe CommonMessages.yes
      elementText(Selectors.noOption) shouldBe CommonMessages.no
    }

    s"have the correct continue button text and url" in {
      elementText(Selectors.button) shouldBe CommonMessages.continue
    }
  }
}
