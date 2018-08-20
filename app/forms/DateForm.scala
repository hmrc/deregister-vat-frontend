/*
 * Copyright 2018 HM Revenue & Customs
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

import forms.utils.FormValidation
import models.DateModel
import play.api.data.Form
import play.api.data.Forms._

object DateForm extends FormValidation{

  val dateForm: Form[DateModel] = Form(
    mapping(
      "dateDay" -> optional(text).verifying("error.date.day", _.isDefined)
        .transform[String](_.get, Some(_)).verifying(isNumeric("error.date.invalidCharacters"))
        .transform[Int](_.toInt, _.toString).verifying(isValidDay("error.date.day")),
      "dateMonth" -> optional(text).verifying("error.date.month", _.isDefined)
        .transform[String](_.get, Some(_)).verifying(isNumeric("error.date.invalidCharacters"))
        .transform[Int](_.toInt, _.toString).verifying(isValidMonth("error.date.month")),
      "dateYear" -> optional(text).verifying("error.date.year", _.isDefined)
        .transform[String](_.get, Some(_)).verifying(isNumeric("error.date.invalidCharacters"))
        .transform[Int](_.toInt, _.toString).verifying(isValidYear("error.date.year"))
    )(DateModel.apply)(DateModel.unapply)
      .verifying(isValidDate("ceasedTrading.error.date.invalidDate"))
  )
}
