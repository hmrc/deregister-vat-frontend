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

import assets.messages.{CeasedTradingDateMessages, CommonMessages}
import forms.CeasedTradingDateForm
import org.jsoup.Jsoup
import org.jsoup.nodes.Document


class CeasedTradingDateSpec extends ViewBaseSpec {

  "Rendering the Ceased trading date page" should {

    object Selectors {
      val back = ".link-back"
      val pageHeading = "#content h1"
      val hint = ".form-hint"
      val button = ".button"
      val day = "#ceasedTradingDate-fieldset > label.form-group.form-group-day > span"
      val month = "#ceasedTradingDate-fieldset > label.form-group.form-group-month > span"
      val year = "#ceasedTradingDate-fieldset > label.form-group.form-group-year > span"
    }

    lazy val view = views.html.ceasedTradingDate(CeasedTradingDateForm.ceasedTradingDateForm)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct document title" in {
      document.title shouldBe CeasedTradingDateMessages.title
    }

    s"have the correct back text" in {
      elementText(Selectors.back) shouldBe CommonMessages.back
      element(Selectors.back).attr("href") shouldBe "#"
    }

    s"have the correct page heading" in {
      elementText(Selectors.pageHeading) shouldBe CeasedTradingDateMessages.title
    }

    s"have the correct a radio " in {
      elementText(Selectors.day) shouldBe CommonMessages.day
      elementText(Selectors.month) shouldBe CommonMessages.month
      elementText(Selectors.year) shouldBe CommonMessages.year
    }

    s"have the correct continue button text and url" in {
      elementText(Selectors.button) shouldBe CommonMessages.continue
    }
  }
}
