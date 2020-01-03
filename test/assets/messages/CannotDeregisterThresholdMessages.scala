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

object CannotDeregisterThresholdMessages extends BaseMessages {

  val title = "The business cannot cancel its VAT registration" + titleSuffix
  val heading = "The business cannot cancel its VAT registration"
  val text = "This is because the business expects its taxable turnover for the next 12 months to be above £83,000."
  val linkText = "You can update the business’s other VAT details."

}
