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

package forms

import forms.DateForm.{day, formatter, month, year}
import models.DateModel
import play.api.data.Form
import play.api.data.Forms.{mapping, of}
import play.api.data.validation.{Constraint, Invalid, Valid, ValidationResult}

import java.time.LocalDate

object DeregistrationDateForm {

  val monthsAllowed = 3
  val checkValidDate: Constraint[DateModel] = Constraint[DateModel]("checkValidDate") {
    date =>
      date.date.fold[ValidationResult](Invalid("deregistrationDate.error.date.noEntry")) {
        validDate => isDateRangeValid(validDate)
      }
  }

  private def isDateRangeValid(date: LocalDate) = {
    if (date.isAfter(LocalDate.now.plusMonths(monthsAllowed))) {
      Invalid("deregistrationDate.error.date.future")
    } else if (date.isBefore(LocalDate.now)) {
      Invalid("deregistrationDate.error.date.past")
    } else {
      Valid
    }
  }

  val form: Form[DateModel] = Form(
    mapping(
      day -> of(formatter),
      month -> of(formatter),
      year -> of(formatter)
    )(DateModel.apply)(DateModel.unapply)
      .verifying(checkValidDate)
  )
}
