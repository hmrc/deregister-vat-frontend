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

package stubs

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import helpers.WireMockMethods
import play.api.http.Status.NO_CONTENT

object DeregisterVatStub extends WireMockMethods {

  private def deregisterVatUri(vrn: String, key: String) = s"/deregister-vat/data/$vrn/$key"

  def successfulPutAnswer(vrn: String, key: String): StubMapping = {
    when(method = PUT, uri = deregisterVatUri(vrn,key))
      .thenReturn(status = NO_CONTENT)
  }

}
