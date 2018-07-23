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

package Forms

import forms.DeregReasonForm
import models.DeregReasonModel
import uk.gov.hmrc.play.test.UnitSpec

class DeregReasonFormSpec extends UnitSpec {

  "Binding a form with valid data" should {

    val data = Map("dereg-reason" -> "Reason")
    val form = DeregReasonForm.deregReasonForm.bind(data)

    "result in a form with no errors" in {
      form.hasErrors shouldBe false
    }

    "generate the correct model" in {
      form.value shouldBe Some(DeregReasonModel("Reason"))
    }
  }

  "Binding a form with invalid data" when {

    "the no option has been selected" should {

      val missingOption: Map[String, String] = Map.empty
      val form = DeregReasonForm.deregReasonForm.bind(missingOption)

      "result in a form with errors" in {
        form.hasErrors shouldBe true
      }

      "throw one error" in {
        form.errors.size shouldBe 1
      }
    }
  }

  "A form built from a valid model" should {

    "generate the correct mapping" in {
      val model = DeregReasonModel("Another Reason")
      val form = DeregReasonForm.deregReasonForm.fill(model)
      form.data shouldBe Map("dereg-reason" -> "Another Reason")
    }
  }

}
