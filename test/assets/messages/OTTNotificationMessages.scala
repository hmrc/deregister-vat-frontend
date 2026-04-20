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

object OTTNotificationMessages extends BaseMessages {

  val heading = "Has HMRC been notified about an option to tax land and buildings?"
  val title: String = heading + titleSuffix
  val text = "A notification to opt to tax is usually completed on a VAT 1614A. This concerns a business choosing to charge VAT on its supplies of land and buildings."
  val inset = "If disposing of property, any VAT recovered in relation to its costs and overheads may be repayable to HMRC. At point of deregistration, all properties must be in a Nil VAT position."
  val yesNoError = "Select yes if HMRC has been notified about an option to tax land and buildings"

}
