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

package services.mocks

import models.CustomerDetails
import models.ErrorModel
import org.scalamock.scalatest.MockFactory
import services.CustomerDetailsService
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.test.UnitSpec

import scala.concurrent.ExecutionContext

trait MockCustomerDetailsService extends UnitSpec with MockFactory {

  val mockCustomerDetailsService: CustomerDetailsService = mock[CustomerDetailsService]

  def setupMockCustomerDetails(vrn: String)(response: Either[ErrorModel, CustomerDetails])(implicit hc: HeaderCarrier, ec: ExecutionContext): Unit = {
    (mockCustomerDetailsService.getCustomerDetails(_: String)(_: HeaderCarrier, _: ExecutionContext))
      .expects(vrn, *, *)
      .returns(response)
  }
}
