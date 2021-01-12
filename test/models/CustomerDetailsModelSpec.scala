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

import uk.gov.hmrc.play.test.UnitSpec
import assets.constants.CustomerDetailsTestConstants._

class CustomerDetailsModelSpec extends UnitSpec {

  "Deserializing from JSON" when {

    "all registration fields are populated" in {
      customerDetailsJsonMax.as[CustomerDetails] shouldBe customerDetailsMax
    }

    "optional values are not supplied" in {
      customerDetailsJsonMin.as[CustomerDetails] shouldBe customerDetailsMin
    }

    "there is a deregistration change indicator populated" in {
      customerDetailsPendingDeregJson.as[CustomerDetails] shouldBe customerDetailsPendingDereg
    }

    "there is a deregistration date populated" in {
      customerDetailsAlreadyDeregisteredJson.as[CustomerDetails] shouldBe customerDetailsAlreadyDeregistered
    }
  }

  "Calling .isInsolventWithoutAccess" should {

    "return true when the user is insolvent and not continuing to trade" in {
      customerDetailsInsolvent.isInsolventWithoutAccess shouldBe true
    }

    "return false when the user is insolvent but is continuing to trade" in {
      customerDetailsInsolvent.copy(continueToTrade = Some(true)).isInsolventWithoutAccess shouldBe false
    }

    "return false when the user is not insolvent, regardless of the continueToTrade flag" in {
      customerDetailsMax.isInsolventWithoutAccess shouldBe false
      customerDetailsMax.copy(continueToTrade = Some(false)).isInsolventWithoutAccess shouldBe false
      customerDetailsMax.copy(continueToTrade = None).isInsolventWithoutAccess shouldBe false
    }
  }
}
