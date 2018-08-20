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

package forms.utils

import play.api.data.validation.{Constraint, Invalid, Valid}

import scala.util.{Success, Try}

trait FormValidation {

  def isNumeric(errMsg: String): Constraint[String] = Constraint("isNumeric"){
    amt => Try(BigDecimal(amt)) match {
      case Success(_) => Valid
      case _ => Invalid(errMsg)
    }
  }

  def hasMaxTwoDecimals(errMsg: String): Constraint[String] = Constraint("isNumeric") {
    amt => if(amt.lastIndexOf(".") >= 0  && (amt.length - amt.lastIndexOf(".") - 1) > 2) Invalid(errMsg) else Valid
  }

  def isPositive(errMsg: String): Constraint[BigDecimal] = Constraint[BigDecimal]("isPositive") {
    amt => if(amt >= 0) Valid else Invalid(errMsg)
  }

  def doesNotExceed(max: BigDecimal, errMsg: String): Constraint[BigDecimal] = Constraint[BigDecimal]("isLessThanMax") {
    amt => if(amt <= max) Valid else Invalid(errMsg, max)
  }

}
