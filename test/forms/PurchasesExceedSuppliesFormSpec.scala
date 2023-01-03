/*
 * Copyright 2023 HM Revenue & Customs
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

package forms

import forms.PurchasesExceedSuppliesForm._
import models.{No, Yes}
import org.scalatest.OptionValues
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import play.api.data.FormError

class PurchasesExceedSuppliesFormSpec extends AnyWordSpecLike with Matchers with OptionValues {

  "PurchasesExceedSuppliesForm" should {

    "successfully parse a Yes" in {
      val res = purchasesExceedSuppliesForm.bind(Map(yesNo -> YesNoForm.yes))
      res.value should contain(Yes)
    }

    "successfully parse a No" in {
      val res = purchasesExceedSuppliesForm.bind(Map(yesNo -> YesNoForm.no))
      res.value should contain(No)
    }

    "fail when nothing has been entered" in {
      val res = purchasesExceedSuppliesForm.bind(Map.empty[String, String])
      res.errors should contain(FormError(yesNo, purchasesExceedSupplyError))
      res.errors.size shouldBe 1
    }

    "A form built from a valid model" when {

      "'Yes' is selected" should {

        "generate the correct mapping" in {
          val form = PurchasesExceedSuppliesForm.purchasesExceedSuppliesForm.fill(Yes)
          form.data shouldBe Map(yesNo -> "yes")
        }
      }

      "'No' is selected" should {

        "generate the correct mapping" in {
          val form = PurchasesExceedSuppliesForm.purchasesExceedSuppliesForm.fill(No)
          form.data shouldBe Map(yesNo -> "no")
        }
      }
    }
  }
}
