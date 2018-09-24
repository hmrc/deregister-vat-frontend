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

import assets.constants.CheckYourAnswersTestConstants._
import utils.TestUtil

class CheckYourAnswersModelSpec extends TestUtil {

  "CheckYourAnswersModel.seqAnswers" when {

    "given every possible answer" should {

      "return a sequence containing every answers" in {
        CheckYourAnswersModel(
          Some(Ceased),
          Some(dateModel),
          Some(taxableTurnoverAbove),
          Some(taxableTurnoverBelow),
          Some(whyTurnoverBelowAll),
          Some(vatAccountsModel),
          Some(yesNoAmountYes),
          Some(yesNoAmountYes),
          Some(yesNoAmountYes),
          Some(Yes),
          Some(Yes),
          Some(deregistrationDate)
        ).seqAnswers shouldBe
          Seq(
            deregReasonRow,
            ceasedTradingRow,
            taxableTurnoverRow,
            nextTaxableTurnoverRow,
            whyBelowRowMax,
            vatAccountsRow,
            optionTaxRowYes,
            optionTaxValueRow,
            stocksRowYes,
            stocksValueRow,
            captialAssetsRowYes,
            captialAssetsValueRow,
            owesMoneyRow,
            outstandingInvoicesRow,
            deregDateRow
          )
      }

      "given one answer to every two part question" should {

        "return a sequence that doesn't include specific rows" in {
          CheckYourAnswersModel(
            Some(Ceased),
            Some(dateModel),
            Some(taxableTurnoverAbove),
            Some(taxableTurnoverBelow),
            Some(whyTurnoverBelowAll),
            Some(vatAccountsModel),
            Some(yesNoAmountNo),
            Some(yesNoAmountNo),
            Some(yesNoAmountNo),
            Some(Yes),
            Some(Yes),
            Some(deregistrationDate)
          ).seqAnswers shouldBe
            Seq(
              deregReasonRow,
              ceasedTradingRow,
              taxableTurnoverRow,
              nextTaxableTurnoverRow,
              whyBelowRowMax,
              vatAccountsRow,
              optionTaxRowNo,
              stocksRowNo,
              captialAssetsRowNo,
              owesMoneyRow,
              outstandingInvoicesRow,
              deregDateRow
            )
        }
      }

      "given one answer for the whyTurnover question" should {

        "return a sequence with only one entry for the whyTurnover" in {
          CheckYourAnswersModel(
            Some(Ceased),
            Some(dateModel),
            Some(taxableTurnoverAbove),
            Some(taxableTurnoverBelow),
            Some(whyTurnoverBelowMin),
            Some(vatAccountsModel),
            Some(yesNoAmountNo),
            Some(yesNoAmountNo),
            Some(yesNoAmountNo),
            Some(Yes),
            Some(Yes),
            Some(deregistrationDate)
          ).seqAnswers shouldBe
            Seq(
              deregReasonRow,
              ceasedTradingRow,
              taxableTurnoverRow,
              nextTaxableTurnoverRow,
              whyBelowRowMin,
              vatAccountsRow,
              optionTaxRowNo,
              stocksRowNo,
              captialAssetsRowNo,
              owesMoneyRow,
              outstandingInvoicesRow,
              deregDateRow
            )
        }
      }

      "given no answers" should {

        "return an empty sequence" in {
          CheckYourAnswersModel(None,None,None,None,None,None,None,None,None,None,None,None).seqAnswers shouldBe
            Seq.empty[CheckYourAnswersRowModel]
        }
      }
    }
  }
}
