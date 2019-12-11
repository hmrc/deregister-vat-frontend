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

package forms

import common.Constants._
import forms.utils.FormValidation
import models.SicCodeModel
import play.api.data.Form
import play.api.data.Forms._

object SicCodeForm extends FormValidation {

  val sicCodeLength = 5

  val sicCodeForm: Form[SicCodeModel] = Form(
    mapping(
      "sicCode" -> optional(text)
        .verifying("sicCode.error.mandatory", _.isDefined)
        .transform[String](x => x.get, x => Some(x))
        .verifying(isInt("taxableTurnover.error.nonNumeric"),
          characters(sicCodeLength, "sicCode.error.tooFew", "sicCode.error.tooMany"))
        .transform[BigDecimal](x => BigDecimal(x), x => x.toString)
        .verifying(isPositive("common.error.negative"), doesNotExceed(maxAmount, "common.error.greaterThanMax"))
    )(SicCodeModel.apply)(SicCodeModel.unapply)
  )

}
