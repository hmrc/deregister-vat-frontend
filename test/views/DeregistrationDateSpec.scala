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

class DeregistrationDateSpec extends ViewBaseSpec {

  object Selectors {
    val back = ".link-back"
    val h1 = "h1"
    val day = ".form-group-day > span"
    val month = ".form-group-month > span"
    val year = ".form-group-year > span"
    val button = ".button"
    val form = "form"
  }

  "Rendering DeregistrationDate view" when {

    "no data is previously entered" should {

      lazy val view = views.html.deregistrationDate(DeregistrationDateForm.form)
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

      "have correct date guidance" in {

      }

      "have hint text" in {

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

      }

      "form is invalid" should {

      }
    }
  }
}
