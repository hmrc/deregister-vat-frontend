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

package assets.messages

object CommonMessages {

  val formatter = java.text.NumberFormat.getIntegerInstance

  val continue = "Continue"
  val back = "Back"
  val signOut = "Sign out"
  val yes = "Yes"
  val no = "No"
  val agentServiceName = "Update your client’s VAT details"
  val clientServiceName = "Business tax account"
  val day = "Day"
  val month = "Month"
  val year = "Year"
  val errorHeading = "There is a problem"
  val errorMandatoryRadioOption = "Select an option"
  val errorMandatoryAmountInput = "Real number value expected"
  val numericValueError = "Numeric value expected"
  val invalidDate = "Invalid date"
  val enterAmount = "No amount entered"
  val moreInfo = "More information"
  val mandatoryAmount = "Enter an amount using numbers 0 to 9"
  val decimals = "Enter a number with a maximum of 2 decimal places"
  val negative = "You must enter a positive number"
  def maximum(amt: BigDecimal) = s"You can not enter a number greater than ${formatter.format(amt)}"

  val errorDateInvalidCharacters = "Enter a date using numbers 0 to 9"
  val errorDateDay = "Enter numbers between 1 and 31"
  val errorDateMonth = "Enter numbers between 1 and 12"
  val errorDateYear = "Enter 4 numbers"

}
