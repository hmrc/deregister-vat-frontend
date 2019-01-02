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

package connectors.mocks

import connectors.VatSubscriptionConnector
import models.deregistrationRequest.DeregistrationInfo
import models.{ErrorModel, VatSubscriptionResponse}
import org.scalamock.scalatest.MockFactory
import uk.gov.hmrc.http.HeaderCarrier
import utils.TestUtil

import scala.concurrent.ExecutionContext

trait MockVatSubscriptionConnector extends TestUtil with MockFactory {

  val mockVatSubscriptionConnector: VatSubscriptionConnector = mock[VatSubscriptionConnector]

  def setupMockSubmit(vrn: String, data: DeregistrationInfo)(response: Either[ErrorModel, VatSubscriptionResponse]): Unit = {
    (mockVatSubscriptionConnector.submit(_: String, _: DeregistrationInfo)(_: HeaderCarrier, _: ExecutionContext))
      .expects(vrn, data, *, *)
      .returns(response)
  }
}
