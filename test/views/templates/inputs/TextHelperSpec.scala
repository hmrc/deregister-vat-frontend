/*
 * Copyright 2021 HM Revenue & Customs
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

import play.api.data.{Field, FormError}
import play.twirl.api.Html
import testOnly.forms.FeatureSwitchForm
import views.html.templates.inputs.Text
import views.templates.TemplateBaseSpec

class TextHelperSpec extends TemplateBaseSpec {

  lazy val text: Text = injector.instanceOf[Text]

  val fieldName = "fieldName"
  val label = "myLabel"
  val field = Field(FeatureSwitchForm.form, fieldName, Seq(), None, Seq(), Some("text"))

  "The text input form helper" when {

    "all parameters are default and there are no errors" should {

      "render the correct HTML" in {

        val expectedMarkup = Html(
          s"""
             |<fieldset>
             |  <div class="form-field">
             |    <label for="$fieldName" class="form-label visuallyhidden">$label</label>
             |    <input class="form-control input--no-spinner" name="$fieldName" id="$fieldName" value="text">
             |  </div>
             |</fieldset>
             |""".stripMargin
        )

        val result = text(field, label)

        formatHtml(result) shouldBe formatHtml(expectedMarkup)
      }
    }

    "there is an error" should {

      "render the error span and apply the form-field--error class to the field and the aria-describedby to the fieldset" in {

        val errorField = Field(FeatureSwitchForm.form, fieldName, Seq(), None, Seq(FormError("error", "ERROR")), Some("text"))

        val expectedMarkup = Html(
          s"""
             |<fieldset aria-describedby="error-message">
             |  <div class="form-field--error panel-border-narrow">
             |    <label for="$fieldName" class="form-label visuallyhidden">$label</label>
             |    <span id="error-message" class="error-message" role="tooltip">
             |      <span class="visuallyhidden">Error:</span>
             |      ERROR
             |    </span>
             |    <input class="form-control input--no-spinner" name="$fieldName" id="$fieldName" value="text">
             |  </div>
             |</fieldset>
             |""".stripMargin
        )

        val result = text(errorField, label)

        formatHtml(result) shouldBe formatHtml(expectedMarkup)
      }
    }

    "labelHidden is set to false" should {

      "not apply the visuallyhidden class to the label" in {

        val expectedMarkup = Html(
          s"""
             |<fieldset>
             |  <div class="form-field">
             |    <label for="$fieldName" class="form-label ">$label</label>
             |    <input class="form-control input--no-spinner" name="$fieldName" id="$fieldName" value="text">
             |  </div>
             |</fieldset>
             |""".stripMargin
        )

        val result = text(field, label, labelHidden = false)

        formatHtml(result) shouldBe formatHtml(expectedMarkup)
      }
    }

    "there is some additional content" should {

      "render the provided content inside of the form" in {

        val expectedMarkup = Html(
          s"""
             |<fieldset>
             |  <div class="form-field">
             |    <p>Additional HTML</p>
             |    <label for="$fieldName" class="form-label visuallyhidden">$label</label>
             |    <input class="form-control input--no-spinner" name="$fieldName" id="$fieldName" value="text">
             |  </div>
             |</fieldset>
             |""".stripMargin
        )

        val result =
          text(field, label, additionalContent = Some(Html("<p>Additional HTML</p>")))

        formatHtml(result) shouldBe formatHtml(expectedMarkup)
      }
    }

    "isMonetaryField is set to true" should {

      "be correctly marked up as a monetary field" in {

        val expectedMarkup = Html(
          s"""
             |<fieldset>
             |  <div class="form-field"> <label for="amount" class="form-label visuallyhidden">$label</label>
             |    <div class="hmrc-currency-input__wrapper"> <span class="hmrc-currency-input__unit" aria-hidden="true">£</span>
             |      <input class="govuk-input govuk-input--width-10" id="amount" name="fieldName" type="text" aria-describedby="amount" autocomplete="off" inputmode="decimal" value="text">
             |    </div>
             |  </div>
             |</fieldset>
             |""".stripMargin
        )

        val result = text(field, label, isMonetaryField = true)

        formatHtml(result) shouldBe formatHtml(expectedMarkup)
      }
    }
  }
}
