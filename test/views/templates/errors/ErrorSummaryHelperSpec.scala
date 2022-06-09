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

package views.templates.errors

import play.api.data.Form
import play.api.data.Forms.nonEmptyText
import play.twirl.api.Html
import views.html.templates.errors.ErrorSummaryHelper
import views.templates.TemplateBaseSpec

class ErrorSummaryHelperSpec extends TemplateBaseSpec {

  lazy val errorSummaryHelper: ErrorSummaryHelper = injector.instanceOf[ErrorSummaryHelper]
  val exampleForm: Form[String] = Form("value" -> nonEmptyText)

  "Generating an ErrorSummary helper" when {

    "a href ID is not provided" should {

      "render the appropriate error summary component, with the error link set to the default value of the field name" in {
        val expectedMarkup: Html = Html(
          s"""
             |<div class="govuk-error-summary" aria-labelledby="error-summary-title" role="alert"
             |     data-module="govuk-error-summary">
             |  <h2 class="govuk-error-summary__title" id="error-summary-title"> There is a problem </h2>
             |  <div class="govuk-error-summary__body">
             |    <ul class="govuk-list govuk-error-summary__list">
             |      <li>
             |        <a href="#value">This field is required</a>
             |      </li>
             |    </ul>
             |  </div>
             |</div>
           """.stripMargin
        )
        val markup = errorSummaryHelper(exampleForm.bind(Map("value" -> "")))
        formatHtml(markup) shouldBe formatHtml(expectedMarkup)
      }
    }

    "a href ID is provided" should {

      "render the appropriate error summary component, with the error link going to the href specified" in {
        val expectedMarkup: Html = Html(
          s"""
             |<div class="govuk-error-summary" aria-labelledby="error-summary-title" role="alert"
             |     data-module="govuk-error-summary">
             |  <h2 class="govuk-error-summary__title" id="error-summary-title"> There is a problem </h2>
             |  <div class="govuk-error-summary__body">
             |    <ul class="govuk-list govuk-error-summary__list">
             |      <li>
             |        <a href="#customInput">This field is required</a>
             |      </li>
             |    </ul>
             |  </div>
             |</div>
           """.stripMargin
        )
        val markup = errorSummaryHelper(exampleForm.bind(Map("value" -> "")), hrefId = Some("#customInput"))
        formatHtml(markup) shouldBe formatHtml(expectedMarkup)
      }
    }
  }
}