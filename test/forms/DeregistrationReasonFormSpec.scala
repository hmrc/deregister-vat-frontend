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

import models.{BelowThreshold, Ceased, ExemptOnly, Other, ZeroRated}
import org.scalatest.OptionValues
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class DeregistrationReasonFormSpec extends AnyWordSpecLike with Matchers with OptionValues {

  "Binding a form with valid data" should {

    val data = Map(DeregistrationReasonForm.reason -> DeregistrationReasonForm.other)
    val form = DeregistrationReasonForm.deregistrationReasonForm.bind(data)

    "result in a form with no errors" in {
      form.hasErrors shouldBe false
    }

    "generate the correct model" in {
      form.value shouldBe Some(Other)
    }
  }

  "Binding a form with invalid data" when {

    "the no option has been selected" should {

      val missingOption: Map[String, String] = Map.empty
      val form = DeregistrationReasonForm.deregistrationReasonForm.bind(missingOption)

      "result in a form with errors" in {
        form.hasErrors shouldBe true
      }

      "throw one error" in {
        form.errors.size shouldBe 1
      }
    }
  }

  "A form built from a valid model" when {

    "the deregistration reason is ceased trading" should {

      "generate the correct mapping" in {
        val form = DeregistrationReasonForm.deregistrationReasonForm.fill(Ceased)
        form.data shouldBe Map(DeregistrationReasonForm.reason -> DeregistrationReasonForm.ceased)
      }
    }

    "the deregistration reason is below threshold" should {

      "generate the correct mapping" in {
        val form = DeregistrationReasonForm.deregistrationReasonForm.fill(BelowThreshold)
        form.data shouldBe Map(DeregistrationReasonForm.reason -> DeregistrationReasonForm.belowThreshold)
      }
    }

    "the deregistration reason is zero rated" should {

      "generate the correct mapping" in {
        val form = DeregistrationReasonForm.deregistrationReasonForm.fill(ZeroRated)
        form.data shouldBe Map(DeregistrationReasonForm.reason -> DeregistrationReasonForm.zeroRated)
      }
    }

    "the deregistration reason is exempt only" should {

      "generate the correct mapping" in {
        val form = DeregistrationReasonForm.deregistrationReasonForm.fill(ExemptOnly)
        form.data shouldBe Map(DeregistrationReasonForm.reason -> DeregistrationReasonForm.exemptOnly)
      }
    }

    "the deregistration reason is other" should {

      "generate the correct mapping" in {
        val form = DeregistrationReasonForm.deregistrationReasonForm.fill(Other)
        form.data shouldBe Map(DeregistrationReasonForm.reason -> DeregistrationReasonForm.other)
      }
    }
  }

}
