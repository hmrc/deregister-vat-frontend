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

package messages

import assets.messages.BaseMessages

object DeregistrationOTTConfirmationMessages extends BaseMessages {

  val heading: String = "We have received your request to cancel VAT registration"
  val title: String = heading + titleSuffix
  val agentTitle: String = heading + titleSuffixAgent

  val updateConfirmationAgent: String = "We will send an update about your VAT deregistration to agentEmail@test.com within 2 working days."

  val updateConfirmation: String = "We will send an update about your VAT deregistration within 2 working days."

  val subheading = "What you need to do next"

  val toDoNext_p1 =  "You still need to provide information to HMRC on any land or buildings you have had an interest in that are opted to tax (opens in a new window)."

  val toDoNext_p2 =  "This includes land and buildings that you will continue to own or have an interest in after VAT deregistration."

  val warningMsg = "Warning VAT deregistration could be delayed if you do not provide this information."

  val finishButtonText = "Complete land and buildings information form"

  val govUkLink = "https://www.gov.uk/guidance/opting-to-tax-land-and-buildings-notice-742a"

}
