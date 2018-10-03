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

package services.mocks

import models._
import org.scalamock.scalatest.MockFactory
import play.api.libs.json.Format
import services.OutstandingInvoicesAnswerService
import uk.gov.hmrc.http.HeaderCarrier
import utils.TestUtil

import scala.concurrent.ExecutionContext

trait MockOutstandingInvoicesService extends TestUtil with MockFactory {

  val mockOutstandingInvoicesService: OutstandingInvoicesAnswerService = mock[OutstandingInvoicesAnswerService]

  def setupMockGetOutstandingInvoices(response: Either[ErrorModel, Option[YesNo]])(implicit user: User[_]): Unit =
    (mockOutstandingInvoicesService.getAnswer(_: User[_], _: Format[YesNo], _: HeaderCarrier, _: ExecutionContext))
      .expects(user, *, *, *)
      .returns(response)

  def setupMockStoreOutstandingInvoices(data: YesNo)(response: Either[ErrorModel, DeregisterVatResponse])(implicit user: User[_]): Unit =
    (mockOutstandingInvoicesService.storeAnswer(_: YesNo)(_: User[_], _: Format[YesNo], _: HeaderCarrier, _: ExecutionContext))
      .expects(data, user, *, *, *)
      .returns(response)

  def setupMockDeleteOutstandingInvoices(response: Either[ErrorModel, DeregisterVatResponse])(implicit user: User[_]): Unit =
    (mockOutstandingInvoicesService.deleteAnswer(_: User[_], _: HeaderCarrier, _: ExecutionContext))
      .expects(user, *, *)
      .returns(response)

  def setupMockDeleteOutstandingInvoicesNotCalled()(implicit user: User[_]): Unit =
    (mockOutstandingInvoicesService.deleteAnswer(_: User[_], _: HeaderCarrier, _: ExecutionContext))
      .expects(user, *, *)
      .never()
}
