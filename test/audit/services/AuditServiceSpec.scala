/*
 * Copyright 2022 HM Revenue & Customs
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
import audit.models.ExtendedAuditModel
import play.api.libs.json.{JsValue, Json, Writes}
import utils.TestUtil

class AuditServiceSpec extends TestUtil with MockAuditConnector {

  "Calling .auditExtendedEvent" should {

    case class ExampleAuditModel(value: Int) extends ExtendedAuditModel {
      override val transactionName: String = "my-transaction"
      override val detail: JsValue = Json.toJson(this)
      override val auditType: String = "ExampleAudit"
    }

    object ExampleAuditModel {
      implicit val writes: Writes[ExampleAuditModel] = Writes { model => Json.obj("fieldName" -> model.value) }
    }

    val model = ExampleAuditModel(1)
    lazy val auditService = new AuditService(mockAuditConnector)

    "audit an event" in {
      setupMockSendExplicitAudit[JsValue](model.auditType, Json.toJson(model))
      auditService.auditExtendedEvent(model)
    }
  }
}
