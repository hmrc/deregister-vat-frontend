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

import java.time.LocalDate

import assets.messages.{CommonMessages, DeregistrationDateMessages}
import forms.{DateForm, DeregistrationDateForm, YesNoForm}
import models.{No, Yes}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document


class DeregistrationDateSpec extends ViewBaseSpec {

  object Selectors {
    val back = ".link-back"
    val pageHeading = "#content h1"
    val hint = ".form-hint"
    val button = ".button"
    val yesNoRadio = "#yes_no"
    val yesOption = "#yes_no-yes"
    val noOption = "#yes_no-no"
    val yesLabel = "#reason > div > fieldset > div.inline.form-group > div:nth-child(1) > label"
    val noLabel = "#reason > div > fieldset > div.inline.form-group > div:nth-child(1) > label"
    val hiddenForm = "#hiddenContent"
    val dayField = "#dateDay"
    val monthField = "#dateMonth"
    val yearField = "#dateYear"
    val dateForm = "#date-fieldset"
    val dayText = "#date-fieldset > label.form-group.form-group-day > span"
    val monthText = "#date-fieldset > label.form-group.form-group-month > span"
    val yearText = "#date-fieldset > label.form-group.form-group-year > span"
    val errorHeading = "#error-summary-heading"
    val errorInvalidDate = "#date-fieldset-error-summary"
    val errorField = "#yes_no > div > fieldset > span"
    val errorYesNo = "#yes_no-error-summary"
    val errorHiddenField = "#date-fieldset > span.error-message"
    val errorDay = "#dateDay-error-summary"
    val errorMonth = "#dateMonth-error-summary"
    val errorYear = "#dateYear-error-summary"
  }

  "Rendering the Deregistration date page with a No for outstanding invoices" should {

    lazy val view = views.html.deregistrationDate(None ,DeregistrationDateForm.deregistrationDateForm)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct document title" in {
      document.title shouldBe DeregistrationDateMessages.title
    }

    s"have the a bank link to the Issue New Invoices page" in {
      elementText(Selectors.back) shouldBe CommonMessages.back
      element(Selectors.back).attr("href") shouldBe controllers.routes.IssueNewInvoicesController.show().url
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

  "Rendering the Deregistration date page with a Yes for outstanding invoices" should {

    lazy val view = views.html.deregistrationDate(outstanding = Some(Yes) ,DeregistrationDateForm.deregistrationDateForm)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct document title" in {
      document.title shouldBe DeregistrationDateMessages.title
    }

    s"have the a bank link to the Outstanding Invoices page" in {
      elementText(Selectors.back) shouldBe CommonMessages.back
      element(Selectors.back).attr("href") shouldBe controllers.routes.OutstandingInvoicesController.show().url
    }
  }

  "Rendering the Deregistration date page with no already selected" should {

    lazy val view = views.html.deregistrationDate(None ,DeregistrationDateForm.deregistrationDateForm
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

  "Rendering the Deregistration date page with no already selected and a date entered" should {

    lazy val view = views.html.deregistrationDate(None ,DeregistrationDateForm.deregistrationDateForm
      .bind(Map(
        YesNoForm.yesNo -> YesNoForm.no,
        DateForm.day -> LocalDate.now.getDayOfMonth.toString,
        DateForm.month -> LocalDate.now.getMonthValue.toString,
        DateForm.year -> LocalDate.now.getYear.toString
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
      document.select("#hiddenContent").hasClass("js-hidden") shouldBe true
    }

    "have no error message being displayed for the fields" in {
      document.select(Selectors.errorField).isEmpty shouldBe true
    }
  }

  "Rendering the Deregistration date page with yes already selected and a valid date" should {

    lazy val view = views.html.deregistrationDate(None ,DeregistrationDateForm.deregistrationDateForm
      .bind(Map(
        YesNoForm.yesNo -> YesNoForm.yes,
        DateForm.day -> LocalDate.now.getDayOfMonth.toString,
        DateForm.month -> LocalDate.now.getMonthValue.toString,
        DateForm.year -> LocalDate.now.getYear.toString
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

  "Rendering the deregistration date page with missing first field" should {

    lazy val view = views.html.deregistrationDate(None ,DeregistrationDateForm.deregistrationDateForm.bind(Map.empty[String,String]))
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct document title" in {
      document.title shouldBe DeregistrationDateMessages.title
    }

    "have an error heading message being displayed" in {
      elementText(Selectors.errorHeading) shouldBe s"${CommonMessages.errorHeading}"
    }
    "have an error for each missing field linking to the correct field" in {
      elementText(Selectors.errorYesNo) shouldBe CommonMessages.errorMandatoryRadioOption
      element(Selectors.errorYesNo).attr("href") shouldBe Selectors.yesNoRadio
    }


    "have an error message being displayed for the fields" in {
      elementText(Selectors.errorField) shouldBe CommonMessages.errorMandatoryRadioOption
    }
  }

  "Rendering the deregistration date page with a yes and one missing date field" should {

    lazy val view = views.html.deregistrationDate(None ,DeregistrationDateForm.deregistrationDateForm.bind(Map(
      YesNoForm.yesNo -> YesNoForm.yes,
      DateForm.day -> "",
      DateForm.month -> LocalDate.now.getMonthValue.toString,
      DateForm.year -> LocalDate.now.getYear.toString
    )))
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct document title" in {
      document.title shouldBe DeregistrationDateMessages.title
    }

    "have an error heading message being displayed" in {
      elementText(Selectors.errorHeading) shouldBe CommonMessages.errorHeading
    }

    "have an error for each missing field linking to the correct field" in {
      elementText(Selectors.errorDay) shouldBe CommonMessages.errorDateDay
      element(Selectors.errorDay).attr("href") shouldBe Selectors.dayField
    }

    "have an error message being displayed for the fields" in {
      elementText(Selectors.errorHiddenField) shouldBe CommonMessages.errorDateDay
    }
  }

  "Rendering the deregistration date page with a yes and no date entered" should {

    lazy val view = views.html.deregistrationDate(None ,DeregistrationDateForm.deregistrationDateForm.bind(Map(
      YesNoForm.yesNo -> YesNoForm.yes,
      DateForm.day -> "",
      DateForm.month -> "",
      DateForm.year -> ""
    )))
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct document title" in {
      document.title shouldBe DeregistrationDateMessages.title
    }

    "have an error heading message being displayed" in {
      elementText(Selectors.errorHeading) shouldBe CommonMessages.errorHeading
    }

    "have an error for each missing field linking to the correct field" in {
      elementText(Selectors.errorDay) shouldBe CommonMessages.errorDateDay
      element(Selectors.errorDay).attr("href") shouldBe Selectors.dayField
      elementText(Selectors.errorMonth) shouldBe CommonMessages.errorDateMonth
      element(Selectors.errorMonth).attr("href") shouldBe Selectors.monthField
      elementText(Selectors.errorYear) shouldBe CommonMessages.errorDateYear
      element(Selectors.errorYear).attr("href") shouldBe Selectors.yearField
    }

    "have an error message being displayed for the fields" in {
      elementText(Selectors.errorHiddenField) shouldBe CommonMessages.invalidDate
    }
  }

  "Rendering the deregistration date page with a yes and one invalid date field entered" should {

    lazy val view = views.html.deregistrationDate(None ,DeregistrationDateForm.deregistrationDateForm.bind(Map(
      YesNoForm.yesNo -> YesNoForm.yes,
      DateForm.day -> LocalDate.now.getDayOfMonth.toString,
      DateForm.month -> "0",
      DateForm.year -> LocalDate.now.getYear.toString
    )))
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct document title" in {
      document.title shouldBe DeregistrationDateMessages.title
    }

    "have an error heading message being displayed" in {
      elementText(Selectors.errorHeading) shouldBe CommonMessages.errorHeading
    }

    "have an error for each missing field linking to the correct field" in {
      elementText(Selectors.errorMonth) shouldBe CommonMessages.errorDateMonth
      element(Selectors.errorMonth).attr("href") shouldBe Selectors.monthField
    }

    "have an error message being displayed for the fields" in {
      elementText(Selectors.errorHiddenField) shouldBe CommonMessages.errorDateMonth
    }
  }

  "Rendering the deregistration date page with a yes and all date fields entered invalid" should {

    lazy val view = views.html.deregistrationDate(None ,DeregistrationDateForm.deregistrationDateForm.bind(Map(
      YesNoForm.yesNo -> YesNoForm.yes,
      DateForm.day -> "0",
      DateForm.month -> "0",
      DateForm.year -> "0"
    )))
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct document title" in {
      document.title shouldBe DeregistrationDateMessages.title
    }

    "have an error heading message being displayed" in {
      elementText(Selectors.errorHeading) shouldBe CommonMessages.errorHeading
    }

    "have an error for each missing field linking to the correct field" in {
      elementText(Selectors.errorMonth) shouldBe CommonMessages.errorDateMonth
      element(Selectors.errorMonth).attr("href") shouldBe Selectors.monthField
    }

    "have an error message being displayed for the fields" in {
      elementText(Selectors.errorHiddenField) shouldBe CommonMessages.invalidDate
    }
  }


  "Rendering the deregistration date page with a no and all date fields entered invalid" should {

    lazy val view = views.html.deregistrationDate(None ,DeregistrationDateForm.deregistrationDateForm.bind(Map(
      YesNoForm.yesNo -> YesNoForm.no,
      DateForm.day -> "0",
      DateForm.month -> "0",
      DateForm.year -> "0"
    )))
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct document title" in {
      document.title shouldBe DeregistrationDateMessages.title
    }

    "have an error heading message being displayed" in {
      document.select(Selectors.errorHeading).isEmpty shouldBe true
    }

    "have an error message being displayed for the fields" in {
      document.select(Selectors.errorHiddenField).isEmpty shouldBe true
    }
  }

  "Rendering the deregistration date page with a yes and incorrect characters in one field" should {

    lazy val view = views.html.deregistrationDate(None ,DeregistrationDateForm.deregistrationDateForm.bind(Map(
      YesNoForm.yesNo -> YesNoForm.yes,
      DateForm.day -> LocalDate.now.getDayOfMonth.toString,
      DateForm.month -> LocalDate.now.getMonthValue.toString,
      DateForm.year -> "a"
    )))
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct document title" in {
      document.title shouldBe DeregistrationDateMessages.title
    }

    "have an error heading message being displayed" in {
      elementText(Selectors.errorHeading) shouldBe CommonMessages.errorHeading
    }

    "have an error for each missing field linking to the correct field" in {
      elementText(Selectors.errorYear) shouldBe CommonMessages.errorDateInvalidCharacters
      element(Selectors.errorYear).attr("href") shouldBe Selectors.yearField
    }

    "have an error message being displayed for the fields" in {
      elementText(Selectors.errorHiddenField) shouldBe CommonMessages.errorDateInvalidCharacters
    }
  }

  "Rendering the deregistration date page with a yes and all date fields entered with invalid characters" should {

    lazy val view = views.html.deregistrationDate(None ,DeregistrationDateForm.deregistrationDateForm.bind(Map(
      YesNoForm.yesNo -> YesNoForm.yes,
      DateForm.day -> "a",
      DateForm.month -> "b",
      DateForm.year -> "c"
    )))
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct document title" in {
      document.title shouldBe DeregistrationDateMessages.title
    }

    "have an error heading message being displayed" in {
      elementText(Selectors.errorHeading) shouldBe CommonMessages.errorHeading
    }

    "have an error for each missing field linking to the correct field" in {
      elementText(Selectors.errorDay) shouldBe CommonMessages.errorDateInvalidCharacters
      element(Selectors.errorDay).attr("href") shouldBe Selectors.dayField
      elementText(Selectors.errorMonth) shouldBe CommonMessages.errorDateInvalidCharacters
      element(Selectors.errorMonth).attr("href") shouldBe Selectors.monthField
      elementText(Selectors.errorYear) shouldBe CommonMessages.errorDateInvalidCharacters
      element(Selectors.errorYear).attr("href") shouldBe Selectors.yearField
    }

    "have an error message being displayed for the fields" in {
      elementText(Selectors.errorHiddenField) shouldBe CommonMessages.invalidDate
    }
  }

  "Rendering the deregistration date page with a no and all date fields entered invalid characters" should {

    lazy val view = views.html.deregistrationDate(None ,DeregistrationDateForm.deregistrationDateForm
      .bind(Map(
        YesNoForm.yesNo -> YesNoForm.no,
        DateForm.day -> "a",
        DateForm.month -> "b",
        DateForm.year -> "c"
      ))
    )
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct document title" in {
      document.title shouldBe DeregistrationDateMessages.title
    }

    "have no error heading message being displayed" in {
      document.select(Selectors.errorHeading).isEmpty shouldBe true
    }

    "have no error message being displayed for the fields" in {
      document.select(Selectors.errorField).isEmpty shouldBe true
    }
  }

  "Rendering the deregistration date page with a yes and an impossible date" should {

    lazy val view = views.html.deregistrationDate(None ,DeregistrationDateForm.deregistrationDateForm.bind(Map(
      YesNoForm.yesNo -> YesNoForm.yes,
      DateForm.day -> "31",
      DateForm.month -> "2",
      DateForm.year -> "2018"
    )))
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct document title" in {
      document.title shouldBe DeregistrationDateMessages.title
    }

    "have an error heading message being displayed" in {
      elementText(Selectors.errorHeading) shouldBe CommonMessages.errorHeading
    }

    "have an error for each missing field linking to the correct field" in {
      elementText(Selectors.errorInvalidDate) shouldBe DeregistrationDateMessages.errorInvalidDate
      element(Selectors.errorInvalidDate).attr("href") shouldBe Selectors.dateForm
    }

    "have an error message being displayed for the fields" in {
      elementText(Selectors.errorHiddenField) shouldBe DeregistrationDateMessages.errorInvalidDate
    }
  }

  "Rendering the deregistration date page with a yes a date in the past" should {

    val testDate = LocalDate.now().minusDays(1)

    lazy val view = views.html.deregistrationDate(None ,DeregistrationDateForm.deregistrationDateForm.bind(Map(
      YesNoForm.yesNo -> YesNoForm.yes,
      DateForm.day -> testDate.getDayOfMonth.toString,
      DateForm.month -> testDate.getMonthValue.toString,
      DateForm.year -> testDate.getYear.toString
    )))
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct document title" in {
      document.title shouldBe DeregistrationDateMessages.title
    }

    "have an error heading message being displayed" in {
      elementText(Selectors.errorHeading) shouldBe CommonMessages.errorHeading
    }

    "have an error for each missing field linking to the correct field" in {
      elementText(Selectors.errorInvalidDate) shouldBe DeregistrationDateMessages.errorPast
      element(Selectors.errorInvalidDate).attr("href") shouldBe Selectors.dateForm
    }

    "have an error message being displayed for the fields" in {
      elementText(Selectors.errorHiddenField) shouldBe DeregistrationDateMessages.errorPast
    }
  }

  "Rendering the deregistration date page with a yes and a date within 3 months" should {

    val testDate = LocalDate.now().plusMonths(3)

    lazy val view = views.html.deregistrationDate(None ,DeregistrationDateForm.deregistrationDateForm.bind(Map(
      YesNoForm.yesNo -> YesNoForm.yes,
      DateForm.day -> testDate.getDayOfMonth.toString,
      DateForm.month -> testDate.getMonthValue.toString,
      DateForm.year -> testDate.getYear.toString
    )))
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct document title" in {
      document.title shouldBe DeregistrationDateMessages.title
    }

    "have an error heading message being displayed" in {
      document.select(Selectors.errorHeading).isEmpty shouldBe true
    }

    "have an error message being displayed for the fields" in {
      document.select(Selectors.errorHiddenField).isEmpty shouldBe true
    }
  }

  "Rendering the deregistration date page with a yes and a date after 3 months" should {

    val testDate = LocalDate.now().plusMonths(3).plusDays(1)

    lazy val view = views.html.deregistrationDate(None ,DeregistrationDateForm.deregistrationDateForm.bind(Map(
      YesNoForm.yesNo -> YesNoForm.yes,
      DateForm.day -> testDate.getDayOfMonth.toString,
      DateForm.month -> testDate.getMonthValue.toString,
      DateForm.year -> testDate.getYear.toString
    )))
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct document title" in {
      document.title shouldBe DeregistrationDateMessages.title
    }

    "have an error heading message being displayed" in {
      elementText(Selectors.errorHeading) shouldBe CommonMessages.errorHeading
    }

    "have an error for each missing field linking to the correct field" in {
      elementText(Selectors.errorInvalidDate) shouldBe DeregistrationDateMessages.errorFuture
      element(Selectors.errorInvalidDate).attr("href") shouldBe Selectors.dateForm
    }

    "have an error message being displayed for the fields" in {
      elementText(Selectors.errorHiddenField) shouldBe DeregistrationDateMessages.errorFuture
    }
  }
}

