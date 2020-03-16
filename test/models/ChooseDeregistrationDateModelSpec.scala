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

package models

import utils.TestUtil

class ChooseDeregistrationDateModelSpec extends TestUtil {

  val validDate = DateModel(1,1,2018)

  "ChooseDeregistrationDateModel.customerApply" should{

    "return a model with a yes and a date" when {

      "given a Yes and 3 valid numbers" in {
        ChooseDeregistrationDateModel.customApply(Yes,Some(1),Some(1),Some(2018)) shouldBe
          ChooseDeregistrationDateModel(Yes,Some(validDate))
      }
    }

    "return a model containing Yes and no date" when {

      "given a Yes and a None for day, month or date" in {
        ChooseDeregistrationDateModel.customApply(Yes,None,Some(1),Some(2018)) shouldBe
          ChooseDeregistrationDateModel(Yes,None)
        ChooseDeregistrationDateModel.customApply(Yes,Some(1),None,Some(2018)) shouldBe
          ChooseDeregistrationDateModel(Yes,None)
        ChooseDeregistrationDateModel.customApply(Yes,Some(1),Some(1),None) shouldBe
          ChooseDeregistrationDateModel(Yes,None)
        ChooseDeregistrationDateModel.customApply(Yes,None,None,None) shouldBe
          ChooseDeregistrationDateModel(Yes,None)
      }
    }

    "return a model containing No and no date" when {

      "given a No and anything" in {
        ChooseDeregistrationDateModel.customApply(No,Some(1),Some(1),Some(2018)) shouldBe
          ChooseDeregistrationDateModel(No,None)
        ChooseDeregistrationDateModel.customApply(No,Some(1),None,None) shouldBe
          ChooseDeregistrationDateModel(No,None)
        ChooseDeregistrationDateModel.customApply(No,None,Some(1),None) shouldBe
          ChooseDeregistrationDateModel(No,None)
        ChooseDeregistrationDateModel.customApply(No,None,None,Some(2018)) shouldBe
          ChooseDeregistrationDateModel(No,None)
        ChooseDeregistrationDateModel.customApply(No,None,None,None) shouldBe
          ChooseDeregistrationDateModel(No,None)
      }
    }
  }

  "ChooseDeregistrationDateModel.customUnnaply" should {

    "return Yes and a date" when {

      "ChooseDeregistrationDateModel has both a Yes and a Date" in {
        ChooseDeregistrationDateModel.customUnapply(ChooseDeregistrationDateModel(Yes, Some(validDate))) shouldBe Some((Yes, Some(1), Some(1), Some(2018)))
      }
    }

    "return Yes and 3 None" when {

      "ChooseDeregistrationDateModel has both a Yes and no date" in {
        ChooseDeregistrationDateModel.customUnapply(ChooseDeregistrationDateModel(Yes, None)) shouldBe Some((Yes, None, None, None))
      }
    }

    "return No and 3 None" when {

      "ChooseDeregistrationDateModel has a No and Some(date) or None" in {
        ChooseDeregistrationDateModel.customUnapply(ChooseDeregistrationDateModel(No, None)) shouldBe Some((No, None, None, None))
        ChooseDeregistrationDateModel.customUnapply(ChooseDeregistrationDateModel(No, Some(validDate))) shouldBe Some((No, Some(1), Some(1), Some(2018)))
      }
    }
  }
}
