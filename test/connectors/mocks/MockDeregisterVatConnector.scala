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

package connectors.mocks

import connectors.DeregisterVatConnector
import models.{DeregisterVatResponse, ErrorModel}
import org.scalamock.scalatest.MockFactory
import play.api.libs.json.Format
import uk.gov.hmrc.http.HeaderCarrier
import utils.TestUtil

import scala.concurrent.{ExecutionContext, Future}

trait MockDeregisterVatConnector extends TestUtil with MockFactory {

  val mockDeregisterVatConnector: DeregisterVatConnector = mock[DeregisterVatConnector]

  def setupMockGetAnswers[T](vrn: String, key: String)(response: Future[Either[ErrorModel, Option[T]]]): Unit = {
    (mockDeregisterVatConnector.getAnswers(_: String, _: String)(_: Format[T], _: HeaderCarrier, _: ExecutionContext))
      .expects(vrn, key, *, *, *)
      .returns(response)
  }

  def setupMockStoreAnswers[T](vrn: String, key: String, data: T)(response: Future[Either[ErrorModel, DeregisterVatResponse]]): Unit = {
    (mockDeregisterVatConnector.putAnswers(_: String, _: String, _: T)(_: Format[T], _: HeaderCarrier, _: ExecutionContext))
      .expects(vrn, key, data, *, *, *)
      .returns(response)
  }

  def setupMockDeleteAnswer(vrn: String, key: String)(response: Future[Either[ErrorModel, DeregisterVatResponse]]): Unit = {
    (mockDeregisterVatConnector.deleteAnswer(_: String, _: String)(_: HeaderCarrier, _: ExecutionContext))
      .expects(vrn, key, *, *)
      .returns(response)
  }

  def setupMockDeleteAllAnswers(vrn: String)(response: Future[Either[ErrorModel, DeregisterVatResponse]]): Unit = {
    (mockDeregisterVatConnector.deleteAllAnswers(_: String)(_: HeaderCarrier, _: ExecutionContext))
      .expects(vrn, *, *)
      .returns(response)
  }
}
