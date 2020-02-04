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

object OptionTaxMessages extends BaseMessages {

  val title: String = "Has the business charged or claimed VAT on land or commercial property?" + titleSuffix
  val heading = "Has the business charged or claimed VAT on land or commercial property?"
  val text = "You can choose to charge VAT on any income earned from renting or selling land or commercial property. This is known as ‘Option to Tax’."
  val hint = "What is the total value?"
  val yesNoError = "Select yes if the business charged or claimed VAT on land or commercial property"
  val emptyAmount = "Enter the total value of the land or commercial property"

}
