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

package views.templates.inputs

import forms.WhyTurnoverBelowForm
import play.api.data.{Form, FormError}
import play.api.data.Forms._
import play.twirl.api.Html
import views.html.templates.inputs.checkboxHelper
import views.templates.TemplateBaseSpec

class CheckboxHelperSpec extends TemplateBaseSpec {

  val fieldName = "fieldName"
  val question = "question"
  val hintText = "hintText"
  val errorMessage = "error message"
  val additionalContent = Html("<h1>foo bar</h1>")

  val choices: Seq[(String, String)] = Seq(
    "value1" -> "display1",
    "value2" -> "display2",
    "value3" -> "display3",
    "value4" -> "display4",
    "value5" -> "display5"
  )

  case class TestModel(value1: Boolean,
                       value2: Boolean,
                       value3: Boolean,
                       value4: Boolean,
                       value5: Boolean)

  val testForm: Form[TestModel] = Form(mapping(
    "value1" -> boolean,
    "value2" -> boolean,
    "value3" -> boolean,
    "value4" -> boolean,
    "value5" -> boolean
  )(TestModel.apply)(TestModel.unapply))

  def generateExpectedCheckboxMarkup(name: String, display: String, checked: Boolean = false): String =
    s"""
       |  <div class="multiple-choice">
       |    <input type="checkbox" id="$name" name="$name" value="true"${if (checked) " checked" else ""}>
       |    <label for="$name">$display</label>
       |  </div>
      """.stripMargin

  "Calling the checkbox helper with no choice pre-selected, no hint and no additional content" should {

    "render the choices as radio buttons" in {

      val expectedMarkup = Html(
        s"""
           |  <div>
           |    <fieldset>
           |
           |      ${generateExpectedLegendMarkup(question)}
           |
           |      <div>
           |        ${generateExpectedCheckboxMarkup("value1", "display1")}
           |        ${generateExpectedCheckboxMarkup("value2", "display2")}
           |        ${generateExpectedCheckboxMarkup("value3", "display3")}
           |        ${generateExpectedCheckboxMarkup("value4", "display4")}
           |        ${generateExpectedCheckboxMarkup("value5", "display5")}
           |      </div>
           |
           |   </fieldset>
           |  </div>
        """.stripMargin
      )

      val markup = checkboxHelper(WhyTurnoverBelowForm.whyTurnoverBelowForm, choices, question, None)

      formatHtml(markup) shouldBe formatHtml(expectedMarkup)
    }
  }

  "Calling the radio group helper with a choice pre-selected, hint and additional content" should {

    "render a list of radio options with one pre-checked" in {
      val expectedMarkup = Html(
        s"""
           |  <div>
           |     <fieldset>
           |
           |      ${generateExpectedLegendMarkup(question)}
           |
           |      $additionalContent
           |
           |      <span class="form-hint">
           |         $hintText
           |      </span>
           |
           |      <div>
           |        ${generateExpectedCheckboxMarkup("value1", "display1", checked = true)}
           |        ${generateExpectedCheckboxMarkup("value2", "display2", checked = true)}
           |        ${generateExpectedCheckboxMarkup("value3", "display3")}
           |        ${generateExpectedCheckboxMarkup("value4", "display4")}
           |        ${generateExpectedCheckboxMarkup("value5", "display5", checked = true)}
           |       </div>
           |    </fieldset>
           |  </div>
        """.stripMargin
      )

      val data = Map(
        "value1" -> "true",
        "value2" -> "true",
        "value3" -> "false",
        "value4" -> "false",
        "value5" -> "true"
      )
      val form = testForm.bind(data)

      val markup = checkboxHelper(form, choices, question, Some(hintText), Some(additionalContent))
      formatHtml(markup) shouldBe formatHtml(expectedMarkup)
    }
  }

  "Calling the radio group helper with an error" should {

    "render an error" in {
      val data = Map("foo" -> "bar")
      val form = testForm.bind(data).withError(FormError("err", errorMessage))
      val expectedMarkup = Html(
        s"""
           |  <div class="form-field--error">
           |    <fieldset>
           |      ${generateExpectedLegendMarkup(question)}
           |
           |      <span class="error-message">$errorMessage</span>
           |      <div>
           |        ${generateExpectedCheckboxMarkup("value1", "display1")}
           |        ${generateExpectedCheckboxMarkup("value2", "display2")}
           |        ${generateExpectedCheckboxMarkup("value3", "display3")}
           |        ${generateExpectedCheckboxMarkup("value4", "display4")}
           |        ${generateExpectedCheckboxMarkup("value5", "display5")}
           |      </div>
           |    </fieldset>
           |  </div>
        """.stripMargin
      )

      val markup = checkboxHelper(form, choices, question, None)
      formatHtml(markup) shouldBe formatHtml(expectedMarkup)
    }
  }

  "Calling the radio group helper with legendAsHeader false" should {

    "render the legend without a header" in {
      val data = Map("foo" -> "bar")
      val form = testForm.bind(data).withError(FormError("err", errorMessage))
      val expectedMarkup = Html(
        s"""
           |  <div class="form-field--error">
           |    <fieldset>
           |      ${generateExpectedLegendMarkup(question, asHeader = false)}
           |
           |      <span class="error-message">$errorMessage</span>
           |      <div>
           |        ${generateExpectedCheckboxMarkup("value1", "display1")}
           |        ${generateExpectedCheckboxMarkup("value2", "display2")}
           |        ${generateExpectedCheckboxMarkup("value3", "display3")}
           |        ${generateExpectedCheckboxMarkup("value4", "display4")}
           |        ${generateExpectedCheckboxMarkup("value5", "display5")}
           |      </div>
           |    </fieldset>
           |  </div>
        """.stripMargin
      )

      val markup = checkboxHelper(form, choices, question, None, legendAsHeader = false)
      formatHtml(markup) shouldBe formatHtml(expectedMarkup)
    }
  }
}
