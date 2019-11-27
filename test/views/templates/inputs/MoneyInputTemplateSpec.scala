/*
 * Copyright 2019 HM Revenue & Customs
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

import play.api.data.Forms._
import play.api.data.{Field, Form, FormError}
import play.twirl.api.Html
import views.html.templates.inputs.moneyInput
import views.templates.TemplateBaseSpec

class MoneyInputTemplateSpec extends TemplateBaseSpec {

  val fieldName = "fieldName"
  val labelText = "labelText"
  val hintText = "hintText"
  val valueText = "100"
  val errorMessage = "error message"

  case class TestModel(amt: BigDecimal)
  val testForm: Form[TestModel] = Form(mapping("amt" -> bigDecimal)(TestModel.apply)(TestModel.unapply))

  "Calling the money input with no value, no hint, visible label and decimals" should {

    "render an empty money input field with label no hint" in {

      val field: Field = Field(testForm, fieldName, Seq(), None, Seq(), None)

      val expectedMarkup = Html(
        s"""
           |  <div class="form-group ">
           |    <label for=${field.id} class="form-label ">
           |      $labelText
           |    </label>
           |
           |    <span class="input-currency"></span>
           |
           |    <input type="number"
           |           class="form-control input--no-spinner money-input-padding "
           |           name="${field.name}"
           |           id="${field.id}"
           |           value=""
           |           step="0.01"/>
           |  </div>
        """.stripMargin
      )

      val markup = moneyInput(field, labelText, labelHidden = false, decimalPlace = true, None)

      formatHtml(markup) shouldBe formatHtml(expectedMarkup)
    }
  }


  "Calling the money input with value, with a hint, hidden label and non-decimals" should {

    "render a money input field with hidden label, hint and amount" in {

      val field: Field = Field(testForm, fieldName, Seq(), None, Seq(), Some(valueText))

      val expectedMarkup = Html(
        s"""
           |  <div class="form-group ">
           |    <label for=${field.id} class="form-label visuallyhidden">
           |      $labelText
           |    </label>
           |
           |    <span class="form-hint">
           |      $hintText
           |    </span>
           |
           |    <span class="input-currency"></span>
           |
           |    <input type="number"
           |           class="form-control input--no-spinner money-input-padding "
           |           name="${field.name}"
           |           id="${field.id}"
           |           value="$valueText"
           |           step="1"/>
           |  </div>
        """.stripMargin
      )

      val markup = moneyInput(field, labelText, labelHidden = true, decimalPlace = false, Some(hintText))

      formatHtml(markup) shouldBe formatHtml(expectedMarkup)
    }
  }


  "Calling the money input with errors" should {

    "render a money input field with hidden label, hint and amount" in {

      val field: Field = Field(testForm, fieldName, Seq(), None, Seq(FormError("err", errorMessage)), Some(valueText))

      val expectedMarkup = Html(
        s"""
           |  <div class="form-group form-field--error">
           |    <label for=${field.id} class="form-label visuallyhidden">
           |      $labelText
           |    </label>
           |
           |    <span class="form-hint">
           |      $hintText
           |    </span>
           |
           |    <span class="error-message" role="tooltip">
           |      $errorMessage
           |    </span>
           |
           |    <span class="input-currency"></span>
           |
           |    <input type="number"
           |           class="form-control input--no-spinner money-input-padding error-field"
           |           name="${field.name}"
           |           id="${field.id}"
           |           value="$valueText"
           |           step="1"/>
           |  </div>
        """.stripMargin
      )

      val markup = moneyInput(field, labelText, labelHidden = true, decimalPlace = false, Some(hintText))

      formatHtml(markup) shouldBe formatHtml(expectedMarkup)
    }
  }
}
