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

package stubs

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import helpers.WireMockMethods
import models.ErrorModel
import play.api.http.Status.{INTERNAL_SERVER_ERROR, NOT_FOUND, NO_CONTENT, OK}
import play.api.libs.json.JsValue

object DeregisterVatStub extends WireMockMethods {

  private def deregisterVatUri(vrn: String, key: String) = s"/deregister-vat/data/$vrn/$key"
  private def deregisterVatUri(vrn: String) = s"/deregister-vat/data/$vrn"

  def successfulPutAnswer(vrn: String, key: String): StubMapping = {
    when(method = PUT, uri = deregisterVatUri(vrn,key))
      .thenReturn(status = NO_CONTENT)
  }

  def successfulGetAnswer(vrn: String, key: String)(jsonBody: JsValue): StubMapping = {
    when(method = GET, uri = deregisterVatUri(vrn,key))
      .thenReturn(status = OK, body = jsonBody)
  }

  def successfulGetNoDataAnswer(vrn: String, key: String): StubMapping = {
    when(method = GET, uri = deregisterVatUri(vrn,key))
      .thenReturn(status = NOT_FOUND)
  }

  def successfulDeleteAnswer(vrn: String, key: String): StubMapping = {
    when(method = DELETE, uri = deregisterVatUri(vrn,key))
      .thenReturn(status = NO_CONTENT)
  }

  def successfulDeleteAllAnswers(vrn: String): StubMapping = {
    when(method = DELETE, uri = deregisterVatUri(vrn))
      .thenReturn(status = NO_CONTENT)
  }

  def putAnswerError(vrn: String, key: String): StubMapping = {
    when(method = PUT, uri = deregisterVatUri(vrn,key))
      .thenReturn(status = INTERNAL_SERVER_ERROR, body = ErrorModel(INTERNAL_SERVER_ERROR,"error"))
  }

  def getAnswerError(vrn: String, key: String): StubMapping = {
    when(method = GET, uri = deregisterVatUri(vrn,key))
      .thenReturn(status = INTERNAL_SERVER_ERROR, body = ErrorModel(INTERNAL_SERVER_ERROR,"error"))
  }

  def deleteAnswerError(vrn: String, key: String): StubMapping = {
    when(method = DELETE, uri = deregisterVatUri(vrn,key))
      .thenReturn(status = INTERNAL_SERVER_ERROR, body = ErrorModel(INTERNAL_SERVER_ERROR,"error"))
  }
}
