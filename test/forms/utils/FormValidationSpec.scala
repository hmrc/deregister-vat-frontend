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

package forms.utils

import forms.utils.FormValidation
import play.api.data.validation.{Invalid, Valid}
import uk.gov.hmrc.play.test.UnitSpec

class FormValidationSpec extends UnitSpec with FormValidation {

  val errMsg = "error"

  "Calling the .isNumeric method" should {

    "for non-numeric values" should {

      "return Invalid(errMsg)" in {
        isNumeric(errMsg)("ABCD") shouldBe Invalid(errMsg)
        isNumeric(errMsg)("") shouldBe Invalid(errMsg)
        isNumeric(errMsg)(".") shouldBe Invalid(errMsg)
        isNumeric(errMsg)("e") shouldBe Invalid(errMsg)
        isNumeric(errMsg)("&") shouldBe Invalid(errMsg)
      }
    }

    "for numeric values" should {

      "return Valid" in {
        isNumeric("error")("1234") shouldBe Valid
        isNumeric("error")("1234.12345") shouldBe Valid
        isNumeric("error")("0.01") shouldBe Valid
        isNumeric("error")(".01") shouldBe Valid
      }
    }
  }

  "Calling the .hasMaxTwoDecimals method" should {

    "for values with more that two decimals" should {

      "return Invalid(errMsg)" in {
        hasMaxTwoDecimals(errMsg)("0.001") shouldBe Invalid(errMsg)
        hasMaxTwoDecimals(errMsg)("0.000") shouldBe Invalid(errMsg)
        hasMaxTwoDecimals(errMsg)(".001") shouldBe Invalid(errMsg)
        hasMaxTwoDecimals(errMsg)(".000") shouldBe Invalid(errMsg)
      }
    }

    "for values with less than two decimlas" should {

      "return Valid" in {
        hasMaxTwoDecimals(errMsg)("0.01") shouldBe Valid
        hasMaxTwoDecimals(errMsg)("0.00") shouldBe Valid
        hasMaxTwoDecimals(errMsg)(".1") shouldBe Valid
        hasMaxTwoDecimals(errMsg)("0.") shouldBe Valid
        hasMaxTwoDecimals(errMsg)("0") shouldBe Valid
        hasMaxTwoDecimals(errMsg)("100") shouldBe Valid
      }
    }
  }

  "Calling the .isPositive method" should {

    "for values which are less than 0" should {

      "return Invalid(errMsg)" in {
        isPositive(errMsg)(-0.01) shouldBe Invalid(errMsg)
        isPositive(errMsg)(-1) shouldBe Invalid(errMsg)
        isPositive(errMsg)(-.01) shouldBe Invalid(errMsg)
      }
    }

    "for values greater than or equal to 0" should {

      "return Valid" in {
        isPositive(errMsg)(0) shouldBe Valid
        isPositive(errMsg)(0.01) shouldBe Valid
        isPositive(errMsg)(.01) shouldBe Valid
        isPositive(errMsg)(1) shouldBe Valid
      }
    }
  }

  "Calling the .isLessThanMax method" should {

    val max = 1

    "for values which are more than the max supplied" should {

      "return Invalid(errMsg, max)" in {
        doesNotExceed(max, errMsg)(max + 0.01) shouldBe Invalid(errMsg, max)
      }
    }

    "for values greater than or equal to the max" should {

      "return Valid" in {
        doesNotExceed(max, errMsg)(max) shouldBe Valid
        doesNotExceed(max, errMsg)(max - 0.01) shouldBe Valid
      }
    }
  }
}
