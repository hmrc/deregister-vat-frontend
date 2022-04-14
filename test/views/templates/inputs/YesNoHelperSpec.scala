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

import forms.YesNoForm
import models.YesNo
import play.api.data.Form
import play.twirl.api.Html
import views.html.templates.inputs.YesNoHelper
import views.templates.TemplateBaseSpec

class YesNoHelperSpec extends TemplateBaseSpec {

  lazy val yesNoHelper: YesNoHelper = injector.instanceOf[YesNoHelper]
  val form: Form[YesNo] = YesNoForm.yesNoForm("This is the error message")
  val title = "My page title"

  val legendMarkup: String =
    s"""
       |<legend class="govuk-fieldset__legend govuk-fieldset__legend--l">
       |  <h1 class="govuk-fieldset__heading">
       |    $title
       |  </h1>
       |</legend>
       """.stripMargin

  val inputMarkup: String =
    s"""
       |<div class="govuk-radios govuk-radios--inline">
       |  <div class="govuk-radios__item">
       |    <input class="govuk-radios__input" id="yes_no" name="yes_no" type="radio" value="yes">
       |      <label class="govuk-label govuk-radios__label" for="yes_no">
       |        Yes
       |      </label>
       |  </div>
       |  <div class="govuk-radios__item">
       |    <input class="govuk-radios__input" id="yes_no-2" name="yes_no" type="radio" value="no">
       |      <label class="govuk-label govuk-radios__label" for="yes_no-2">
       |        No
       |      </label>
       |  </div>
       |</div>
        """.stripMargin

  "Generating a YesNo radio helper" when {

    "there is no additional content and no errors" should {

      "render the appropriate radios component" in {
        val expectedMarkup: Html = Html(
          s"""
             |<div class="govuk-form-group">
             |  <fieldset class="govuk-fieldset">
             |    $legendMarkup
             |    $inputMarkup
             |  </fieldset>
             |</div>
             """.stripMargin
        )
        val markup = yesNoHelper(form, title)
        formatHtml(markup) shouldBe formatHtml(expectedMarkup)
      }
    }

    "there is some additional content on the page" should {

      "render the appropriate radios component with hint content" in {
        val expectedMarkup: Html = Html(
          s"""
             |<div class="govuk-form-group">
             |  <fieldset class="govuk-fieldset" aria-describedby="yes_no-hint">
             |    $legendMarkup
             |    <div id="yes_no-hint" class="govuk-hint">
             |      Hint text
             |    </div>
             |    $inputMarkup
             |  </fieldset>
             |</div>
             """.stripMargin
        )
        val hintContent = Some(Html("Hint text"))
        val markup = yesNoHelper(form, title, hintContent)
        formatHtml(markup) shouldBe formatHtml(expectedMarkup)
      }
    }

    "there is an error in the form" should {

      "render the appropriate radios component with an error message" in {
        val expectedMarkup: Html = Html(
          s"""
             |<div class="govuk-form-group govuk-form-group--error">
             |  <fieldset class="govuk-fieldset" aria-describedby="yes_no-error">
             |    $legendMarkup
             |    <span id="yes_no-error" class="govuk-error-message">
             |      <span class="govuk-visually-hidden">Error:</span>
             |      This is the error message
             |    </span>
             |    $inputMarkup
             |  </fieldset>
             |</div>
             """.stripMargin
        )
        val markup = yesNoHelper(form.bind(Map("yes_no" -> "")), title)
        formatHtml(markup) shouldBe formatHtml(expectedMarkup)
      }
    }
  }
}