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

object DeregistrationDateMessages {

  val title = "Does the business want to choose its own deregistration date?"
  val p1 = "The deregistration date can be up to a maximum of 3 months from today."
  val p2 = "We will confirm the deregistration date when your deregistration request is accepted."
  val errorInvalidDate = "Enter a real deregistration date"
  val errorPast = "The deregistration date must be in the future"
  val errorFuture = "The deregistration date must not be more than 3 months from today"

}
