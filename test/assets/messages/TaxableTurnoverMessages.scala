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

object TaxableTurnoverMessages extends BaseMessages {

  val formatter = java.text.NumberFormat.getIntegerInstance

  val title = "Was the business’s taxable turnover below £88,000 in the last 12 months?" + titleSuffix
  val agentTitle = "Was the business’s taxable turnover below £88,000 in the last 12 months?" + titleSuffixAgent
  val heading = "Was the business’s taxable turnover below £88,000 in the last 12 months?"
  val mandatory = "Select yes if the turnover was less than £88,000 in the last 12 months"

}
