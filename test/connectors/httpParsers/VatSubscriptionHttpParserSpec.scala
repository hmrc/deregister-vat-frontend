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

package connectors.httpParsers

import models.{ErrorModel, VatSubscriptionSuccess}
import play.api.http.Status
import uk.gov.hmrc.http.HttpResponse
import utils.TestUtil

class VatSubscriptionHttpParserSpec extends TestUtil {

  "The VatSubscriptionHttpParser.updateReads" when {

    "the http response status is OK" should {

      "return the expected model" in {
        VatSubscriptionHttpParser.updateReads.read("", "",
          HttpResponse(Status.OK, "")) shouldBe Right(VatSubscriptionSuccess)
      }
    }

    "the http response status is NOT OK" should {

      "return an ErrorModel" in {
        VatSubscriptionHttpParser.updateReads.read("", "", HttpResponse(Status.BAD_REQUEST, "")) shouldBe
          Left(ErrorModel(Status.BAD_REQUEST, "Downstream error returned when updating Vat Subscription"))
      }
    }
  }
}
