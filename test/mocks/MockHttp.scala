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

package mocks


import models.{DeregisterVatResponse, ErrorModel}
import org.scalamock.scalatest.MockFactory
import play.api.libs.json.Writes
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpReads}
import utils.TestUtil

import scala.concurrent.{ExecutionContext, Future}

trait MockHttp extends TestUtil with MockFactory {

  val mockHttp: HttpClient = mock[HttpClient]

  def setupMockHttpGet[T](url: String)(response: Future[Either[ErrorModel, T]]): Unit = {
    (mockHttp.GET(_: String, _: Seq[(String, String)], _: Seq[(String, String)])
                 (_: HttpReads[Either[ErrorModel, T]], _: HeaderCarrier, _: ExecutionContext))
      .expects(url, *, *, *, *, *)
      .returns(response)
  }

  def setupMockHttpPut[T](url: String, model: T)(response: Future[Either[ErrorModel, DeregisterVatResponse]]): Unit = {
    (mockHttp.PUT(_: String, _: T, _: Seq[(String, String)])
    (_: Writes[T], _: HttpReads[Either[ErrorModel, DeregisterVatResponse]], _: HeaderCarrier, _: ExecutionContext))
      .expects(url, model, *, *, *, *, *)
      .returns(response)
  }

  def setupMockHttpDelete[T](url: String)(response: Future[Either[ErrorModel, DeregisterVatResponse]]): Unit = {
    (mockHttp.DELETE(_: String, _: Seq[(String, String)])(_: HttpReads[Either[ErrorModel, DeregisterVatResponse]], _: HeaderCarrier, _: ExecutionContext))
      .expects(url, *, *, *, *)
      .returns(response)
  }
}
