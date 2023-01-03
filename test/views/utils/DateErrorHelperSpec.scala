/*
 * Copyright 2023 HM Revenue & Customs
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

package views.utils

import forms.DateForm
import models.{CeasedTradingDate, DateModel, DeregistrationDate}
import org.scalatest.OptionValues
import  org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import play.api.data.Form

class DateErrorHelperSpec extends AnyWordSpecLike with Matchers with OptionValues {

  val form: Form[DateModel] = DateForm.dateForm

  "The .errorContent function" when {

    "there is more than 1 error and the form variant is CeasedTradingDate" should {

      "return the generic ceased trading error message" in {
        val errorForm = form.bind(Map("dateDay" -> "", "dateMonth" -> "", "dateYear" -> ""))
        DateErrorHelper.errorContent(errorForm, CeasedTradingDate) shouldBe "ceasedTrading.error.date.noEntry"
      }
    }

    "there is more than 1 error and the form variant is DeregistrationDate" should {

      "return the generic deregistration date error message" in {
        val errorForm = form.bind(Map("dateDay" -> "", "dateMonth" -> "", "dateYear" -> ""))
        DateErrorHelper.errorContent(errorForm, DeregistrationDate) shouldBe "deregistrationDate.error.date.noEntry"
      }
    }

    "there is only 1 error" should {

      "return the associated error message" in {
        val errorForm = form.bind(Map("dateDay" -> "1", "dateMonth" -> "1", "dateYear" -> ""))
        DateErrorHelper.errorContent(errorForm, CeasedTradingDate) shouldBe "error.date.year"
      }
    }
  }

  "The .dateFieldErrorId function" when {

    "there is more than 1 form error, including the 'day' field" should {

      "link to the 'day' input" in {
        val errorForm = form.bind(Map("dateDay" -> "f", "dateMonth" -> "x", "dateYear" -> "z"))
        DateErrorHelper.dateFieldErrorId(errorForm) shouldBe "#dateDay"
      }
    }

    "there is more than 1 form error, not including the 'day' field" should {

      "link to the 'month' input" in {
        val errorForm = form.bind(Map("dateDay" -> "1", "dateMonth" -> "x", "dateYear" -> "z"))
        DateErrorHelper.dateFieldErrorId(errorForm) shouldBe "#dateMonth"
      }
    }

    "there is only 1 form error, and this is in the 'day' field" should {

      "link to the 'day' input" in {
        val errorForm = form.bind(Map("dateDay" -> "f", "dateMonth" -> "1", "dateYear" -> "2000"))
        DateErrorHelper.dateFieldErrorId(errorForm) shouldBe "#dateDay"
      }
    }

    "there is only 1 form error, and this is in the 'month' field" should {

      "link to the 'month' input" in {
        val errorForm = form.bind(Map("dateDay" -> "1", "dateMonth" -> "x", "dateYear" -> "2000"))
        DateErrorHelper.dateFieldErrorId(errorForm) shouldBe "#dateMonth"
      }
    }

    "there is only 1 form error, and this is in the 'year' field" should {

      "link to the 'year' input" in {
        val errorForm = form.bind(Map("dateDay" -> "1", "dateMonth" -> "1", "dateYear" -> "z"))
        DateErrorHelper.dateFieldErrorId(errorForm) shouldBe "#dateYear"
      }
    }
  }

  "The .applyInputItemClass function" when {

    "the form has more than one error and the given formType has an error" should {

      "return an error class" in {

        val errorForm = form.bind(Map("dateDay" -> "", "dateMonth" -> "", "dateYear" -> "z"))
        DateErrorHelper.applyInputItemClass(errorForm, "dateDay", "exampleClass") shouldBe "exampleClass govuk-input--error"
      }
    }

    "the form has more than one error and the given formType does not have an error" should {

      "return an error class" in {

        val errorForm = form.bind(Map("dateDay" -> "1", "dateMonth" -> "", "dateYear" -> "z"))
        DateErrorHelper.applyInputItemClass(errorForm, "dateDay", "exampleClass") shouldBe "exampleClass govuk-input--error"
      }
    }

    "the form has one error and the given formType does have an error" should {

      "return an error class" in {

        val errorForm = form.bind(Map("dateDay" -> "", "dateMonth" -> "4", "dateYear" -> "2020"))
        DateErrorHelper.applyInputItemClass(errorForm, "dateDay", "exampleClass") shouldBe "exampleClass govuk-input--error"
      }
    }

    "the form has one error and the given formType does not have an error" should {

      "return an error class" in {

        val errorForm = form.bind(Map("dateDay" -> "1", "dateMonth" -> "4", "dateYear" -> "z"))
        DateErrorHelper.applyInputItemClass(errorForm, "dateDay", "exampleClass") shouldBe "exampleClass"
      }
    }

    "the form has no errors" should {

      "return an error class" in {

        val errorForm = form.bind(Map("dateDay" -> "1", "dateMonth" -> "4", "dateYear" -> "2020"))
        DateErrorHelper.applyInputItemClass(errorForm, "dateDay", "exampleClass") shouldBe "exampleClass"
      }
    }
  }
}
