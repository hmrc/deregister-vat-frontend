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

package models

import play.api.libs.json.Json
import utils.TestUtil

class NumberInputModelSpec extends TestUtil {

  val testAmount = BigDecimal(88326)

  "NumberInputModel.format" should {
    "serialize to the correct JSON" in {
      Json.toJson(NumberInputModel(testAmount)) shouldBe
        Json.obj(
        "value" ->testAmount
        )
    }

    "deserialize from JSON correctly" in {
      Json.obj(
        "value" -> testAmount
      ).as[NumberInputModel] shouldBe NumberInputModel(testAmount)
    }
  }

}
