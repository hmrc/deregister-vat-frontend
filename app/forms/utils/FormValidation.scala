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

package forms.utils

import java.time.LocalDate

import models.DateModel
import play.api.data.validation.{Constraint, Invalid, Valid}

import scala.util.{Failure, Success, Try}

trait FormValidation {

  val isNumeric: String => Boolean = amt => Try(BigDecimal(amt)) match {
    case Success(_) => true
    case _ => false
  }

  val isInteger: String => Boolean = amt => Try(amt.toInt) match {
    case Success(_) => true
    case _ => false
  }

  def isInt(errMsg: String): Constraint[String] = Constraint("isInt") {
    amt => if(isInteger(amt)) Valid else Invalid(errMsg)
  }

  def characters(characterLength: Int, tooLow: String, tooHigh: String): Constraint[String] = Constraint("characterLimit") {
    string => if(string.length > characterLength) Invalid(tooHigh) else if(string.length < characterLength) Invalid(tooLow) else Valid
  }

  val hasMoreThanTwoDecimals: String => Boolean = amtAsString =>
    amtAsString.lastIndexOf(".") >= 0 && (amtAsString.length - amtAsString.lastIndexOf(".") - 1) > 2

  def isNumericConstraint(errMsg: String): Constraint[String] = Constraint("isNumeric"){
    amt => if (isNumeric(amt)) Valid else Invalid(errMsg)
  }

  def hasMaxTwoDecimalsConstraint(errMsg: String): Constraint[String] = Constraint("tooManyDecimals") {
    amt => if(hasMoreThanTwoDecimals(amt)) Invalid(errMsg) else Valid
  }

  def isPositive(errMsg: String): Constraint[BigDecimal] = Constraint[BigDecimal]("isPositive") {
    amt => if(amt >= 0) Valid else Invalid(errMsg)
  }

  def doesNotExceed(max: BigDecimal, errMsg: String): Constraint[BigDecimal] = Constraint[BigDecimal]("isLessThanMax") {
    amt => if(amt < max) Valid else Invalid(errMsg, max)
  }

  def isValidDateConstraint(errMsg: String): Constraint[DateModel] = Constraint[DateModel]("isValidDate") {
    date => if (isValidDate(date)) Valid else Invalid(errMsg)
  }

  def isValidDate(date: DateModel): Boolean = {
    Try(LocalDate.of(date.dateYear, date.dateMonth, date.dateDay)) match {
      case Success(_) => true
      case Failure(_) => false
    }
  }
}
