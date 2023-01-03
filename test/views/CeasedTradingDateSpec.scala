/*
 * Copyright 2023 HM Revenue & Customs
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
    val back = ".govuk-back-link"
    val pageHeading = "#content h1"
    val hint = ".govuk-hint"
    val button = ".govuk-button"
    val dayField = "#dateDay"
    val monthField = "#dateMonth"
    val yearField = "#dateYear"
    val dayText = "[for=dateDay]"
    val monthText = "[for=dateMonth]"
    val yearText = "[for=dateYear]"
    val errorHeading = ".govuk-error-summary h2"
    val errorHeadingLink = ".govuk-error-summary a"
    val errorField = ".govuk-error-message"
  }

  "Rendering the Ceased trading date page" should {

    lazy val view = ceasedTradingDate(DateForm.dateForm)(user,messages,mockConfig)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct document title" in {
      document.title shouldBe CeasedTradingDateMessages.title
    }

    "have the correct back text" in {
      elementText(Selectors.back) shouldBe CommonMessages.back
      element(Selectors.back).attr("href") shouldBe controllers.routes.DeregistrationReasonController.show.url
    }

    "have no error summary heading message being displayed" in {
      document.select(Selectors.errorHeading).isEmpty shouldBe true
    }

    "have the correct page heading" in {
      elementText(Selectors.pageHeading) shouldBe CeasedTradingDateMessages.heading
    }

    "have the correct date hint text" in {
      elementText(Selectors.hint) shouldBe CeasedTradingDateMessages.dateHint
    }

    "have the correct label text for the inputs" in {
      elementText(Selectors.dayText) shouldBe CommonMessages.day
      elementText(Selectors.monthText) shouldBe CommonMessages.month
      elementText(Selectors.yearText) shouldBe CommonMessages.year
    }

    "have the correct continue button text and url" in {
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

    "have the correct document title" in {
      document.title shouldBe s"${CommonMessages.errorPrefix} ${CeasedTradingDateMessages.title}"
    }

    "have an error summary heading message being displayed" in {
      elementText(Selectors.errorHeading) shouldBe CommonMessages.errorHeading
    }

    "have an error summary item linking to the incorrect field" in {
      elementText(Selectors.errorHeadingLink) shouldBe CommonMessages.errorDateDay
      element(Selectors.errorHeadingLink).attr("href") shouldBe Selectors.dayField
    }

    "have an error message being displayed for the fields" in {
      elementText(Selectors.errorField) shouldBe s"${CommonMessages.errorPrefix} ${CommonMessages.errorDateDay}"
    }
  }

  "Rendering the Ceased trading date page with no values" should {

    lazy val view = ceasedTradingDate(DateForm.dateForm.bind(Map(
      "dateDay" -> "",
      "dateMonth" -> "",
      "dateYear" -> ""
    )))
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct document title" in {
      document.title shouldBe s"${CommonMessages.errorPrefix} ${CeasedTradingDateMessages.title}"
    }

    "have an error summary heading message being displayed" in {
      elementText(Selectors.errorHeading) shouldBe CommonMessages.errorHeading
    }

    "have an error summary item that links to the first incorrect field" in {
      elementText(Selectors.errorHeadingLink) shouldBe CeasedTradingDateMessages.errorNoEntry
      element(Selectors.errorHeadingLink).attr("href") shouldBe Selectors.dayField
    }

    "have an error message being displayed for the fields" in {
      elementText(Selectors.errorField) shouldBe s"${CommonMessages.errorPrefix} ${CeasedTradingDateMessages.errorNoEntry}"
    }
  }

  "Rendering the Ceased trading date page with one value missing" should {

    lazy val view = ceasedTradingDate(DateForm.dateForm.bind(Map(
      "dateDay" -> "1",
      "dateMonth" -> "1",
      "dateYear" -> ""
    )))
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct document title" in {
      document.title shouldBe s"${CommonMessages.errorPrefix} ${CeasedTradingDateMessages.title}"
    }

    "have an error heading message being displayed" in {
      elementText(Selectors.errorHeading) shouldBe CommonMessages.errorHeading
    }

    "have an error summary item linking to the incorrect field" in {
      elementText(Selectors.errorHeadingLink) shouldBe CommonMessages.errorDateYear
      element(Selectors.errorHeadingLink).attr("href") shouldBe Selectors.yearField
    }

    "have an error message being displayed for the fields" in {
      elementText(Selectors.errorField) shouldBe s"${CommonMessages.errorPrefix} ${CommonMessages.errorDateYear}"
    }
  }

  "Rendering the Ceased trading date page with one incorrect value" should {

    lazy val view = ceasedTradingDate(DateForm.dateForm.bind(Map(
      "dateDay" -> "1",
      "dateMonth" -> "1",
      "dateYear" -> "0"
    )))
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct document title" in {
      document.title shouldBe s"${CommonMessages.errorPrefix} ${CeasedTradingDateMessages.title}"
    }

    "have an error summary heading message being displayed" in {
      elementText(Selectors.errorHeading) shouldBe CommonMessages.errorHeading
    }

    "have an error summary item linking to the incorrect field" in {
      elementText(Selectors.errorHeadingLink) shouldBe CommonMessages.errorDateYear
      element(Selectors.errorHeadingLink).attr("href") shouldBe Selectors.yearField
    }

    "have an error message being displayed for the fields" in {
      elementText(Selectors.errorField) shouldBe s"${CommonMessages.errorPrefix} ${CommonMessages.errorDateYear}"
    }
  }

  "Rendering the Ceased trading date page with incorrect values" should {

    lazy val view = ceasedTradingDate(DateForm.dateForm.bind(Map(
      "dateDay" -> "0",
      "dateMonth" -> "0",
      "dateYear" -> "0"
    )))
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct document title" in {
      document.title shouldBe s"${CommonMessages.errorPrefix} ${CeasedTradingDateMessages.title}"
    }

    "have an error summary heading message being displayed" in {
      elementText(Selectors.errorHeading) shouldBe CommonMessages.errorHeading
    }

    "have an error summary item that links to the first incorrect field" in {
      elementText(Selectors.errorHeadingLink) shouldBe CeasedTradingDateMessages.errorNoEntry
      element(Selectors.errorHeadingLink).attr("href") shouldBe Selectors.dayField
    }

    "have an error message being displayed for the fields" in {
      elementText(Selectors.errorField) shouldBe s"${CommonMessages.errorPrefix} ${CeasedTradingDateMessages.errorNoEntry}"
    }
  }

  "Rendering the Ceased trading date page with a non numerical character in one field" should {

    lazy val view = ceasedTradingDate(DateForm.dateForm.bind(Map(
      "dateDay" -> "1",
      "dateMonth" -> "a",
      "dateYear" -> "2018"
    )))
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct document title" in {
      document.title shouldBe s"${CommonMessages.errorPrefix} ${CeasedTradingDateMessages.title}"
    }

    "have an error summary heading message being displayed" in {
      elementText(Selectors.errorHeading) shouldBe CommonMessages.errorHeading
    }

    "have an error summary item linking to the incorrect field" in {
      elementText(Selectors.errorHeadingLink) shouldBe CommonMessages.errorDateMonth
      element(Selectors.errorHeadingLink).attr("href") shouldBe Selectors.monthField
    }

    "have an error message being displayed for the field" in {
      elementText(Selectors.errorField) shouldBe s"${CommonMessages.errorPrefix} ${CommonMessages.errorDateMonth}"
    }
  }

  "Rendering the Ceased trading date page with non numerical characters in all fields" should {

    lazy val view = ceasedTradingDate(DateForm.dateForm.bind(Map(
      "dateDay" -> "a",
      "dateMonth" -> "a",
      "dateYear" -> "a"
    )))
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct document title" in {
      document.title shouldBe s"${CommonMessages.errorPrefix} ${CeasedTradingDateMessages.title}"
    }

    "have an error summary heading message being displayed" in {
      elementText(Selectors.errorHeading) shouldBe CommonMessages.errorHeading
    }

    "have an error summary item that links to the first incorrect field" in {
      elementText(Selectors.errorHeadingLink) shouldBe CeasedTradingDateMessages.errorNoEntry
      element(Selectors.errorHeadingLink).attr("href") shouldBe Selectors.dayField
    }

    "have an error message being displayed for the fields" in {
      elementText(Selectors.errorField) shouldBe s"${CommonMessages.errorPrefix} ${CeasedTradingDateMessages.errorNoEntry}"
    }
  }

  "Rendering the Ceased trading date page with an invalid date" should {

    lazy val view = ceasedTradingDate(DateForm.dateForm.bind(Map(
      "dateDay" -> "31",
      "dateMonth" -> "2",
      "dateYear" -> "2018"
    )))
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct document title" in {
      document.title shouldBe s"${CommonMessages.errorPrefix} ${CeasedTradingDateMessages.title}"
    }

    "have an error summary heading message being displayed" in {
      elementText(Selectors.errorHeading) shouldBe CommonMessages.errorHeading
    }

    "have an error summary item linking to the form ID which focuses the first incorrect field" in {
      elementText(Selectors.errorHeadingLink) shouldBe CeasedTradingDateMessages.errorNoEntry
      element(Selectors.errorHeadingLink).attr("href") shouldBe Selectors.dayField
    }

    "have an error message being displayed for the fields" in {
      elementText(Selectors.errorField) shouldBe s"${CommonMessages.errorPrefix} ${CeasedTradingDateMessages.errorNoEntry}"
    }
  }
}

