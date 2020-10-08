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

package views

import assets.messages.{CommonMessages, DeregistrationDateMessages}
import forms.DeregistrationDateForm
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import views.html.DeregistrationDate

class DeregistrationDateSpec extends ViewBaseSpec {

  lazy val deregistrationDate: DeregistrationDate = injector.instanceOf[DeregistrationDate]

  object Selectors {
    val back = ".link-back"
    val h1 = "h1"
    val day = ".form-group-day > span"
    val dayValue = ".form-group-day input"
    val month = ".form-group-month > span"
    val monthValue = ".form-group-month input"
    val year = ".form-group-year > span"
    val yearValue = ".form-group-year input"
    val button = ".button"
    val form = "form"
    val p1 = ".form-group p:nth-of-type(1)"
    val p2 = ".form-group p:nth-of-type(2)"
    val hint = ".form-hint"
    val errorSummaryHeading = "#error-summary-display h2"
    val fieldError = ".error-message"
    def errorSummaryText(row: Int): String = s"#error-summary-display li:nth-of-type($row)"
    def errorSummaryLink(row: Int): String = s"#error-summary-display li:nth-of-type($row) a"
  }

  "Rendering DeregistrationDate view" when {

    "no data is previously entered" should {

      lazy val view = deregistrationDate(DeregistrationDateForm.form)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have the correct title" in {
        document.title() shouldBe DeregistrationDateMessages.title
      }

      "have a back link" which {

        "has text 'Back'" in {
          elementText(Selectors.back) shouldBe CommonMessages.back
        }

        s"has a link to ${controllers.routes.ChooseDeregistrationDateController.show().url}" in {
          element(Selectors.back).attr("href") shouldBe controllers.routes.ChooseDeregistrationDateController.show().url
        }
      }

      "have the correct heading" in {
        elementText(Selectors.h1) shouldBe DeregistrationDateMessages.heading
      }

      "have correct guidance" in {
        elementText(Selectors.p1) shouldBe DeregistrationDateMessages.p1
        elementText(Selectors.p2) shouldBe DeregistrationDateMessages.p2
      }

      "have hint text" in {
        elementText(Selectors.hint) shouldBe DeregistrationDateMessages.hintText
      }

      "have a date form" which {

        s"POSTs to ${controllers.routes.DeregistrationDateController.submit().url}" in {
          element(Selectors.form).attr("action") shouldBe controllers.routes.DeregistrationDateController.submit().url
        }

        "contains the correct inputs" in {
          elementText(Selectors.day) shouldBe CommonMessages.day
          elementText(Selectors.month) shouldBe CommonMessages.month
          elementText(Selectors.year) shouldBe CommonMessages.year
        }

        "contains a submit button" which {

          "has the text 'Continue'" in {
            elementText(Selectors.button) shouldBe CommonMessages.continue
          }
        }
      }
    }

    "data is previously entered" when {

      "form is valid" should {

        lazy val view = deregistrationDate(DeregistrationDateForm.form.bind(
          Map("dateDay" -> "1", "dateMonth" -> "2", "dateYear" -> "1999")
        ))
        lazy implicit val document: Document = Jsoup.parse(view.body)

        "contain previously filled data" in {
          element(Selectors.dayValue).attr("value") shouldBe "1"
          element(Selectors.monthValue).attr("value") shouldBe "2"
          element(Selectors.yearValue).attr("value") shouldBe "1999"
        }
      }

      "form is invalid in two fields" should {

        lazy val view = deregistrationDate(DeregistrationDateForm.form.bind(
          Map("dateDay" -> "01", "dateMonth" -> "", "dateYear" -> "")
        ))
        lazy implicit val document: Document = Jsoup.parse(view.body)

        "have the correct error heading" in {
          document.title() shouldBe DeregistrationDateMessages.errorTitle
        }

        "display an error summary" which {

          "has the correct header" in {
            elementText(Selectors.errorSummaryHeading) shouldBe DeregistrationDateMessages.errorSummaryTitle
          }

          "contains an error" in {
            elementText(Selectors.errorSummaryText(1)) shouldBe "Enter a valid cancellation date"
          }

          "contains a link to the correct field" in {
            element(Selectors.errorSummaryLink(1)).attr("href") shouldBe "#dateMonth"
          }
        }

        "display a field error" in {
          elementText(Selectors.fieldError) shouldBe "Error: Enter a valid cancellation date"
        }
      }

      "form is invalid in one field" should {

        lazy val view = deregistrationDate(DeregistrationDateForm.form.bind(
          Map("dateDay" -> "01", "dateMonth" -> "01", "dateYear" -> "")
        ))
        lazy implicit val document: Document = Jsoup.parse(view.body)

        "have the correct error heading" in {
          document.title() shouldBe DeregistrationDateMessages.errorTitle
        }

        "display an error summary" which {

          "has the correct header" in {
            elementText(Selectors.errorSummaryHeading) shouldBe DeregistrationDateMessages.errorSummaryTitle
          }

          "contains an error" in {
            elementText(Selectors.errorSummaryText(1)) shouldBe "Enter 4 numbers"
          }

          "contains a link to the correct field" in {
            element(Selectors.errorSummaryLink(1)).attr("href") shouldBe "#dateYear"
          }
        }

        "display a field error" in {
          elementText(Selectors.fieldError) shouldBe "Error: Enter 4 numbers"
        }
      }
    }
  }
}
