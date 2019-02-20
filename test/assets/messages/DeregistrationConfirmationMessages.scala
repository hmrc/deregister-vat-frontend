/*
 * Copyright 2019 HM Revenue & Customs
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

object DeregistrationConfirmationMessages {

  val title = "We have received the request to deregister from VAT"
  val subheading = "What happens next"

  val textNonAgentP1 = "We will send you an update within 15 working days."

  val textNonAgentP2 = "Make sure your contact details are up to date."

  val textAgentPrefYes = "We will send an email to agentEmail@test.com within 2 working days telling you " +
    "whether or not the request has been accepted."

  val text2AgentWithOrgName = "We will also contact Fake Business Name Limited with an update."

  val textAgentPrefNo = "We will send a confirmation letter to the agency address within 15 working days."

  val text2AgentPrefNo = "We will also contact your client with an update."

}
