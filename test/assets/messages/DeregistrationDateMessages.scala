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

package assets.messages

object DeregistrationDateMessages extends BaseMessages {

  val title = "Do you want to choose the cancellation date?" + titleSuffix
  val heading = "Do you want to choose the cancellation date?"
  val p1 = "The date can be up to a maximum of 3 months from today."
  val p2 = "We'll confirm the date when we accept your request."
  val errorInvalidDate = "Enter a real deregistration date"
  val errorPast = "The deregistration date must be in the future"
  val errorFuture = "The deregistration date must not be more than 3 months from today"

}
