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

object SicCodeMessages extends BaseMessages {

  val heading = "What is the business’s Standard Industrial Classification (SIC) Code?"
  val title: String = heading + titleSuffix

  val explanation = "This is the 5 digit code which describes the business activity."
  val findCode = "Find the code that best describes the business activity (opens in a new window or tab)"

  val invalid = "Enter the 5 digit code which best describes your business activity"
  val tooFew = "You have entered too few numbers"
  val tooMany = "You have entered too many numbers"
}
