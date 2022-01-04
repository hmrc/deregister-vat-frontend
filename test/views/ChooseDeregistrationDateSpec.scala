/*
 * Copyright 2022 HM Revenue & Customs
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

import assets.messages.{ChooseDeregistrationDateMessages, CommonMessages}
import forms.YesNoForm
import models.Yes
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import views.html.ChooseDeregistrationDate

class ChooseDeregistrationDateSpec extends ViewBaseSpec {

  lazy val chooseDeregistrationDate: ChooseDeregistrationDate = injector.instanceOf[ChooseDeregistrationDate]

  object Selectors {
    val back = ".govuk-back-link"
    val pageHeading = "#content h1"
    val hint = ".form-hint"
    val button = ".govuk-button"
    val yesNoRadio = "#yes_no"
    val yesOption = "#yes_no-yes"
    val noOption = "#yes_no-no"
    val yesLabel = "#reason > div > fieldset > div.inline.form-group > div:nth-child(1) > label"
    val noLabel = "#reason > div > fieldset > div.inline.form-group > div:nth-child(1) > label"
    val hiddenForm = "#conditional-yes_no"
    val dayField = "#dateDay"
    val monthField = "#dateMonth"
    val yearField = "#dateYear"
    val dateForm = "#date-fieldset"
    val dayText = "#date-fieldset > div >label.form-group.form-group-day > span"
    val monthText = "#date-fieldset > div > label.form-group.form-group-month > span"
    val yearText = "#date-fieldset > div> label.form-group.form-group-year > span"
    val errorHeading = ".govuk-error-summary h2"
    val errorInvalidDate = "#date-fieldset-error-summary"
    val errorMessage = ".govuk-error-message"
    val errorLink = ".govuk-error-summary a"
    val errorDay = "#dateDay-error-summary"
    val errorMonth = "#dateMonth-error-summary"
    val errorYear = "#dateYear-error-summary"
  }

  "Rendering the Deregistration date page with a No for outstanding invoices" should {

    lazy val view = chooseDeregistrationDate(None, YesNoForm.yesNoForm("chooseDeregistrationDate.error.mandatoryRadioOption"))
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct document title" in {
      document.title shouldBe ChooseDeregistrationDateMessages.title
    }

    "have a back link to the Issue New Invoices page" in {
      elementText(Selectors.back) shouldBe CommonMessages.back
      element(Selectors.back).attr("href") shouldBe controllers.routes.IssueNewInvoicesController.show().url
    }

    "have no error heading message being displayed" in {
      document.select(Selectors.errorHeading).isEmpty shouldBe true
    }

    "have the correct page heading" in {
      elementText(Selectors.pageHeading) shouldBe ChooseDeregistrationDateMessages.heading
    }

    "have the correct continue button text and url" in {
      elementText(Selectors.button) shouldBe CommonMessages.continue
    }

    "have no error message being displayed for the fields" in {
      document.select(Selectors.errorMessage).isEmpty shouldBe true
    }
  }

  "Rendering the Deregistration date page with a Yes for outstanding invoices" should {

    lazy val view = chooseDeregistrationDate(outstanding = Some(Yes), YesNoForm.yesNoForm("chooseDeregistrationDate.error.mandatoryRadioOption"))
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct document title" in {
      document.title shouldBe ChooseDeregistrationDateMessages.title
    }

    "have a back link to the Outstanding Invoices page" in {
      elementText(Selectors.back) shouldBe CommonMessages.back
      element(Selectors.back).attr("href") shouldBe controllers.routes.OutstandingInvoicesController.show().url
    }
  }

  "Rendering the Deregistration date page with no already selected" should {

    lazy val view = chooseDeregistrationDate(None, YesNoForm.yesNoForm("chooseDeregistrationDate.error.mandatoryRadioOption")
      .bind(Map("yes_no" -> "no"))
    )
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct document title" in {
      document.title shouldBe ChooseDeregistrationDateMessages.title
    }

    "have no error heading message being displayed" in {
      document.select(Selectors.errorHeading).isEmpty shouldBe true
    }

    "have no error message being displayed for the fields" in {
      document.select(Selectors.errorMessage).isEmpty shouldBe true
    }
  }

  "Rendering the deregistration date page with 'yes' already selected" should {

    lazy val view = chooseDeregistrationDate(None, YesNoForm.yesNoForm("chooseDeregistrationDate.error.mandatoryRadioOption").bind(Map(
      YesNoForm.yesNo -> YesNoForm.yes
    )))
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct document title" in {
      document.title shouldBe ChooseDeregistrationDateMessages.title
    }

    "have no error heading message being displayed" in {
      document.select(Selectors.errorHeading).isEmpty shouldBe true
    }

    "have an error message being displayed for the fields" in {
      document.select(Selectors.errorMessage).isEmpty shouldBe true
    }
  }

  "Rendering the deregistration date page with missing field" should {

    lazy val view = chooseDeregistrationDate(None, YesNoForm.yesNoForm(
      "chooseDeregistrationDate.error.mandatoryRadioOption").bind(Map.empty[String,String]))
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct document title" in {
      document.title shouldBe s"${CommonMessages.errorPrefix} ${ChooseDeregistrationDateMessages.title}"
    }

    "have an error heading message being displayed" in {
      elementText(Selectors.errorHeading) shouldBe s"${CommonMessages.errorHeading}"
    }

    "have an error for each missing field linking to the correct field" in {
      elementText(Selectors.errorLink) shouldBe ChooseDeregistrationDateMessages.yesNoError
      element(Selectors.errorLink).attr("href") shouldBe Selectors.yesNoRadio
    }

    "have an error message being displayed for the fields" in {
      elementText(Selectors.errorMessage) shouldBe s"${CommonMessages.errorPrefix} ${ChooseDeregistrationDateMessages.yesNoError}"
    }
  }
}

