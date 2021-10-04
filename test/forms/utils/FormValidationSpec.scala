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

package forms.utils

import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatest.matchers.should.Matchers
import org.scalatest.OptionValues
import play.api.data.validation.{Invalid, Valid}

class FormValidationSpec extends AnyWordSpecLike with Matchers with OptionValues with FormValidation {

  val errMsg = "error"

  "Calling the .isNumeric method" should {

    "for non-numeric values" should {

      "return Invalid(errMsg)" in {
        isNumericConstraint(errMsg)("ABCD") shouldBe Invalid(errMsg)
        isNumericConstraint(errMsg)("") shouldBe Invalid(errMsg)
        isNumericConstraint(errMsg)(".") shouldBe Invalid(errMsg)
        isNumericConstraint(errMsg)("e") shouldBe Invalid(errMsg)
        isNumericConstraint(errMsg)("&") shouldBe Invalid(errMsg)
      }
    }

    "for numeric values" should {

      "return Valid" in {
        isNumericConstraint("error")("1234") shouldBe Valid
        isNumericConstraint("error")("1234.12345") shouldBe Valid
        isNumericConstraint("error")("0.01") shouldBe Valid
        isNumericConstraint("error")(".01") shouldBe Valid
      }
    }
  }

  "Calling the isInteger method" should {

    "return 'Valid' for integer values" in {
      isInt(errMsg)("123") shouldBe Valid
    }

    "return 'Invalid' for non-integer values" in {
      isInt(errMsg)("123.4") shouldBe Invalid(errMsg)
      isInt(errMsg)("123.") shouldBe Invalid(errMsg)
      isInt(errMsg)("") shouldBe Invalid(errMsg)
      isInt(errMsg)(".") shouldBe Invalid(errMsg)
      isInt(errMsg)("e") shouldBe Invalid(errMsg)
      isInt(errMsg)("&") shouldBe Invalid(errMsg)
    }
  }

  "Calling the 'characters' method" should {

    val length = 5
    val errorFew = "tooFew"
    val errorMany = "tooMany"

    "return 'Valid' when the character length is matched" in {
      characters(length, errorFew, errorMany)("12345") shouldBe Valid
    }

    "return 'Invalid' when the character length is not matched" in {
      characters(length, errorFew, errorMany)("123456") shouldBe Invalid(errorMany)
      characters(length, errorFew, errorMany)("1234") shouldBe Invalid(errorFew)
    }
  }

  "Calling the .hasMaxTwoDecimals method" should {

    "for values with more that two decimals" should {

      "return Invalid(errMsg)" in {
        hasMaxTwoDecimalsConstraint(errMsg)("0.001") shouldBe Invalid(errMsg)
        hasMaxTwoDecimalsConstraint(errMsg)("0.000") shouldBe Invalid(errMsg)
        hasMaxTwoDecimalsConstraint(errMsg)(".001") shouldBe Invalid(errMsg)
        hasMaxTwoDecimalsConstraint(errMsg)(".000") shouldBe Invalid(errMsg)
      }
    }

    "for values with less than two decimlas" should {

      "return Valid" in {
        hasMaxTwoDecimalsConstraint(errMsg)("0.01") shouldBe Valid
        hasMaxTwoDecimalsConstraint(errMsg)("0.00") shouldBe Valid
        hasMaxTwoDecimalsConstraint(errMsg)(".1") shouldBe Valid
        hasMaxTwoDecimalsConstraint(errMsg)("0.") shouldBe Valid
        hasMaxTwoDecimalsConstraint(errMsg)("0") shouldBe Valid
        hasMaxTwoDecimalsConstraint(errMsg)("100") shouldBe Valid
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

    "for values which are more than or equal to the max supplied" should {

      "return Invalid(errMsg, max)" in {
        doesNotExceed(max, errMsg)(max + 0.01) shouldBe Invalid(errMsg, max)
        doesNotExceed(max, errMsg)(max) shouldBe Invalid(errMsg, max)
      }
    }

    "for values less than the max" should {

      "return Valid" in {
        doesNotExceed(max, errMsg)(max - 0.01) shouldBe Valid
      }
    }
  }

  "Calling the .stripChar method" should {

    "return the expected string with no pound sign included" in {
      stripChar("£1000") shouldBe "1000"
    }

    "return the expected string with no commas included" in {
      stripChar("£100,000,000") shouldBe "100000000"
    }

    "return the expected string if there is a full stop at the end" in {
      stripChar("£10.50.") shouldBe "10.50"
    }

    "return the expected string if a white space is included at the start" in {
      stripChar(" £100") shouldBe "100"
    }

    "return the expected string if a white space is included at the end" in {
      stripChar("£100 ") shouldBe "100"
    }

    "return the expected string if there is a pound sign and comma present, and a full stop at the end" in {
      stripChar("£100.,50") shouldBe "100.50"
    }

    "return the expected string if there is a whitespace at the start, pound sign and comma present and a full stop at the end" in {
      stripChar(" £100.,50") shouldBe "100.50"
    }

    "return the expected string if there is pound sign and comma present, and a full stop and white space at the end" in {
      stripChar("£100.,50 ") shouldBe "100.50"
    }

  }
}
