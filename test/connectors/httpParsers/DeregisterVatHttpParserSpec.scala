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

package connectors.httpParsers

import models.{DeregisterVatSuccess, ErrorModel}
import play.api.http.Status
import play.api.libs.json.{Format, JsObject, Json}
import uk.gov.hmrc.http.HttpResponse
import utils.TestUtil

class DeregisterVatHttpParserSpec extends TestUtil {

  case class TestModel(foo: String)
  object TestModel {
    implicit val fmt: Format[TestModel] = Json.format[TestModel]
  }

  val testModel = TestModel("bar")
  val testValidJson: JsObject = Json.obj("foo" -> "bar")
  val testInvalidJson: JsObject = Json.obj()

  "The DeregisterVatHttpParser.getReads" when {

    "the http response status is OK with valid Json" should {

      "return the expected model" in {
        DeregisterVatHttpParser.getReads[TestModel].read("", "", HttpResponse(Status.OK, Some(testValidJson))) shouldBe Right(testModel)
      }
    }

    "the http response status is OK with invalid Json" should {

      "return an ErrorModel" in {
        DeregisterVatHttpParser.getReads[TestModel].read("", "", HttpResponse(Status.OK, Some(testInvalidJson))) shouldBe
          Left(ErrorModel(Status.INTERNAL_SERVER_ERROR, "Invalid Json returned from deregister-vat"))
      }
    }

    "the http response status is NOT OK" should {

      "return an ErrorModel" in {
        DeregisterVatHttpParser.getReads[TestModel].read("", "", HttpResponse(Status.BAD_REQUEST)) shouldBe
          Left(ErrorModel(Status.BAD_REQUEST, "Downstream error returned when retrieving Model from Deregister Vat"))
      }
    }
  }

  "The DeregisterVatHttpParser.updateReads" when {

    "the http response status is NO_CONTENT" should {

      "return the expected model" in {
        DeregisterVatHttpParser.updateReads[TestModel].read("", "", HttpResponse(Status.NO_CONTENT)) shouldBe Right(DeregisterVatSuccess)
      }
    }

    "the http response status is NOT OK" should {

      "return an ErrorModel" in {
        DeregisterVatHttpParser.updateReads[TestModel].read("", "", HttpResponse(Status.BAD_REQUEST)) shouldBe
          Left(ErrorModel(Status.BAD_REQUEST, "Downstream error returned when updating Deregister Vat"))
      }
    }
  }
}
