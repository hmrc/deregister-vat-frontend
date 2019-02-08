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

package stubs

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import helpers.WireMockMethods
import play.api.http.Status.OK
import play.api.libs.json.Json

object PendingDeregStub extends WireMockMethods {

  def noDeregPending(): StubMapping = {
    when(method = GET, uri = "/vat-subscription/([0-9]+)/full-information")
      .thenReturn(status = OK, body = Json.obj(
        "changeIndicators" -> Json.obj(
          "deregister" -> false
        )
      ))
  }

  def deregPending(): StubMapping = {
    when(method = GET, uri = "/vat-subscription/([0-9]+)/full-information")
      .thenReturn(status = OK, body = Json.obj(
        "changeIndicators" -> Json.obj(
          "deregister" -> true
        )
      ))
  }

  def noPendingData(): StubMapping = {
    when(method = GET, uri = "/vat-subscription/([0-9]+)/full-information")
      .thenReturn(status = OK, body = Json.obj(
        "somethingElse" -> Json.obj()
      ))
  }

}
