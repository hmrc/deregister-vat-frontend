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

package assets.constants

import models.ErrorModel
import play.api.http.Status
import play.api.libs.json.{Format, JsObject, Json}

object BaseTestConstants {

  val vrn = "968501689"
  val arn = "TARN1234567"
  val agentEmail = "agentEmail@test.com"

  case class TestModel(foo: String)

  object TestModel {
    implicit val fmt: Format[TestModel] = Json.format[TestModel]
  }

  val testKey = "testKey"
  val testModel = TestModel("bar")
  val testValidJson: JsObject = Json.obj("foo" -> "bar")
  val testInvalidJson: JsObject = Json.obj()
  val errorModel = ErrorModel(Status.INTERNAL_SERVER_ERROR, "Error")

}
