/*
 * Copyright 2018 HM Revenue & Customs
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

package audit

import audit.mocks.MockAuditConnector
import audit.models.{AuditModel, TestAuditModel}
import uk.gov.hmrc.play.audit.http.connector.AuditResult._

import scala.concurrent.Future

class AuditServiceSpec extends MockAuditConnector {

  object TestAuditService extends AuditService(mockAuditConnector, config)

  lazy val testModel = TestAuditModel("foo", "bar")
  lazy val testPath = "/test/path"

  "AuditService" when {

    "given an AuditModel" should {

      "return success when the Audit is successful" in {
        setupMockSendExtendedEvent(Future.successful(Success))
        await(TestAuditService.auditEvent(testModel, Some(testPath))) shouldBe Success
      }

      "return failure when the Audit is failed" in {
        setupMockSendExtendedEvent(Future.successful(Failure("Error")))
        await(TestAuditService.auditEvent(testModel, Some(testPath))) shouldBe Failure("Error")
      }

      "return disabled when the Audit is disabled" in {
        setupMockSendExtendedEvent(Future.successful(Disabled))
        await(TestAuditService.auditEvent(testModel, Some(testPath))) shouldBe Disabled
      }
    }
  }
}