/*
 * Copyright 2021 HM Revenue & Customs
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

package audit.mocks

import org.scalamock.scalatest.MockFactory
import play.api.libs.json.Writes
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import utils.TestUtil

import scala.concurrent.ExecutionContext

trait MockAuditConnector extends TestUtil with MockFactory {

  val mockAuditConnector: AuditConnector = mock[AuditConnector]

  def setupMockSendExplicitAudit[T](auditType: String, detail: T): Unit =
    (mockAuditConnector.sendExplicitAudit(_: String, _: T)(_: HeaderCarrier, _: ExecutionContext, _: Writes[T]))
      .expects(auditType, detail, *, *, *)
      .atLeastOnce()
}
