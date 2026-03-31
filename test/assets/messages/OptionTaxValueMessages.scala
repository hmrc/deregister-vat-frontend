/*
 * Copyright 2026 HM Revenue & Customs
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

package messages

import assets.messages.BaseMessages

object OptionTaxValueMessages  extends BaseMessages {

  val heading = "What is the value of the land and buildings you continue to own after VAT registration?"
  val title: String = heading + titleSuffix
  val text = "If all land and buildings have already been sold, enter a zero value."
  val mandatoryError = "Enter the value of the land and buildings in pounds and pence"
  val nonNumericError = "The value of the land and buildings must only contain numbers"
  val negativeError = "The value of the land and buildings cannot be a minus value"


}
