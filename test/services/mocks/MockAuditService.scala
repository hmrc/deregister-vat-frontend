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

package services.mocks

import audit.models.ExtendedAuditModel
import audit.services.AuditService
import org.scalamock.scalatest.MockFactory
import org.scalatest.wordspec.AnyWordSpecLike
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext

trait MockAuditService extends AnyWordSpecLike with MockFactory {

  val mockAuditService: AuditService = mock[AuditService]

  def mockAudit(): Any = {
    (mockAuditService.extendedAudit(_: ExtendedAuditModel, _: Option[String])(_: HeaderCarrier, _: ExecutionContext))
      .stubs(*, *, *, *)
      .returns({})
  }
}
