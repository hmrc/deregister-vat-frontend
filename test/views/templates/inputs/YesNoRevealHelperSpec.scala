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

package views.templates.inputs

import forms.YesNoAmountForm
import models.YesNoAmountModel
import play.api.data.Form
import play.twirl.api.Html
import views.html.templates.inputs.YesNoRevealHelper
import views.templates.TemplateBaseSpec

class YesNoRevealHelperSpec extends TemplateBaseSpec {

  lazy val yesNoRevealHelper: YesNoRevealHelper = injector.instanceOf[YesNoRevealHelper]
  val form: Form[YesNoAmountModel] = YesNoAmountForm.yesNoAmountForm("This is the error message", "0")
  val title = "My page title"
  val hintText = "Hint text"
  val currencyLabelText = "What is the amount?"

  val legendMarkup: String =
    s"""
       |<legend class="govuk-fieldset__legend govuk-fieldset__legend--l">
       |  <h1 class="govuk-fieldset__heading">
       |    $title
       |  </h1>
       |</legend>
       """.stripMargin

  val hintMarkup: String =
    s"""
       |<div id="yes_no-hint" class="govuk-hint">
       |  $hintText
       |</div>
       """.stripMargin

  val inputMarkup: String =
    s"""
       |<div class="govuk-radios" data-module="govuk-radios">
       |  <div class="govuk-radios__item">
       |    <input class="govuk-radios__input" id="yes_no" name="yes_no" type="radio" value="yes" data-aria-controls="conditional-yes_no">
       |      <label class="govuk-label govuk-radios__label" for="yes_no">
       |        Yes
       |      </label>
       |  </div>
       |  <div class="govuk-radios__conditional govuk-radios__conditional--hidden" id="conditional-yes_no">
       |    <div class="govuk-form-group"> <label class="govuk-label" for="amount"> What is the amount? </label>
       |     <div class="govuk-input__wrapper"><div class="govuk-input__prefix" aria-hidden="true">£</div>
       |      <input class="govuk-input govuk-input--width-10" id="amount" name="amount" type="text"></div>
       |    </div>
       |  </div>
       |  <div class="govuk-radios__item">
       |    <input class="govuk-radios__input" id="yes_no-2" name="yes_no" type="radio" value="no">
       |      <label class="govuk-label govuk-radios__label" for="yes_no-2">
       |        No
       |      </label>
       |  </div>
       |</div>
        """.stripMargin

  "Generating a YesNoReveal helper" when {

    "there are no errors" should {

      "render the appropriate radios component with a hidden currency input" in {
        val expectedMarkup: Html = Html(
          s"""
             |<div class="govuk-form-group">
             |  <fieldset class="govuk-fieldset" aria-describedby="yes_no-hint">
             |    $legendMarkup
             |    $hintMarkup
             |    $inputMarkup
             |  </fieldset>
             |</div>
             """.stripMargin
        )
        val markup = yesNoRevealHelper(form, title, hintText, currencyLabelText)
        formatHtml(markup) shouldBe formatHtml(expectedMarkup)
      }
    }

    "there is an error in the radio input" should {

      "render the appropriate radios component with an error message" in {
        val expectedMarkup: Html = Html(
          s"""
             |<div class="govuk-form-group govuk-form-group--error">
             |  <fieldset class="govuk-fieldset" aria-describedby="yes_no-hint yes_no-error">
             |    $legendMarkup
             |    $hintMarkup
             |    <p id="yes_no-error" class="govuk-error-message">
             |      <span class="govuk-visually-hidden">Error:</span>
             |      This is the error message
             |    </p>
             |    $inputMarkup
             |  </fieldset>
             |</div>
             """.stripMargin
        )
        val markup = yesNoRevealHelper(form.bind(Map("yes_no" -> "")), title, hintText, currencyLabelText)
        formatHtml(markup) shouldBe formatHtml(expectedMarkup)
      }
    }

    "there is an error with the currency input" should {

      "render the appropriate radios component with the visible currency input and an error message" in {
        val expectedMarkup: Html = Html(
          s"""
             |<div class="govuk-form-group govuk-form-group--error">
             |  <fieldset class="govuk-fieldset" aria-describedby="yes_no-hint yes_no-error">
             |    $legendMarkup
             |    $hintMarkup
             |    <p id="yes_no-error" class="govuk-error-message">
             |      <span class="govuk-visually-hidden">Error:</span>
             |      This is the error message
             |    </p>
             |    $inputMarkup
             |  </fieldset>
             |</div>
             """.stripMargin
        )
        val markup = yesNoRevealHelper(form.bind(Map("amount" -> "")), title, hintText, currencyLabelText)
        formatHtml(markup) shouldBe formatHtml(expectedMarkup)
      }
    }
  }
}