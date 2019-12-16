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

import forms.utils.FormValidation
import models.NumberInputModel
import play.api.data.Form
import play.api.data.Forms._
import common.Constants._

object NextTaxableTurnoverForm extends FormValidation {

  val taxableTurnoverForm: Form[NumberInputModel] = Form(
    mapping(
      "value" -> optional(text)
        .verifying("taxableTurnover.error.mandatory", _.isDefined)
        .transform[String](x => x.get, x => Some(x))
        .verifying(isNumericConstraint("taxableTurnover.error.nonNumeric"), hasMaxTwoDecimalsConstraint("common.error.tooManyDecimals"))
        .transform[BigDecimal](x => BigDecimal(x), x => x.toString)
        .verifying(isPositive("common.error.negative"), doesNotExceed(maxAmount, "common.error.tooManyDigitsBeforeDecimal"))
    )(NumberInputModel.apply)(NumberInputModel.unapply)
  )

}
