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

package stubs

import com.github.tomakehurst.wiremock.client.WireMock.{equalToJson, putRequestedFor, urlEqualTo, verify}
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import helpers.WireMockMethods
import play.api.http.Status._
import play.api.libs.json.{JsValue, Json}

object VatSubscriptionStub extends WireMockMethods {

  def noDeregPending(): StubMapping = {
    when(method = GET, uri = "/vat-subscription/([0-9]+)/full-information")
      .thenReturn(status = OK, body = Json.obj(
        "customerDetails" -> Json.obj(
          "isInsolvent" -> false
        ),
        "changeIndicators" -> Json.obj(
          "deregister" -> false
        )
      ))
  }

  def deregPending(): StubMapping = {
    when(method = GET, uri = "/vat-subscription/([0-9]+)/full-information")
      .thenReturn(status = OK, body = Json.obj(
        "customerDetails" -> Json.obj(
          "isInsolvent" -> false
        ),
        "changeIndicators" -> Json.obj(
          "deregister" -> true
        )
      ))
  }

  def noPendingData(): StubMapping = {
    when(method = GET, uri = "/vat-subscription/([0-9]+)/full-information")
      .thenReturn(status = OK, body = Json.obj(
        "customerDetails" -> Json.obj(
          "isInsolvent" -> false
        ),
        "commsPreference" -> "DIGITAL"
      ))
  }

  def deregisterForVatSuccess(): StubMapping = {
    when(method = PUT, uri = "/vat-subscription/([0-9]+)/deregister")
      .thenReturn(status = OK, body = Json.obj(
        "formBundleIdentifier" -> "12345"
      ))
  }

  def deregisterForVatFailure(): StubMapping = {
    when(method = PUT, uri = "/vat-subscription/([0-9]+)/deregister")
      .thenReturn(status = SERVICE_UNAVAILABLE, body = Json.obj())
  }

  def verifyDeregistration(body: JsValue): Unit =
    verify(putRequestedFor(urlEqualTo("/vat-subscription/968501689/deregister"))
      .withRequestBody(equalToJson(body.toString()))
    )
}
