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

package views.utils

import models.{CeasedTradingDate, DateFormVariant, DateModel, DeregistrationDate}
import play.api.data.Form

object DateErrorHelper {

  def errorContent(form: Form[DateModel], formVariant: DateFormVariant): String = {
    (form.errors.length > 1, formVariant) match {
      case (true, CeasedTradingDate) => "ceasedTrading.error.date.noEntry"
      case (true, DeregistrationDate) => "deregistrationDate.error.date.noEntry"
      case _ => form.errors.headOption.fold("")(_.message)
    }
  }

  def dateFieldErrorId(form: Form[DateModel]): String = {
    (form.errors.length > 1, form("dateDay").hasErrors, form("dateMonth").hasErrors, form("dateYear").hasErrors) match {
      case (true, false, _, _) => "#dateMonth"
      case (false, true, false, false) => "#dateDay"
      case (false, false, true, false) => "#dateMonth"
      case (false, false, false, true) => "#dateYear"
      case _ => "#dateDay"
    }
  }

  def applyInputItemClass(form: Form[DateModel], formType: String, inputItemClass: String): String = {
    (form.errors.length > 1, form(formType).hasErrors) match {
      case (true, _) => s"$inputItemClass govuk-input--error"
      case (false, true) => s"$inputItemClass govuk-input--error"
      case _ => inputItemClass
    }
  }
}
