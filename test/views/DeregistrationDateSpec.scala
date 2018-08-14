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

import assets.messages.{CommonMessages, DeregistrationDateMessages}
import forms.{DateForm, DeregistrationDateForm}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document


class DeregistrationDateSpec extends ViewBaseSpec {

  object Selectors {
    val back = ".link-back"
    val pageHeading = "#content h1"
    val hint = ".form-hint"
    val button = ".button"
    val yesOption = "#yes_no-yes"
    val noOption = "#yes_no-no"
    val yesLabel = "#reason > div > fieldset > div.inline.form-group > div:nth-child(1) > label"
    val noLabel = "#reason > div > fieldset > div.inline.form-group > div:nth-child(1) > label"
    val hiddenForm = "#hiddenContent"
    val dayField = "#dateDay"
    val monthField = "#dateMonth"
    val yearField = "#dateYear"
    val dayText = "#deregistrationDate-fieldset > label.form-group.form-group-day > span"
    val monthText = "#deregistrationDate-fieldset > label.form-group.form-group-month > span"
    val yearText = "#deregistrationDate-fieldset > label.form-group.form-group-year > span"
    val errorHeading = "#error-summary-display"
    val errorField = "#yes_no > div > fieldset > span"
    val errorHiddenField = "#deregistrationDate-fieldset > span.error-message"

  }

  "Rendering the Deregistration date page" should {

    lazy val view = views.html.deregistrationDate(DeregistrationDateForm.deregistrationDateForm)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct document title" in {
      document.title shouldBe DeregistrationDateMessages.title
    }

    s"have the correct back text" in {
      elementText(Selectors.back) shouldBe CommonMessages.back
      element(Selectors.back).attr("href") shouldBe "#"
    }

    "have no error heading message being displayed" in {
      document.select(Selectors.errorHeading).isEmpty shouldBe true
    }

    s"have the correct page heading" in {
      elementText(Selectors.pageHeading) shouldBe DeregistrationDateMessages.title
    }

    s"have the correct a date form but be hidden" in {
      elementText(Selectors.dayText) shouldBe CommonMessages.day
      elementText(Selectors.monthText) shouldBe CommonMessages.month
      elementText(Selectors.yearText) shouldBe CommonMessages.year
      document.select("#hiddenContent").hasClass("js-hidden") shouldBe true
    }

    s"have the correct continue button text and url" in {
      elementText(Selectors.button) shouldBe CommonMessages.continue
    }

    "have no error message being displayed for the fields" in {
      document.select(Selectors.errorField).isEmpty shouldBe true
    }
  }

  "Rendering the Deregistration date page with no already selected" should {

    lazy val view = views.html.deregistrationDate(DeregistrationDateForm.deregistrationDateForm
      .bind(Map("yes_no" -> "no"))
    )
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct document title" in {
      document.title shouldBe DeregistrationDateMessages.title
    }

    "have no error heading message being displayed" in {
      document.select(Selectors.errorHeading).isEmpty shouldBe true
    }

    s"have the date form be hidden" in {
      document.select("#hiddenContent").hasClass("js-hidden") shouldBe true
    }

    "have no error message being displayed for the fields" in {
      document.select(Selectors.errorField).isEmpty shouldBe true
    }
  }

  "Rendering the Deregistration date page with yes already selected and a valid date" should {

    lazy val view = views.html.deregistrationDate(DeregistrationDateForm.deregistrationDateForm
      .bind(Map(
        "yes_no" -> "yes",
        "deregistrationDateDay" -> "1",
        "deregistrationDateMonth" -> "1",
        "deregistrationDateYear" -> "2018"
      ))
    )
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct document title" in {
      document.title shouldBe DeregistrationDateMessages.title
    }

    "have no error heading message being displayed" in {
      document.select(Selectors.errorHeading).isEmpty shouldBe true
    }

    s"have the date form be hidden" in {
      document.select("#hiddenContent").hasClass("js-hidden") shouldBe false
    }

    "have no error message being displayed for the fields" in {
      document.select(Selectors.errorField).isEmpty shouldBe true
    }
  }

  "Rendering the Ceased trading date page with missing first field" should {

    lazy val view = views.html.deregistrationDate(DeregistrationDateForm.deregistrationDateForm.bind(Map(
      "yes_no" -> ""
    )))
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct document title" in {
      document.title shouldBe DeregistrationDateMessages.title
    }

    "have an error heading message being displayed" in {
      elementText(Selectors.errorHeading) shouldBe s"${CommonMessages.errorHeading} ${CommonMessages.errorMandatoryRadioOption}"
    }

    "have an error message being displayed for the fields" in {
      elementText(Selectors.errorField) shouldBe CommonMessages.errorMandatoryRadioOption
    }
  }

  "Rendering the Ceased trading date page with a yes and a missing date field" should {

    lazy val view = views.html.deregistrationDate(DeregistrationDateForm.deregistrationDateForm.bind(Map(
      "yes_no" -> "yes",
      "deregistrationDateDay" -> "",
      "deregistrationDateMonth" -> "",
      "deregistrationDateYear" -> ""
    )))
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct document title" in {
      document.title shouldBe DeregistrationDateMessages.title
    }

    "have an error heading message being displayed" in {
      elementText(Selectors.errorHeading) shouldBe s"${CommonMessages.errorHeading} ${CommonMessages.invalidDate}"
    }

    "have an error message being displayed for the fields" in {
      elementText(Selectors.errorHiddenField) shouldBe CommonMessages.invalidDate
    }
  }
}

