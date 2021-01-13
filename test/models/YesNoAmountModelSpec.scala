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

package models

import play.api.libs.json.Json
import utils.TestUtil

class YesNoAmountModelSpec extends TestUtil {

  val testAmount = 99999
  "YesNoAmount.isValid" should {

    "return true" when {

      "given a Yes and an amount" in {
        YesNoAmountModel(Yes,Some(testAmount)).isValid shouldBe true
      }

      "given a No and an amount" in {
        YesNoAmountModel(No,Some(testAmount)).isValid shouldBe true
      }

      "given a No and no amount" in {
        YesNoAmountModel(No,None).isValid shouldBe true
      }
    }

    "return false" when {

      "given a Yes and no amount" in {
        YesNoAmountModel(Yes,None).isValid shouldBe false
      }
    }
  }

  "YesNoAmountModel.format" should {

    "serialize to the correct JSON" in {
      Json.toJson(YesNoAmountModel(Yes,Some(testAmount))) shouldBe
        Json.obj(
          "yesNo" -> Json.obj(YesNo.id -> true),
          "amount" -> testAmount
        )
    }

    "deserialize from JSON correctly" in {
      Json.obj(
        "yesNo" -> Json.obj(YesNo.id -> true),
        "amount" -> testAmount
      ).as[YesNoAmountModel] shouldBe YesNoAmountModel(Yes,Some(testAmount))

    }
  }
}