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

import assets.constants.NextTaxableTurnoverTestConstants._
import assets.constants.CheckYourAnswersTestConstants._
import assets.constants.WhyTurnoverBelowTestConstants._
import assets.constants.YesNoAmountTestConstants._
import utils.TestUtil

class CheckYourAnswersModelSpec extends TestUtil {

  "CheckYourAnswersModel.seqAnswers" when {

    "given every possible answer" should {

      "return a sequence containing every answers" in {
        CheckYourAnswersModel(
          Some(Ceased),
          Some(dateModel),
          Some(Yes),
          Some(nextTaxableTurnoverBelow),
          Some(whyTurnoverBelowAll),
          Some(StandardAccounting),
          Some(ottModel),
          Some(assetsModel),
          Some(stocksModel),
          Some(Yes),
          Some(Yes),
          Some(deregistrationDateYes)
        ).seqAnswers shouldBe
          Seq(
            deregReasonRow,
            ceasedTradingRow,
            taxableTurnoverRow(mockConfig.thresholdString),
            nextTaxableTurnoverRow,
            whyBelowRowMax,
            vatAccountsRow,
            optionTaxRowYes,
            optionTaxValueRow,
            captialAssetsRowYes,
            captialAssetsValueRow,
            stocksRowYes,
            stocksValueRow,
            newInvoicesRow,
            outstandingInvoicesRow,
            deregDateRowYes
          )
      }

      "given one answer to every two part question" should {

        "return a sequence that doesn't include specific rows" in {
          CheckYourAnswersModel(
            Some(Ceased),
            Some(dateModel),
            Some(Yes),
            Some(nextTaxableTurnoverBelow),
            Some(whyTurnoverBelowAll),
            Some(StandardAccounting),
            Some(yesNoAmountNo),
            Some(yesNoAmountNo),
            Some(yesNoAmountNo),
            Some(Yes),
            Some(Yes),
            Some(deregistrationDateYes)
          ).seqAnswers shouldBe
            Seq(
              deregReasonRow,
              ceasedTradingRow,
              taxableTurnoverRow(mockConfig.thresholdString),
              nextTaxableTurnoverRow,
              whyBelowRowMax,
              vatAccountsRow,
              optionTaxRowNo,
              captialAssetsRowNo,
              stocksRowNo,
              newInvoicesRow,
              outstandingInvoicesRow,
              deregDateRowYes
            )
        }
      }

      "given one answer for the whyTurnover question" should {

        "return a sequence with only one entry for the whyTurnover" in {
          CheckYourAnswersModel(
            Some(Ceased),
            Some(dateModel),
            Some(Yes),
            Some(nextTaxableTurnoverBelow),
            Some(whyTurnoverBelowMin),
            Some(StandardAccounting),
            Some(yesNoAmountNo),
            Some(yesNoAmountNo),
            Some(yesNoAmountNo),
            Some(Yes),
            Some(Yes),
            Some(deregistrationDateYes)
          ).seqAnswers shouldBe
            Seq(
              deregReasonRow,
              ceasedTradingRow,
              taxableTurnoverRow(mockConfig.thresholdString),
              nextTaxableTurnoverRow,
              whyBelowRowMin,
              vatAccountsRow,
              optionTaxRowNo,
              captialAssetsRowNo,
              stocksRowNo,
              newInvoicesRow,
              outstandingInvoicesRow,
              deregDateRowYes
            )
        }
      }

      "given an answer of 'no' for deregistrationDate" should{

        "return a sequence containing the deregistrationDate row with a value of 'No'" in {
          CheckYourAnswersModel(
            Some(Ceased),
            Some(dateModel),
            Some(Yes),
            Some(nextTaxableTurnoverBelow),
            Some(whyTurnoverBelowMin),
            Some(StandardAccounting),
            Some(yesNoAmountNo),
            Some(yesNoAmountNo),
            Some(yesNoAmountNo),
            Some(Yes),
            Some(Yes),
            Some(deregistrationDateNo)
          ).seqAnswers shouldBe
            Seq(
              deregReasonRow,
              ceasedTradingRow,
              taxableTurnoverRow(mockConfig.thresholdString),
              nextTaxableTurnoverRow,
              whyBelowRowMin,
              vatAccountsRow,
              optionTaxRowNo,
              captialAssetsRowNo,
              stocksRowNo,
              newInvoicesRow,
              outstandingInvoicesRow,
              deregDateRowNo
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
