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

object NextTaxableTurnoverMessages extends BaseMessages {

  val title = "What is the business’s expected taxable turnover for the next 12 months?" + titleSuffix
  val heading = "What is the business’s expected taxable turnover for the next 12 months?"

  val mandatory = "Enter the business’s taxable turnover"
  val nonNumeric = "Enter the turnover using numbers 0 to 9"

}
