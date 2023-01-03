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

package models

import assets.constants.CheckYourAnswersTestConstants._
import assets.constants.NextTaxableTurnoverTestConstants._
import assets.constants.WhyTurnoverBelowTestConstants._
import assets.constants.YesNoAmountTestConstants._
import utils.TestUtil

class CheckYourAnswersModelSpec extends TestUtil {

  lazy val cyaModel = CheckYourAnswersModel(
    Some(Ceased),
    Some(dateModel),
    None,
    None,
    None,
    Some(StandardAccounting),
    Some(ottModel),
    Some(assetsModel),
    Some(stocksModel),
    Some(Yes),
    Some(Yes),
    Some(Yes),
    Some(dateModel),
    Some(Yes),
    Some(sicCodeValue),
    Some(zeroRatedSuppliesValue),
    Some(Yes)
  )

  "CheckYourAnswersModel.seqAnswers" when {

    "the dereg reason is Ceased" should {

      "return a sequence containing the answers relevant to the ceased trading journey" in {
        cyaModel.seqAnswers shouldBe
          Seq(
            deregReasonCeasedTradingRow,
            ceasedTradingRow,
            vatAccountsRow,
            optionTaxRowYes,
            optionTaxValueRow,
            captialAssetsRowYes,
            captialAssetsValueRow,
            stocksRowYes,
            stocksValueRow,
            newInvoicesRow,
            outstandingInvoicesRow,
            chooseDeregDateRowYes,
            deregDateRow
          )
      }
    }

    "the dereg reason is BelowThreshold" should {

      "return a sequence containing the answers relevant to the turnover below threshold journey" in {
        cyaModel.copy(
          deregistrationReason = Some(BelowThreshold),
          ceasedTradingDate = None,
          turnover = Some(Yes),
          nextTurnover = Some(nextTaxableTurnoverBelow),
          whyTurnoverBelow = Some(whyTurnoverBelowAll)
        ).seqAnswers shouldBe
          Seq(
            deregReasonTurnoverBelowRow,
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
            chooseDeregDateRowYes,
            deregDateRow
          )
      }
    }

    "the dereg reason is ZeroRated" should {

      "return a sequence containing the answers relevant to the zero-rated journey" in {
        cyaModel.copy(
          deregistrationReason = Some(ZeroRated),
          nextTurnover = Some(nextTaxableTurnoverBelow)
        ).seqAnswers shouldBe
          Seq(
            deregReasonZeroRatedRow,
            businessActivityRow,
            sicCodeRow,
            nextTaxableTurnoverRow,
            zeroRatedSuppliesRow,
            purchaseExceedSuppliesRow,
            vatAccountsRow,
            optionTaxRowYes,
            optionTaxValueRow,
            captialAssetsRowYes,
            captialAssetsValueRow,
            stocksRowYes,
            stocksValueRow,
            newInvoicesRow,
            outstandingInvoicesRow,
            chooseDeregDateRowYes,
            deregDateRow
          )
      }
    }

    "the dereg reason is ExemptOnly" should {

      "return a sequence containing the answers relevant to the exempt only journey" in {
        cyaModel.copy(
          deregistrationReason = Some(ExemptOnly),
          ceasedTradingDate = None
        ).seqAnswers shouldBe
          Seq(
            deregReasonExemptOnlyRow,
            vatAccountsRow,
            optionTaxRowYes,
            optionTaxValueRow,
            captialAssetsRowYes,
            captialAssetsValueRow,
            stocksRowYes,
            stocksValueRow,
            newInvoicesRow,
            outstandingInvoicesRow,
            chooseDeregDateRowYes,
            deregDateRow
          )
      }
    }

    "there is only one answer to every two part question" should {

      "return a sequence that doesn't include specific rows" in {
        cyaModel.copy(
          optionTax = Some(yesNoAmountNo),
          capitalAssets = Some(yesNoAmountNo),
          stocks = Some(yesNoAmountNo)
        ).seqAnswers shouldBe
          Seq(
            deregReasonCeasedTradingRow,
            ceasedTradingRow,
            vatAccountsRow,
            optionTaxRowNo,
            captialAssetsRowNo,
            stocksRowNo,
            newInvoicesRow,
            outstandingInvoicesRow,
            chooseDeregDateRowYes,
            deregDateRow
          )
      }
    }

    "there is only one answer for the whyTurnover question" should {

      "return a sequence with only one entry for the whyTurnover question" in {
        cyaModel.copy(
          turnover = Some(Yes),
          nextTurnover = Some(nextTaxableTurnoverBelow),
          whyTurnoverBelow = Some(whyTurnoverBelowMin)
        ).seqAnswers shouldBe
          Seq(
            deregReasonCeasedTradingRow,
            ceasedTradingRow,
            taxableTurnoverRow(mockConfig.thresholdString),
            nextTaxableTurnoverRow,
            whyBelowRowMin,
            vatAccountsRow,
            optionTaxRowYes,
            optionTaxValueRow,
            captialAssetsRowYes,
            captialAssetsValueRow,
            stocksRowYes,
            stocksValueRow,
            newInvoicesRow,
            outstandingInvoicesRow,
            chooseDeregDateRowYes,
            deregDateRow
          )
      }
    }

    "the answer to the chooseDeregistrationDate question is 'No'" should{

      "return a sequence containing the chooseDeregistrationDate row with a value of 'No'" in {
        cyaModel.copy(
          chooseDeregDate = Some(No),
          deregDate = None
        ).seqAnswers shouldBe
          Seq(
            deregReasonCeasedTradingRow,
            ceasedTradingRow,
            vatAccountsRow,
            optionTaxRowYes,
            optionTaxValueRow,
            captialAssetsRowYes,
            captialAssetsValueRow,
            stocksRowYes,
            stocksValueRow,
            newInvoicesRow,
            outstandingInvoicesRow,
            chooseDeregDateRowNo
          )
      }
    }

    "there are no answers" should {

      "return an empty sequence" in {
        CheckYourAnswersModel(
          None,None,None,None,None,None,None,None,None,None,None,None,None,None,None,None,None
        ).seqAnswers shouldBe Seq.empty[CheckYourAnswersRowModel]
      }
    }
  }
}
