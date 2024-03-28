/*
 * Copyright 2024 HM Revenue & Customs
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

package assets.messages

import java.text.NumberFormat

object CommonMessages {

  val formatter: NumberFormat = java.text.NumberFormat.getIntegerInstance

  val continue = "Continue"
  val back = "Back"
  val signOut = "Sign out"
  val yes = "Yes"
  val no = "No"
  val finish = "Finish"
  val errorPrefix = "Error:"
  val agentServiceName = "Your client’s VAT details"
  val clientServiceName = "Manage your VAT account"
  val otherServiceName = "VAT"
  val day = "Day"
  val month = "Month"
  val year = "Year"
  val errorHeading = "There is a problem"
  val errorMandatoryAmountInput = "Real number value expected"
  val numericValueError = "Numeric value expected"
  val enterAmount = "No amount entered"
  val moreInfo = "More information"

  val errorMandatoryAmount = "Enter the amount using numbers 0 to 9"
  val errorTooManyDecimals = "Enter a maximum of 2 decimal places for pence"
  val errorTooManyNumbersBeforeDecimal = "Enter a maximum of 13 decimal places for pounds"
  val errorNegative = "Enter a positive amount"
  val errorMaximum = "You have entered too many numbers"

  val errorDateDay = "Enter the day in the correct format"
  val errorDateMonth = "Enter the month in the correct format"
  val errorDateYear = "Enter the year in the correct format"
}
