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

import assets.messages.{CeasedTradingDateMessages, CommonMessages}
import forms.DateForm
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import views.html.CeasedTradingDate


class CeasedTradingDateSpec extends ViewBaseSpec {

  lazy val ceasedTradingDate: CeasedTradingDate = injector.instanceOf[CeasedTradingDate]

  object Selectors {
    val back = ".link-back"
    val pageHeading = "#content h1"
    val hint = ".form-hint"
    val button = ".button"
    val dayField = "#dateDay"
    val monthField = "#dateMonth"
    val yearField = "#dateYear"
    val ceasedTradingField = "#ceasedTrading"
    val dayText = "#date-fieldset > div > label.form-group.form-group-day > span"
    val monthText = "#date-fieldset > div > label.form-group.form-group-month > span"
    val yearText = "#date-fieldset > div > label.form-group.form-group-year > span"
    val errorHeading = "#error-summary-heading"
    val errorCeasedTrading = "#ceasedTrading-error-summary"
    val errorDay = "#dateDay-error-summary"
    val errorMonth = "#dateMonth-error-summary"
    val errorYear = "#dateYear-error-summary"
    val errorField = "#date-fieldset > div > span.error-message"
  }

  "Rendering the Ceased trading date page" should {

    lazy val view = ceasedTradingDate(DateForm.dateForm)(user,messages,mockConfig)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct document title" in {
      document.title shouldBe CeasedTradingDateMessages.title
    }

    s"have the correct back text" in {
      elementText(Selectors.back) shouldBe CommonMessages.back
      element(Selectors.back).attr("href") shouldBe controllers.routes.DeregistrationReasonController.show().url
    }

    "have no error heading message being displayed" in {
      document.select(Selectors.errorHeading).isEmpty shouldBe true
    }

    s"have the correct page heading" in {
      elementText(Selectors.pageHeading) shouldBe CeasedTradingDateMessages.heading
    }

    s"have the correct date hint text" in {
      elementText(Selectors.hint) shouldBe CeasedTradingDateMessages.dateHint
    }

    s"have the correct a radio " in {
      elementText(Selectors.dayText) shouldBe CommonMessages.day
      elementText(Selectors.monthText) shouldBe CommonMessages.month
      elementText(Selectors.yearText) shouldBe CommonMessages.year
    }

    s"have the correct continue button text and url" in {
      elementText(Selectors.button) shouldBe CommonMessages.continue
    }

    "have no error message being displayed for the fields" in {
      document.select(Selectors.errorField).isEmpty shouldBe true
    }
  }

  "Rendering the Ceased trading date page with one missing field" should {

    lazy val view = ceasedTradingDate(DateForm.dateForm.bind(Map(
      "dateMonth" -> "1",
      "dateYear" -> "2018"
    )))
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct document title" in {
      document.title shouldBe s"${CommonMessages.errorTitlePrefix} ${CeasedTradingDateMessages.title}"
    }

    "have an error heading message being displayed" in {
      elementText(Selectors.errorHeading) shouldBe CommonMessages.errorHeading
    }

    "have an error for each missing field linking to the correct field" in {
      elementText(Selectors.errorDay) shouldBe CommonMessages.errorDateDay
      element(Selectors.errorDay).attr("href") shouldBe Selectors.dayField
    }

    "have an error message being displayed for the fields" in {
      elementText(Selectors.errorField) shouldBe CommonMessages.errorDateDay
    }
  }

  "Rendering the Ceased trading date page with no values" should {

    lazy val view = ceasedTradingDate(DateForm.dateForm.bind(Map(
      "dateDay" -> "",
      "dateMonth" -> "",
      "dateYear" -> ""
    )))
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct document title" in {
      document.title shouldBe s"${CommonMessages.errorTitlePrefix} ${CeasedTradingDateMessages.title}"
    }

    "have an error heading message being displayed" in {
      elementText(Selectors.errorHeading) shouldBe CommonMessages.errorHeading
    }

    "have an error for all fields and link to the correct field" in {
      elementText(Selectors.errorDay) shouldBe CommonMessages.errorAllFieldsInvalid
      element(Selectors.errorDay).attr("href") shouldBe Selectors.dayField
    }

    "have an error message being displayed for the fields" in {
      elementText(Selectors.errorField) shouldBe CeasedTradingDateMessages.errorNoEntry
    }
  }

  "Rendering the Ceased trading date page with one value missing" should {

    lazy val view = ceasedTradingDate(DateForm.dateForm.bind(Map(
      "dateDay" -> "1",
      "dateMonth" -> "1",
      "dateYear" -> ""
    )))
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct document title" in {
      document.title shouldBe s"${CommonMessages.errorTitlePrefix} ${CeasedTradingDateMessages.title}"
    }

    "have an error heading message being displayed" in {
      elementText(Selectors.errorHeading) shouldBe CommonMessages.errorHeading
    }

    "have an error for each missing field linking to the correct field" in {
      elementText(Selectors.errorYear) shouldBe CommonMessages.errorDateYear
      element(Selectors.errorYear).attr("href") shouldBe Selectors.yearField
    }

    "have an error message being displayed for the fields" in {
      elementText(Selectors.errorField) shouldBe CommonMessages.errorDateYear
    }
  }

  "Rendering the Ceased trading date page with one incorrect value" should {

    lazy val view = ceasedTradingDate(DateForm.dateForm.bind(Map(
      "dateDay" -> "1",
      "dateMonth" -> "1",
      "dateYear" -> "0"
    )))
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct document title" in {
      document.title shouldBe s"${CommonMessages.errorTitlePrefix} ${CeasedTradingDateMessages.title}"
    }

    "have an error heading message being displayed" in {
      elementText(Selectors.errorHeading) shouldBe CommonMessages.errorHeading
    }

    "have an error for each missing field linking to the correct field" in {
      elementText(Selectors.errorYear) shouldBe CommonMessages.errorDateYear
      element(Selectors.errorYear).attr("href") shouldBe Selectors.yearField
    }

    "have an error message being displayed for the fields" in {
      elementText(Selectors.errorField) shouldBe CommonMessages.errorDateYear
    }
  }

  "Rendering the Ceased trading date page with incorrect values" should {

    lazy val view = ceasedTradingDate(DateForm.dateForm.bind(Map(
      "dateDay" -> "0",
      "dateMonth" -> "0",
      "dateYear" -> "0"
    )))
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct document title" in {
      document.title shouldBe s"${CommonMessages.errorTitlePrefix} ${CeasedTradingDateMessages.title}"
    }

    "have an error heading message being displayed" in {
      elementText(Selectors.errorHeading) shouldBe CommonMessages.errorHeading
    }

    "have an error for all fields and link to the correct field" in {
      elementText(Selectors.errorDay) shouldBe CommonMessages.errorAllFieldsInvalid
      element(Selectors.errorDay).attr("href") shouldBe Selectors.dayField
    }

    "have an error message being displayed for the fields" in {
      elementText(Selectors.errorField) shouldBe CeasedTradingDateMessages.errorNoEntry
    }
  }

  "Rendering the Ceased trading date page with a non numerical character" should {

    lazy val view = ceasedTradingDate(DateForm.dateForm.bind(Map(
      "dateDay" -> "1",
      "dateMonth" -> "a",
      "dateYear" -> "2018"
    )))
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct document title" in {
      document.title shouldBe s"${CommonMessages.errorTitlePrefix} ${CeasedTradingDateMessages.title}"
    }

    "have an error heading message being displayed" in {
      elementText(Selectors.errorHeading) shouldBe CommonMessages.errorHeading
    }

    "have an error for each missing field linking to the correct field" in {
      elementText(Selectors.errorMonth) shouldBe CommonMessages.errorDateInvalidCharacters
      element(Selectors.errorMonth).attr("href") shouldBe Selectors.monthField
    }

    "have an error message being displayed for the fields" in {
      elementText(Selectors.errorField) shouldBe CommonMessages.errorDateInvalidCharacters
    }
  }

  "Rendering the Ceased trading date page with non numerical characters" should {

    lazy val view = ceasedTradingDate(DateForm.dateForm.bind(Map(
      "dateDay" -> "a",
      "dateMonth" -> "a",
      "dateYear" -> "a"
    )))
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct document title" in {
      document.title shouldBe s"${CommonMessages.errorTitlePrefix} ${CeasedTradingDateMessages.title}"
    }

    "have an error heading message being displayed" in {
      elementText(Selectors.errorHeading) shouldBe CommonMessages.errorHeading
    }

    "have an error for each missing field linking to the correct field" in {
      elementText(Selectors.errorDay) shouldBe CommonMessages.errorDateInvalidCharacters
      element(Selectors.errorDay).attr("href") shouldBe Selectors.dayField
    }

    "have an error message being displayed for the fields" in {
      elementText(Selectors.errorField) shouldBe CommonMessages.errorDateInvalidCharacters
    }
  }

  "Rendering the Ceased trading date page with an invalid date" should {

    lazy val view = ceasedTradingDate(DateForm.dateForm.bind(Map(
      "dateDay" -> "31",
      "dateMonth" -> "2",
      "dateYear" -> "2018"
    )))
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct document title" in {
      document.title shouldBe s"${CommonMessages.errorTitlePrefix} ${CeasedTradingDateMessages.title}"
    }

    "have an error heading message being displayed" in {
      elementText(Selectors.errorHeading) shouldBe CommonMessages.errorHeading
    }

    "have an error for the incorrect field linking to the correct field" in {
      elementText(Selectors.errorCeasedTrading) shouldBe CeasedTradingDateMessages.errorNoEntry
      element(Selectors.errorCeasedTrading).attr("href") shouldBe Selectors.ceasedTradingField
    }

    "have an error message being displayed for the fields" in {
      elementText(Selectors.errorField) shouldBe CeasedTradingDateMessages.errorNoEntry
    }
  }
}

