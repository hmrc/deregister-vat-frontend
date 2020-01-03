/*
 * Copyright 2020 HM Revenue & Customs
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

import connectors.httpParsers.PendingDeregHttpParser.PendingDeregReads
import models.{ChangeIndicatorModel, ErrorModel}
import play.api.http.Status
import uk.gov.hmrc.http.HttpResponse
import utils.TestUtil
import assets.constants.CustomerDetailsTestConstants.{noPendingDereg, pendingDeregFalse, pendingDeregFalseJson}
import play.api.libs.json.Json


class PendingDeregHttpParserSpec extends TestUtil {

  val successBadJson = Some(Json.obj("changeIndicators" -> Json.obj("deregister" -> 1)))
  val errorModel = ErrorModel(Status.BAD_REQUEST, "Error Message")

  private def pendingDeregResult(response: HttpResponse): Either[ErrorModel, ChangeIndicatorModel] =
    PendingDeregReads.read("", "", response)

  "The PendingDeregHttpParser.reads" when {

    "the http response status is OK with valid Json" should {

      "return a CustomerDetailsModel" in {
        PendingDeregReads.read("", "", HttpResponse(Status.OK, Some(pendingDeregFalseJson))) shouldBe Right(pendingDeregFalse)
      }
    }

    "the http response status is OK with no changeIndicators" should {

      "return a 'ChangeIndicators(None)')" in {
        PendingDeregReads.read("", "", HttpResponse(Status.OK, Some(Json.obj()))) shouldBe
          Right(ChangeIndicatorModel(None))
      }
    }

    "the http response status is OK with invalid Json" should {

      "return an Error model with status code of 500 (INTERNAL_SERVER_ERROR)" in {
        PendingDeregReads.read("", "", HttpResponse(Status.OK, successBadJson)) shouldBe
          Left(ErrorModel(Status.INTERNAL_SERVER_ERROR, "Invalid Json"))
      }
    }

    "the http response status unexpected" should {

      "return an ErrorModel" in {
        PendingDeregReads.read("", "", HttpResponse(Status.SEE_OTHER, None)) shouldBe
          Left(ErrorModel(Status.SEE_OTHER,"Downstream error returned when retrieving pending deregistration status"))
      }
    }
  }

}
