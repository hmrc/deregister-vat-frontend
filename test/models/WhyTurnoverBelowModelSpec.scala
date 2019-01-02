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

import utils.TestUtil

class WhyTurnoverBelowModelSpec extends TestUtil {

  "WhyTurnoverBelowModel.atLeastOneOptionTrue" when {

    "given at least one true" should {

      "return true" in {
        WhyTurnoverBelowModel(false, false, false, true, false, false, false).hasAtLeastOneSelected shouldBe true
      }
    }

    "given all false" should {

      "return false" in {
        WhyTurnoverBelowModel(false, false, false, false, false, false, false).hasAtLeastOneSelected shouldBe false
      }
    }
  }
}
