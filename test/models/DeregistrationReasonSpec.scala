/*
 * Copyright 2024 HM Revenue & Customs
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

import play.api.libs.json.{JsString, Json}
import utils.TestUtil

class DeregistrationReasonSpec extends TestUtil {

  "DeregistrationReason.Ceased" should {

    "serialize to the correct JSON" in {
      Json.toJson(Ceased) shouldBe Json.obj(DeregistrationReason.id -> Ceased.value)
    }

    "serialize to the correct JSON when using submissionWrites" in {
      Json.toJson(Ceased)(Ceased.submissionWrites) shouldBe JsString(Ceased.value)
    }

    "deserialize from the correct JSON" in {
      Json.obj(DeregistrationReason.id -> Ceased.value).as[DeregistrationReason] shouldBe Ceased
    }
  }

  "DeregistrationReason.BelowThreshold" should {

    "serialize to the correct JSON" in {
      Json.toJson(BelowThreshold) shouldBe Json.obj(DeregistrationReason.id -> BelowThreshold.value)
    }

    "serialize to the correct JSON when using submissionWrites" in {
      Json.toJson(BelowThreshold)(BelowThreshold.submissionWrites) shouldBe JsString(BelowThreshold.value)
    }

    "deserialize from the correct JSON" in {
      Json.obj(DeregistrationReason.id -> BelowThreshold.value).as[DeregistrationReason] shouldBe BelowThreshold
    }
  }

  "DeregistrationReason.ZeroRated" should {

    "serialize to the correct JSON" in {
      Json.toJson(ZeroRated) shouldBe Json.obj(DeregistrationReason.id -> ZeroRated.value)
    }

    "serialize to the correct JSON when using submissionWrites" in {
      Json.toJson(ZeroRated)(ZeroRated.submissionWrites) shouldBe JsString(ZeroRated.value)
    }

    "deserialize from the correct JSON" in {
      Json.obj(DeregistrationReason.id -> ZeroRated.value).as[DeregistrationReason] shouldBe ZeroRated
    }
  }

  "DeregistrationReason.ExemptOnly" should {

    "serialize to the correct JSON" in {
      Json.toJson(ExemptOnly) shouldBe Json.obj(DeregistrationReason.id -> ExemptOnly.value)
    }

    "serialize to the correct JSON when using submissionWrites" in {
      Json.toJson(ExemptOnly)(ExemptOnly.submissionWrites) shouldBe JsString(ExemptOnly.value)
    }

    "deserialize from the correct JSON" in {
      Json.obj(DeregistrationReason.id -> ExemptOnly.value).as[DeregistrationReason] shouldBe ExemptOnly
    }
  }

  "DeregistrationReason.Other" should {

    "serialize to the correct JSON" in {
      Json.toJson(Other) shouldBe Json.obj(DeregistrationReason.id -> Other.value)
    }

    "serialize to the correct JSON when using submissionWrites" in {
      Json.toJson(Other)(Other.submissionWrites) shouldBe JsString(Other.value)
    }

    "deserialize from the correct JSON" in {
      Json.obj(DeregistrationReason.id -> Other.value).as[DeregistrationReason] shouldBe Other
    }
  }
}
