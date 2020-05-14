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

object DeregistrationConfirmationMessages extends BaseMessages {

  val heading = "You have asked to cancel the VAT registration"
  val title: String = heading + titleSuffix
  val agentTitle: String = heading + titleSuffixAgent
  val subheading = "What happens next"

  val contactPrefError = "We’ll send you an update within 15 working days."

  val contactDetails = "Make sure your contact details are up to date."

  val digitalPreference: String = "We’ll send you an email within 2 working days with an update, followed by a " +
    "letter to your principal place of business. You can also check your HMRC secure messages for an update."

  val emailPreference: String = "We’ll send you an email within 2 working days with an update " +
    "or you can check your HMRC secure messages."

  val paperPreference = "We’ll send a letter to your principal place of business with an update within 15 working days."

  val textAgentPrefYes: String = "We’ll send an email to agentEmail@test.com within 2 working days telling you " +
    "whether or not the request has been accepted."

  val text2AgentWithOrgName = "We’ll also contact Fake Business Name Limited with an update."

  val textAgentPrefNo: String = "We’ll send a confirmation letter to the agency address registered with HMRC " +
    "within 15 working days."

  val text2AgentPrefNo = "We’ll also contact your client with an update."

  val checkContactDetails = "Check your contact details are up to date."

  val bpOffAgentYesPref = "We’ll send an email to agentEmail@test.com within 2 working days telling you whether we can accept your request."

  val agentWithBName = "We’ll contact Fake Business Name Limited with an update."

  val agentNoBName = "We’ll contact your client with an update."
}
