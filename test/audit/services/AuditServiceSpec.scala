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

package audit.services

import audit.mocks.MockAuditConnector
import audit.models.ContactPreferenceAuditModel
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.play.audit.http.connector.AuditResult.Success
import utils.TestUtil

import scala.concurrent.Future

class AuditServiceSpec extends TestUtil with MockAuditConnector {

  "Calling .auditExtendedEvent" should {

    val model = ContactPreferenceAuditModel("999999999", "DIGITAL")
    lazy val auditService = new AuditService(mockAuditConnector)

    "audit an event" in {
      setupMockSendExplicitAudit[JsValue](model.auditType, Json.toJson(model))(Future.successful(Success))
      auditService.auditExtendedEvent(model)
    }
  }
}