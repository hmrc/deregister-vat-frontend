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

package assets.messages

object DeregistrationDateMessages extends BaseMessages {

  val title: String = "What is the cancellation date?" + titleSuffix
  val errorTitle: String = "Error: " + title
  val errorSummaryTitle: String = "There is a problem"
  val heading: String = "What is the cancellation date?"
  val p1: String = "The date can be up to a maximum of 3 months from today."
  val p2: String = "We’ll confirm the date when we accept your request."
  val hintText: String = "For example, 31 3 1980"

}
