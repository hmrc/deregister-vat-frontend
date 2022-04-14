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

import forms.DateForm
import models.{CeasedTradingDate, DateModel}
import play.api.data.Form
import play.twirl.api.Html
import views.html.templates.inputs.DateHelper
import views.templates.TemplateBaseSpec

class DateHelperSpec extends TemplateBaseSpec {
  lazy val dateHelper: DateHelper = injector.instanceOf[DateHelper]
  val form: Form[DateModel] = DateForm.dateForm
  val title = "My page title"
  val id = "date"
  val hintContent: Html = Html("Hint text")

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
       |<div id="$id-hint" class="govuk-hint">
       |  ${hintContent.toString()}
       |</div>
       """.stripMargin

  val inputMarkupNoErrors: String =
    s"""
       |<div class="govuk-date-input" id="$id">
       |  <div class="govuk-date-input__item">
       |    <div class="govuk-form-group">
       |      <label class="govuk-label govuk-date-input__label" for="dateDay">
       |        Day
       |      </label>
       |      <input class="govuk-input govuk-date-input__input govuk-input--width-2"
       |             id="dateDay" name="dateDay" type="text" pattern="[0-9]*" inputmode="numeric">
       |    </div>
       |  </div>
       |  <div class="govuk-date-input__item">
       |    <div class="govuk-form-group">
       |      <label class="govuk-label govuk-date-input__label" for="dateMonth">
       |        Month
       |      </label>
       |      <input class="govuk-input govuk-date-input__input govuk-input--width-2"
       |             id="dateMonth" name="dateMonth" type="text" pattern="[0-9]*" inputmode="numeric">
       |    </div>
       |  </div>
       |  <div class="govuk-date-input__item">
       |    <div class="govuk-form-group">
       |      <label class="govuk-label govuk-date-input__label" for="dateYear">
       |        Year
       |      </label>
       |      <input class="govuk-input govuk-date-input__input govuk-input--width-4"
       |             id="dateYear" name="dateYear" type="text" pattern="[0-9]*" inputmode="numeric">
       |    </div>
       |  </div>
       |</div>
       """.stripMargin

  val inputMarkupAllDateFieldsError: String =
    s"""
       |<div class="govuk-date-input" id="$id">
       |  <div class="govuk-date-input__item">
       |    <div class="govuk-form-group">
       |      <label class="govuk-label govuk-date-input__label" for="dateDay">
       |        Day
       |      </label>
       |      <input class="govuk-input govuk-date-input__input govuk-input--width-2 govuk-input--error"
       |             id="dateDay" name="dateDay" type="text" pattern="[0-9]*" inputmode="numeric">
       |    </div>
       |  </div>
       |  <div class="govuk-date-input__item">
       |    <div class="govuk-form-group">
       |      <label class="govuk-label govuk-date-input__label" for="dateMonth">
       |        Month
       |      </label>
       |      <input class="govuk-input govuk-date-input__input govuk-input--width-2 govuk-input--error"
       |             id="dateMonth" name="dateMonth" type="text" pattern="[0-9]*" inputmode="numeric">
       |    </div>
       |  </div>
       |  <div class="govuk-date-input__item">
       |    <div class="govuk-form-group">
       |      <label class="govuk-label govuk-date-input__label" for="dateYear">
       |        Year
       |      </label>
       |      <input class="govuk-input govuk-date-input__input govuk-input--width-4 govuk-input--error"
       |             id="dateYear" name="dateYear" type="text" pattern="[0-9]*" inputmode="numeric">
       |    </div>
       |  </div>
       |</div>
       """.stripMargin

  val inputMarkupDayAndMonthFieldsError: String =
    s"""
       |<div class="govuk-date-input" id="$id">
       |  <div class="govuk-date-input__item">
       |    <div class="govuk-form-group">
       |      <label class="govuk-label govuk-date-input__label" for="dateDay">
       |        Day
       |      </label>
       |      <input class="govuk-input govuk-date-input__input govuk-input--width-2 govuk-input--error"
       |             id="dateDay" name="dateDay" type="text" pattern="[0-9]*" inputmode="numeric">
       |    </div>
       |  </div>
       |  <div class="govuk-date-input__item">
       |    <div class="govuk-form-group">
       |      <label class="govuk-label govuk-date-input__label" for="dateMonth">
       |        Month
       |      </label>
       |      <input class="govuk-input govuk-date-input__input govuk-input--width-2 govuk-input--error"
       |             id="dateMonth" name="dateMonth" type="text" pattern="[0-9]*" inputmode="numeric">
       |    </div>
       |  </div>
       |  <div class="govuk-date-input__item">
       |    <div class="govuk-form-group">
       |      <label class="govuk-label govuk-date-input__label" for="dateYear">
       |        Year
       |      </label>
       |      <input class="govuk-input govuk-date-input__input govuk-input--width-4 govuk-input--error"
       |             id="dateYear" name="dateYear" type="text" value="2020" pattern="[0-9]*" inputmode="numeric">
       |    </div>
       |  </div>
       |</div>
       """.stripMargin

  val inputMarkupDayAndYearFieldsError: String =
    s"""
       |<div class="govuk-date-input" id="$id">
       |  <div class="govuk-date-input__item">
       |    <div class="govuk-form-group">
       |      <label class="govuk-label govuk-date-input__label" for="dateDay">
       |        Day
       |      </label>
       |      <input class="govuk-input govuk-date-input__input govuk-input--width-2 govuk-input--error"
       |             id="dateDay" name="dateDay" type="text" pattern="[0-9]*" inputmode="numeric">
       |    </div>
       |  </div>
       |  <div class="govuk-date-input__item">
       |    <div class="govuk-form-group">
       |      <label class="govuk-label govuk-date-input__label" for="dateMonth">
       |        Month
       |      </label>
       |      <input class="govuk-input govuk-date-input__input govuk-input--width-2 govuk-input--error"
       |             id="dateMonth" name="dateMonth" type="text" value="5" pattern="[0-9]*" inputmode="numeric">
       |    </div>
       |  </div>
       |  <div class="govuk-date-input__item">
       |    <div class="govuk-form-group">
       |      <label class="govuk-label govuk-date-input__label" for="dateYear">
       |        Year
       |      </label>
       |      <input class="govuk-input govuk-date-input__input govuk-input--width-4 govuk-input--error"
       |             id="dateYear" name="dateYear" type="text" pattern="[0-9]*" inputmode="numeric">
       |    </div>
       |  </div>
       |</div>
       """.stripMargin

  val inputMarkupMonthAndYearFieldsError: String =
    s"""
       |<div class="govuk-date-input" id="$id">
       |  <div class="govuk-date-input__item">
       |    <div class="govuk-form-group">
       |      <label class="govuk-label govuk-date-input__label" for="dateDay">
       |        Day
       |      </label>
       |      <input class="govuk-input govuk-date-input__input govuk-input--width-2 govuk-input--error"
       |             id="dateDay" name="dateDay" type="text" value="4" pattern="[0-9]*" inputmode="numeric">
       |    </div>
       |  </div>
       |  <div class="govuk-date-input__item">
       |    <div class="govuk-form-group">
       |      <label class="govuk-label govuk-date-input__label" for="dateMonth">
       |        Month
       |      </label>
       |      <input class="govuk-input govuk-date-input__input govuk-input--width-2 govuk-input--error"
       |             id="dateMonth" name="dateMonth" type="text" pattern="[0-9]*" inputmode="numeric">
       |    </div>
       |  </div>
       |  <div class="govuk-date-input__item">
       |    <div class="govuk-form-group">
       |      <label class="govuk-label govuk-date-input__label" for="dateYear">
       |        Year
       |      </label>
       |      <input class="govuk-input govuk-date-input__input govuk-input--width-4 govuk-input--error"
       |             id="dateYear" name="dateYear" type="text" pattern="[0-9]*" inputmode="numeric">
       |    </div>
       |  </div>
       |</div>
       """.stripMargin

  val inputMarkupOnlyDayError: String =
    s"""
       |<div class="govuk-date-input" id="$id">
       |  <div class="govuk-date-input__item">
       |    <div class="govuk-form-group">
       |      <label class="govuk-label govuk-date-input__label" for="dateDay">
       |        Day
       |      </label>
       |      <input class="govuk-input govuk-date-input__input govuk-input--width-2 govuk-input--error"
       |             id="dateDay" name="dateDay" type="text" pattern="[0-9]*" inputmode="numeric">
       |    </div>
       |  </div>
       |  <div class="govuk-date-input__item">
       |    <div class="govuk-form-group">
       |      <label class="govuk-label govuk-date-input__label" for="dateMonth">
       |        Month
       |      </label>
       |      <input class="govuk-input govuk-date-input__input govuk-input--width-2"
       |             id="dateMonth" name="dateMonth" type="text" value="5" pattern="[0-9]*" inputmode="numeric">
       |    </div>
       |  </div>
       |  <div class="govuk-date-input__item">
       |    <div class="govuk-form-group">
       |      <label class="govuk-label govuk-date-input__label" for="dateYear">
       |        Year
       |      </label>
       |      <input class="govuk-input govuk-date-input__input govuk-input--width-4"
       |             id="dateYear" name="dateYear" type="text" value="2020" pattern="[0-9]*" inputmode="numeric">
       |    </div>
       |  </div>
       |</div>
       """.stripMargin

  val inputMarkupOnlyMonthError: String =
    s"""
       |<div class="govuk-date-input" id="$id">
       |  <div class="govuk-date-input__item">
       |    <div class="govuk-form-group">
       |      <label class="govuk-label govuk-date-input__label" for="dateDay">
       |        Day
       |      </label>
       |      <input class="govuk-input govuk-date-input__input govuk-input--width-2"
       |             id="dateDay" name="dateDay" type="text" value="4" pattern="[0-9]*" inputmode="numeric">
       |    </div>
       |  </div>
       |  <div class="govuk-date-input__item">
       |    <div class="govuk-form-group">
       |      <label class="govuk-label govuk-date-input__label" for="dateMonth">
       |        Month
       |      </label>
       |      <input class="govuk-input govuk-date-input__input govuk-input--width-2 govuk-input--error"
       |             id="dateMonth" name="dateMonth" type="text" pattern="[0-9]*" inputmode="numeric">
       |    </div>
       |  </div>
       |  <div class="govuk-date-input__item">
       |    <div class="govuk-form-group">
       |      <label class="govuk-label govuk-date-input__label" for="dateYear">
       |        Year
       |      </label>
       |      <input class="govuk-input govuk-date-input__input govuk-input--width-4"
       |             id="dateYear" name="dateYear" type="text" value="2020" pattern="[0-9]*" inputmode="numeric">
       |    </div>
       |  </div>
       |</div>
       """.stripMargin

  val inputMarkupOnlyYearError: String =
    s"""
       |<div class="govuk-date-input" id="$id">
       |  <div class="govuk-date-input__item">
       |    <div class="govuk-form-group">
       |      <label class="govuk-label govuk-date-input__label" for="dateDay">
       |        Day
       |      </label>
       |      <input class="govuk-input govuk-date-input__input govuk-input--width-2"
       |             id="dateDay" name="dateDay" type="text" value="4" pattern="[0-9]*" inputmode="numeric">
       |    </div>
       |  </div>
       |  <div class="govuk-date-input__item">
       |    <div class="govuk-form-group">
       |      <label class="govuk-label govuk-date-input__label" for="dateMonth">
       |        Month
       |      </label>
       |      <input class="govuk-input govuk-date-input__input govuk-input--width-2"
       |             id="dateMonth" name="dateMonth" type="text" value="5" pattern="[0-9]*" inputmode="numeric">
       |    </div>
       |  </div>
       |  <div class="govuk-date-input__item">
       |    <div class="govuk-form-group">
       |      <label class="govuk-label govuk-date-input__label" for="dateYear">
       |        Year
       |      </label>
       |      <input class="govuk-input govuk-date-input__input govuk-input--width-4 govuk-input--error"
       |             id="dateYear" name="dateYear" type="text" pattern="[0-9]*" inputmode="numeric">
       |    </div>
       |  </div>
       |</div>
       """.stripMargin


  "Generating a Date helper" when {

    "there are no errors" should {

      "render the appropriate date component" in {
        val expectedMarkup: Html = Html(
          s"""
             |<div class="govuk-form-group">
             |  <fieldset class="govuk-fieldset" role="group" aria-describedby="$id-hint">
             |    $legendMarkup
             |    $hintMarkup
             |    $inputMarkupNoErrors
             |  </fieldset>
             |</div>
             """.stripMargin
        )
        val markup = dateHelper(form, title, id, CeasedTradingDate, hintContent)
        formatHtml(markup) shouldBe formatHtml(expectedMarkup)
      }
    }

    "all three date fields error" should {

      "render the appropriate date component with an error message" in {
        val expectedMarkup: Html = Html(
          s"""
             |<div class="govuk-form-group govuk-form-group--error">
             |  <fieldset class="govuk-fieldset" role="group" aria-describedby="$id-hint $id-error">
             |    $legendMarkup
             |    $hintMarkup
             |    <span id="$id-error" class="govuk-error-message">
             |      <span class="govuk-visually-hidden">Error:</span>
             |      Enter the date the business stopped or will stop trading
             |    </span>
             |    $inputMarkupAllDateFieldsError
             |  </fieldset>
             |</div>
             """.stripMargin
        )
        val markup = dateHelper(
          form.bind(Map("dateDay" -> "", "dateMonth" -> "", "dateYear" -> "")),
          title,
          id,
          CeasedTradingDate,
          hintContent)
        formatHtml(markup) shouldBe formatHtml(expectedMarkup)
      }
    }

    "the day and month fields error" should {

      "render the appropriate date component with an error message" in {
        val expectedMarkup: Html = Html(
          s"""
             |<div class="govuk-form-group govuk-form-group--error">
             |  <fieldset class="govuk-fieldset" role="group" aria-describedby="$id-hint $id-error">
             |    $legendMarkup
             |    $hintMarkup
             |    <span id="$id-error" class="govuk-error-message">
             |      <span class="govuk-visually-hidden">Error:</span>
             |      Enter the date the business stopped or will stop trading
             |    </span>
             |    $inputMarkupDayAndMonthFieldsError
             |  </fieldset>
             |</div>
             """.stripMargin
        )
        val markup = dateHelper(
          form.bind(Map("dateDay" -> "", "dateMonth" -> "", "dateYear" -> "2020")),
          title,
          id,
          CeasedTradingDate,
          hintContent)
        formatHtml(markup) shouldBe formatHtml(expectedMarkup)
      }
    }

    "the day and year fields error" should {

      "render the appropriate date component with an error message" in {
        val expectedMarkup: Html = Html(
          s"""
             |<div class="govuk-form-group govuk-form-group--error">
             |  <fieldset class="govuk-fieldset" role="group" aria-describedby="$id-hint $id-error">
             |    $legendMarkup
             |    $hintMarkup
             |    <span id="$id-error" class="govuk-error-message">
             |      <span class="govuk-visually-hidden">Error:</span>
             |      Enter the date the business stopped or will stop trading
             |    </span>
             |    $inputMarkupDayAndYearFieldsError
             |  </fieldset>
             |</div>
             """.stripMargin
        )
        val markup = dateHelper(
          form.bind(Map("dateDay" -> "", "dateMonth" -> "5", "dateYear" -> "")),
          title,
          id,
          CeasedTradingDate,
          hintContent)
        formatHtml(markup) shouldBe formatHtml(expectedMarkup)
      }
    }

    "the month and year fields error" should {

      "render the appropriate date component with an error message" in {
        val expectedMarkup: Html = Html(
          s"""
             |<div class="govuk-form-group govuk-form-group--error">
             |  <fieldset class="govuk-fieldset" role="group" aria-describedby="$id-hint $id-error">
             |    $legendMarkup
             |    $hintMarkup
             |    <span id="$id-error" class="govuk-error-message">
             |      <span class="govuk-visually-hidden">Error:</span>
             |      Enter the date the business stopped or will stop trading
             |    </span>
             |    $inputMarkupMonthAndYearFieldsError
             |  </fieldset>
             |</div>
             """.stripMargin
        )
        val markup = dateHelper(
          form.bind(Map("dateDay" -> "4", "dateMonth" -> "", "dateYear" -> "")),
          title,
          id,
          CeasedTradingDate,
          hintContent)
        formatHtml(markup) shouldBe formatHtml(expectedMarkup)
      }
    }

    "only the day field errors" should {

      "render the appropriate date component with an error message" in {
        val expectedMarkup: Html = Html(
          s"""
             |<div class="govuk-form-group govuk-form-group--error">
             |  <fieldset class="govuk-fieldset" role="group" aria-describedby="$id-hint $id-error">
             |    $legendMarkup
             |    $hintMarkup
             |    <span id="$id-error" class="govuk-error-message">
             |      <span class="govuk-visually-hidden">Error:</span>
             |      Enter the day in the correct format
             |    </span>
             |    $inputMarkupOnlyDayError
             |  </fieldset>
             |</div>
             """.stripMargin
        )
        val markup = dateHelper(
          form.bind(Map("dateDay" -> "", "dateMonth" -> "5", "dateYear" -> "2020")),
          title,
          id,
          CeasedTradingDate,
          hintContent)
        formatHtml(markup) shouldBe formatHtml(expectedMarkup)
      }
    }

    "only the month field errors" should {

      "render the appropriate date component with an error message" in {
        val expectedMarkup: Html = Html(
          s"""
             |<div class="govuk-form-group govuk-form-group--error">
             |  <fieldset class="govuk-fieldset" role="group" aria-describedby="$id-hint $id-error">
             |    $legendMarkup
             |    $hintMarkup
             |    <span id="$id-error" class="govuk-error-message">
             |      <span class="govuk-visually-hidden">Error:</span>
             |      Enter the month in the correct format
             |    </span>
             |    $inputMarkupOnlyMonthError
             |  </fieldset>
             |</div>
             """.stripMargin
        )
        val markup = dateHelper(
          form.bind(Map("dateDay" -> "4", "dateMonth" -> "", "dateYear" -> "2020")),
          title,
          id,
          CeasedTradingDate,
          hintContent)
        formatHtml(markup) shouldBe formatHtml(expectedMarkup)
      }
    }

    "only the year field errors" should {

      "render the appropriate date component with an error message" in {
        val expectedMarkup: Html = Html(
          s"""
             |<div class="govuk-form-group govuk-form-group--error">
             |  <fieldset class="govuk-fieldset" role="group" aria-describedby="$id-hint $id-error">
             |    $legendMarkup
             |    $hintMarkup
             |    <span id="$id-error" class="govuk-error-message">
             |      <span class="govuk-visually-hidden">Error:</span>
             |      Enter the year in the correct format
             |    </span>
             |    $inputMarkupOnlyYearError
             |  </fieldset>
             |</div>
             """.stripMargin
        )
        val markup = dateHelper(
          form.bind(Map("dateDay" -> "4", "dateMonth" -> "5", "dateYear" -> "")),
          title,
          id,
          CeasedTradingDate,
          hintContent)
        formatHtml(markup) shouldBe formatHtml(expectedMarkup)
      }
    }
  }
}