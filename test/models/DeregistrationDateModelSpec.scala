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

package models

import utils.TestUtil

class DeregistrationDateModelSpec extends TestUtil {

  val validDate = DateModel(1,1,2018)

  "DeregistrationDateModel.checkValidDateIfYes" should {

    "return true" when{

      "given a Yes and a date" in {
        DeregistrationDateModel(Yes,Some(validDate)).checkValidDateIfYes shouldBe true
      }

      "given a No and a date" in {
        DeregistrationDateModel(No,Some(validDate)).checkValidDateIfYes shouldBe true
      }

      "given a No and no date" in {
        DeregistrationDateModel(No,None).checkValidDateIfYes shouldBe true
      }
    }

    "return false" when {

      "given a Yes but no date" in {
        DeregistrationDateModel(Yes, None).checkValidDateIfYes shouldBe false
      }
    }
  }

  "DeregistrationDateModel.customerApply" should{

    "return a model with a yes and a date" when {

      "given a Yes and 3 valid numbers" in {
        DeregistrationDateModel.customApply(Yes,Some(1),Some(1),Some(2018)) shouldBe
          DeregistrationDateModel(Yes,Some(validDate))
      }
    }

    "return a model containing Yes and no date" when {

      "given a Yes and a None for day, month or date" in {
        DeregistrationDateModel.customApply(Yes,None,Some(1),Some(2018)) shouldBe
          DeregistrationDateModel(Yes,None)
        DeregistrationDateModel.customApply(Yes,Some(1),None,Some(2018)) shouldBe
          DeregistrationDateModel(Yes,None)
        DeregistrationDateModel.customApply(Yes,Some(1),Some(1),None) shouldBe
          DeregistrationDateModel(Yes,None)
        DeregistrationDateModel.customApply(Yes,None,None,None) shouldBe
          DeregistrationDateModel(Yes,None)
      }
    }

    "return a model containing No and no date" when {

      "given a No and anything" in {
        DeregistrationDateModel.customApply(No,Some(1),Some(1),Some(2018)) shouldBe
          DeregistrationDateModel(No,None)
        DeregistrationDateModel.customApply(No,Some(1),None,None) shouldBe
          DeregistrationDateModel(No,None)
        DeregistrationDateModel.customApply(No,None,Some(1),None) shouldBe
          DeregistrationDateModel(No,None)
        DeregistrationDateModel.customApply(No,None,None,Some(2018)) shouldBe
          DeregistrationDateModel(No,None)
        DeregistrationDateModel.customApply(No,None,None,None) shouldBe
          DeregistrationDateModel(No,None)
      }
    }
  }

  "DeregistationDateModel.customUnnaply" should {

    "return Yes and a date" when {

      "DeregistrationDateModel has both a Yes and a Date" in {
        DeregistrationDateModel.customUnapply(DeregistrationDateModel(Yes, Some(validDate))) shouldBe Some((Yes, Some(1), Some(1), Some(2018)))
      }
    }

    "return Yes and 3 None" when {

      "DeregistrationDateModel has both a Yes and no date" in {
        DeregistrationDateModel.customUnapply(DeregistrationDateModel(Yes, None)) shouldBe Some((Yes, None, None, None))
      }
    }

    "return No and 3 None" when {

      "DeregistrationDateModel has a No and Some(date) or None" in {
        DeregistrationDateModel.customUnapply(DeregistrationDateModel(No, None)) shouldBe Some((No, None, None, None))
        DeregistrationDateModel.customUnapply(DeregistrationDateModel(No, Some(validDate))) shouldBe Some((No, Some(1), Some(1), Some(2018)))
      }
    }
  }
}
