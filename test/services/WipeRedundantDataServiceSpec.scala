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

package services

import models._
import org.scalamock.scalatest.MockFactory
import services.mocks._
import uk.gov.hmrc.http.HeaderCarrier
import utils.TestUtil

import scala.concurrent.ExecutionContext

class WipeRedundantDataServiceSpec extends TestUtil with MockFactory {

  object TestWipeRedundantDataService extends WipeRedundantDataService(
    MockDeregReasonAnswerService.mockStoredAnswersService,
    MockCeasedTradingDateAnswerService.mockStoredAnswersService,
    MockCapitalAssetsAnswerService.mockStoredAnswersService,
    MockTaxableTurnoverAnswerService.mockStoredAnswersService,
    MockNextTaxableTurnoverAnswerService.mockStoredAnswersService,
    MockIssueNewInvoicesAnswerService.mockStoredAnswersService,
    MockOutstandingInvoicesService.mockStoredAnswersService,
    MockWhyTurnoverBelowAnswerService.mockStoredAnswersService,
    MockDeregDateAnswerService.mockStoredAnswersService,
    mockConfig
  )

  val errorModel: ErrorModel = ErrorModel(1, "")

  "Calling .wipeDeregDate" when {

    "Deregistration reason is Ceased" when {

      val deregReason: Option[DeregistrationReason] = Some(Ceased)

      "Capital assets, Issue new invoices and Outstanding invoices answers are No" should {

        val capitalAssets: Option[YesNoAmountModel] = Some(YesNoAmountModel(No, None))
        val issueInvoices: Option[YesNo] = Some(No)
        val outstandingInvoices: Option[YesNo] = Some(No)

        "Delete deregistration date answer" in {
          MockDeregDateAnswerService.setupMockDeleteAnswer(Right(DeregisterVatSuccess))
          val result = TestWipeRedundantDataService.wipeDeregDate(deregReason, capitalAssets, issueInvoices, outstandingInvoices)
          await(result) shouldBe Right(DeregisterVatSuccess)
        }
      }

      "Capital assets answer is Yes, all others are No" should {

        val capitalAssets: Option[YesNoAmountModel] = Some(YesNoAmountModel(Yes, Some(1)))
        val issueInvoices: Option[YesNo] = Some(No)
        val outstandingInvoices: Option[YesNo] = Some(No)

        "Not delete deregistration date answer" in {
          MockDeregDateAnswerService.setupMockDeleteAnswerNotCalled()
          val result = TestWipeRedundantDataService.wipeDeregDate(deregReason, capitalAssets, issueInvoices, outstandingInvoices)
          await(result) shouldBe Right(DeregisterVatSuccess)
        }
      }

      "Issue new invoices answer is Yes, all others are No" should {

        val capitalAssets: Option[YesNoAmountModel] = Some(YesNoAmountModel(No, None))
        val issueInvoices: Option[YesNo] = Some(Yes)
        val outstandingInvoices: Option[YesNo] = Some(No)

        "Not delete deregistration date answer" in {
          MockDeregDateAnswerService.setupMockDeleteAnswerNotCalled()

          val result = TestWipeRedundantDataService.wipeDeregDate(deregReason, capitalAssets, issueInvoices, outstandingInvoices)
          await(result) shouldBe Right(DeregisterVatSuccess)
        }
      }

      "Outstanding invoices answer is Yes, all others are No" should {

        val capitalAssets: Option[YesNoAmountModel] = Some(YesNoAmountModel(No, None))
        val issueInvoices: Option[YesNo] = Some(No)
        val outstandingInvoices: Option[YesNo] = Some(Yes)

        "Not delete deregistration date answer" in {
          MockDeregDateAnswerService.setupMockDeleteAnswerNotCalled()

          val result = TestWipeRedundantDataService.wipeDeregDate(deregReason, capitalAssets, issueInvoices, outstandingInvoices)
          await(result) shouldBe Right(DeregisterVatSuccess)
        }
      }
    }

    "Deregistration reason is Below Threshold" should {

      val deregReason: Option[DeregistrationReason] = Some(BelowThreshold)
      val capitalAssets: Option[YesNoAmountModel] = Some(YesNoAmountModel(Yes, Some(1)))
      val issueInvoices: Option[YesNo] = Some(Yes)
      val outstandingInvoices: Option[YesNo] = Some(Yes)

      "Not delete deregistration date answer" in {
        MockDeregDateAnswerService.setupMockDeleteAnswerNotCalled()

        val result = TestWipeRedundantDataService.wipeDeregDate(deregReason, capitalAssets, issueInvoices, outstandingInvoices)
        await(result) shouldBe Right(DeregisterVatSuccess)
      }
    }
  }

  "Calling .wipeOutstandingInvoices" when {

    "Issue new invoices answer is Yes" should {

      val issueInvoices: Option[YesNo] = Some(Yes)

      "Delete outstanding invoices answer" in {
        MockOutstandingInvoicesService.setupMockDeleteAnswer(Right(DeregisterVatSuccess))

        val result = TestWipeRedundantDataService.wipeOutstandingInvoices(issueInvoices)
        await(result) shouldBe Right(DeregisterVatSuccess)
      }
    }

    "Issue new invoices answer is No" should {

      val issueInvoices: Option[YesNo] = Some(No)

      "Not delete outstanding invoices answer" in {
        MockOutstandingInvoicesService.setupMockDeleteAnswerNotCalled()

        val result = TestWipeRedundantDataService.wipeOutstandingInvoices(issueInvoices)
        await(result) shouldBe Right(DeregisterVatSuccess)
      }
    }
  }

  "Calling .wipeNext12MonthsBelow" when {

    "Taxable Turnover is <= Dereg Threshold and" when {

      lazy val turnover = Some(TaxableTurnoverModel(mockConfig.deregThreshold))

      "All deletions are successful" should {

        "Delete Next Taxable turnover and Why Turnover Below answers" in {

          inSequence {
            MockWhyTurnoverBelowAnswerService.setupMockDeleteAnswer(Right(DeregisterVatSuccess))
            MockNextTaxableTurnoverAnswerService.setupMockDeleteAnswer(Right(DeregisterVatSuccess))
          }

          val result = TestWipeRedundantDataService.wipeNext12MonthsBelow(turnover)
          await(result) shouldBe Right(DeregisterVatSuccess)
        }
      }


      "First deletion is unsuccessful" should {

        "Not delete Taxable turnover and Next taxable turnover answers" in {

          inSequence {
            MockWhyTurnoverBelowAnswerService.setupMockDeleteAnswer(Left(errorModel))
            MockNextTaxableTurnoverAnswerService.setupMockDeleteAnswerNotCalled()
          }

          val result = TestWipeRedundantDataService.wipeNext12MonthsBelow(turnover)
          await(result) shouldBe Left(errorModel)
        }
      }

      "Second deletion is unsuccessful" should {

        "Not delete Next taxable turnover answer" in {

          inSequence {
            MockWhyTurnoverBelowAnswerService.setupMockDeleteAnswer(Right(DeregisterVatSuccess))
            MockNextTaxableTurnoverAnswerService.setupMockDeleteAnswer(Left(errorModel))
          }

          val result = TestWipeRedundantDataService.wipeNext12MonthsBelow(turnover)
          await(result) shouldBe Left(errorModel)
        }
      }
    }

    "Taxable turnover is > threshold" should {

      lazy val turnover = Some(TaxableTurnoverModel(mockConfig.deregThreshold + 0.01))

      "Perform no deletions" in {
        val result = TestWipeRedundantDataService.wipeNext12MonthsBelow(turnover)
        await(result) shouldBe Right(DeregisterVatSuccess)
      }
    }
  }

  "Calling .wipeBelowThresholdJourney" when {

    "All deletions are successful" should {

      "Delete Turnover below, Taxable turnover and Next taxable turnover answers" in {

        inSequence {
          MockWhyTurnoverBelowAnswerService.setupMockDeleteAnswer(Right(DeregisterVatSuccess))
          MockTaxableTurnoverAnswerService.setupMockDeleteAnswer(Right(DeregisterVatSuccess))
          MockNextTaxableTurnoverAnswerService.setupMockDeleteAnswer(Right(DeregisterVatSuccess))
        }

        val result = TestWipeRedundantDataService.wipeBelowThresholdJourney
        await(result) shouldBe Right(DeregisterVatSuccess)
      }
    }


    "First deletion is unsuccessful" should {

      "Not delete Taxable turnover and Next taxable turnover answers" in {

        inSequence {
          MockWhyTurnoverBelowAnswerService.setupMockDeleteAnswer(Left(errorModel))
          MockTaxableTurnoverAnswerService.setupMockDeleteAnswerNotCalled()
          MockNextTaxableTurnoverAnswerService.setupMockDeleteAnswerNotCalled()
        }

        val result = TestWipeRedundantDataService.wipeBelowThresholdJourney
        await(result) shouldBe Left(errorModel)
      }
    }

    "Second deletion is unsuccessful" should {

      "Not delete Next taxable turnover answer" in {

        inSequence {
          MockWhyTurnoverBelowAnswerService.setupMockDeleteAnswer(Right(DeregisterVatSuccess))
          MockTaxableTurnoverAnswerService.setupMockDeleteAnswer(Left(errorModel))
          MockNextTaxableTurnoverAnswerService.setupMockDeleteAnswerNotCalled()
        }

        val result = TestWipeRedundantDataService.wipeBelowThresholdJourney
        await(result) shouldBe Left(errorModel)
      }
    }

    "Third deletion is unsuccessful" should {

      "Return an error model" in {

        inSequence {
          MockWhyTurnoverBelowAnswerService.setupMockDeleteAnswer(Right(DeregisterVatSuccess))
          MockTaxableTurnoverAnswerService.setupMockDeleteAnswer(Right(DeregisterVatSuccess))
          MockNextTaxableTurnoverAnswerService.setupMockDeleteAnswer(Left(errorModel))
        }

        val result = TestWipeRedundantDataService.wipeBelowThresholdJourney
        await(result) shouldBe Left(errorModel)
      }
    }
  }

  "Calling .wipeRedundantDeregReasonJourneyData" when {

    "Deregistration reason is Ceased" should {

      val deregReason: Option[DeregistrationReason] = Some(Ceased)

      "Call .wipeBelowThresholdJourney to remove redundant data" in {

        inSequence {
          MockWhyTurnoverBelowAnswerService.setupMockDeleteAnswer(Right(DeregisterVatSuccess))
          MockTaxableTurnoverAnswerService.setupMockDeleteAnswer(Right(DeregisterVatSuccess))
          MockNextTaxableTurnoverAnswerService.setupMockDeleteAnswer(Right(DeregisterVatSuccess))
        }

        val result = TestWipeRedundantDataService.wipeRedundantDeregReasonJourneyData(deregReason)
        await(result) shouldBe Right(DeregisterVatSuccess)
      }
    }

    "Deregistration reason is Below Threshold" should {

      val deregReason: Option[DeregistrationReason] = Some(BelowThreshold)

      "Delete Ceased Trading date answer" in {

        MockCeasedTradingDateAnswerService.setupMockDeleteAnswer(Right(DeregisterVatSuccess))

        val result = TestWipeRedundantDataService.wipeRedundantDeregReasonJourneyData(deregReason)
        await(result) shouldBe Right(DeregisterVatSuccess)
      }
    }

    "Deregistration reason is not supplied" should {

      "Perform no deletions" in {
        val result = TestWipeRedundantDataService.wipeRedundantDeregReasonJourneyData(None)
        await(result) shouldBe Right(DeregisterVatSuccess)
      }
    }
  }

  "Calling .wipeRedundantData" when {

    "all data retrievals are successful" should {

      "all data deletions are successful" should {

        "make calls to wipe relevant data" in {

          inSequence {
            MockDeregReasonAnswerService.setupMockGetAnswers(Right(Some(BelowThreshold)))
            MockCapitalAssetsAnswerService.setupMockGetAnswers(Right(Some(YesNoAmountModel(No, None))))
            MockIssueNewInvoicesAnswerService.setupMockGetAnswers(Right(Some(No)))
            MockOutstandingInvoicesService.setupMockGetAnswers(Right(Some(No)))
            MockTaxableTurnoverAnswerService.setupMockGetAnswers(Right(Some(TaxableTurnoverModel(1))))
            MockCeasedTradingDateAnswerService.setupMockDeleteAnswer(Right(DeregisterVatSuccess))
            MockOutstandingInvoicesService.setupMockDeleteAnswer(Right(DeregisterVatSuccess))
            MockWhyTurnoverBelowAnswerService.setupMockDeleteAnswer(Right(DeregisterVatSuccess))
            MockNextTaxableTurnoverAnswerService.setupMockDeleteAnswer(Right(DeregisterVatSuccess))
          }

          val result = TestWipeRedundantDataService.wipeRedundantData
          await(result) shouldBe Right(DeregisterVatSuccess)
        }
      }

      "a data deletion is unsuccessful" should {

        "return an error model" in {

          inSequence {
            MockDeregReasonAnswerService.setupMockGetAnswers(Right(Some(BelowThreshold)))
            MockCapitalAssetsAnswerService.setupMockGetAnswers(Right(Some(YesNoAmountModel(No, None))))
            MockIssueNewInvoicesAnswerService.setupMockGetAnswers(Right(Some(No)))
            MockOutstandingInvoicesService.setupMockGetAnswers(Right(Some(No)))
            MockTaxableTurnoverAnswerService.setupMockGetAnswers(Right(Some(TaxableTurnoverModel(1))))
            MockCeasedTradingDateAnswerService.setupMockDeleteAnswer(Left(errorModel))
          }

          val result = TestWipeRedundantDataService.wipeRedundantData
          await(result) shouldBe Left(errorModel)
        }
      }
    }

    "a data retrieval is unsuccessful" should {

      "return an error model" in {

        inSequence {
          MockDeregReasonAnswerService.setupMockGetAnswers(Right(Some(BelowThreshold)))
          MockCapitalAssetsAnswerService.setupMockGetAnswers(Right(Some(YesNoAmountModel(No, None))))
          MockIssueNewInvoicesAnswerService.setupMockGetAnswers(Left(errorModel))
        }

        val result = TestWipeRedundantDataService.wipeRedundantData
        await(result) shouldBe Left(errorModel)
      }
    }
  }
}
