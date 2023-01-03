/*
 * Copyright 2023 HM Revenue & Customs
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

package views.utils

import _root_.utils.TestUtil
import assets.messages.CommonMessages

class ServiceNameUtilSpec extends TestUtil {

  ".generateHeader" when {

    "given a User who is an Agent" should {

      s"return the agent service name ${CommonMessages.agentServiceName}" in {
        ServiceNameUtil.generateHeader(agentUserPrefYes, messages) shouldBe CommonMessages.agentServiceName
      }
    }

    "given a User who is not an Agent" should {

      s"return the client service name ${CommonMessages.clientServiceName}" in {
        ServiceNameUtil.generateHeader(user, messages) shouldBe CommonMessages.clientServiceName
      }
    }

    "NOT given a user" should {

      s"return the client service name ${CommonMessages.clientServiceName}" in {
        ServiceNameUtil.generateHeader(request, messages) shouldBe CommonMessages.otherServiceName
      }
    }

  }

  ".generateServiceUrl" when {

    "given a User who is an Agent" should {

      "return the Agent Hub URL" in {
        ServiceNameUtil.generateServiceUrl(agentUserPrefYes, mockConfig) shouldBe Some(mockConfig.agentClientLookupAgentHubPath)
      }
    }

    "given a User who is not an Agent" should {

      "return the BTA home URL" in {
        ServiceNameUtil.generateServiceUrl(user, mockConfig) shouldBe Some(mockConfig.vatSummaryFrontendUrl)
      }
    }

    "not given a User" should {

      "return None" in {
        ServiceNameUtil.generateServiceUrl(request, mockConfig) shouldBe None
      }
    }
  }
}