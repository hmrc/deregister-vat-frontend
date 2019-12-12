/*
 * Copyright 2019 HM Revenue & Customs
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
import utils.TestUtil

class WipeRedundantDataServiceSpec extends TestUtil with MockFactory with MockDeregReasonAnswerService
  with MockCeasedTradingDateAnswerService with MockCapitalAssetsAnswerService with MockTaxableTurnoverAnswerService
  with MockIssueNewInvoicesAnswerService with MockOutstandingInvoicesService with MockWhyTurnoverBelowAnswerService
  with MockDeregDateAnswerService with MockNextTaxableTurnoverAnswerService with MockBusinessActivityAnswerService
  with MockZeroRatedSuppliesValueService with MockPurchasesExceedSuppliesAnswerService with MockSicCodeAnswerService {

  object TestWipeRedundantDataService extends WipeRedundantDataService(
    mockDeregReasonAnswerService,
    mockCeasedTradingDateAnswerService,
    mockCapitalAssetsAnswerService,
    mockTaxableTurnoverAnswerService,
    mockNextTaxableTurnoverAnswerService,
    mockIssueNewInvoicesAnswerService,
    mockOutstandingInvoicesService,
    mockWhyTurnoverBelowAnswerService,
    mockDeregDateAnswerService,
    mockBusinessActivityAnswerService,
    mockZeroRatedSuppliesValueService,
    mockPurchasesExceedSuppliesAnswerService,
    mockSicCodeAnswerService
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
          setupMockDeleteDeregDate(Right(DeregisterVatSuccess))
          val result = TestWipeRedundantDataService.wipeDeregDate(deregReason, capitalAssets, issueInvoices, outstandingInvoices)
          await(result) shouldBe Right(DeregisterVatSuccess)
        }
      }

      "Capital assets answer is Yes, all others are No" should {

        val capitalAssets: Option[YesNoAmountModel] = Some(YesNoAmountModel(Yes, Some(1)))
        val issueInvoices: Option[YesNo] = Some(No)
        val outstandingInvoices: Option[YesNo] = Some(No)

        "Not delete deregistration date answer" in {
          setupMockDeleteDeregDateNotCalled()
          val result = TestWipeRedundantDataService.wipeDeregDate(deregReason, capitalAssets, issueInvoices, outstandingInvoices)
          await(result) shouldBe Right(DeregisterVatSuccess)
        }
      }

      "Issue new invoices answer is Yes, all others are No" should {

        val capitalAssets: Option[YesNoAmountModel] = Some(YesNoAmountModel(No, None))
        val issueInvoices: Option[YesNo] = Some(Yes)
        val outstandingInvoices: Option[YesNo] = Some(No)

        "Not delete deregistration date answer" in {
          setupMockDeleteDeregDateNotCalled()

          val result = TestWipeRedundantDataService.wipeDeregDate(deregReason, capitalAssets, issueInvoices, outstandingInvoices)
          await(result) shouldBe Right(DeregisterVatSuccess)
        }
      }

      "Outstanding invoices answer is Yes, all others are No" should {

        val capitalAssets: Option[YesNoAmountModel] = Some(YesNoAmountModel(No, None))
        val issueInvoices: Option[YesNo] = Some(No)
        val outstandingInvoices: Option[YesNo] = Some(Yes)

        "Not delete deregistration date answer" in {
          setupMockDeleteDeregDateNotCalled()

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
        setupMockDeleteDeregDateNotCalled()

        val result = TestWipeRedundantDataService.wipeDeregDate(deregReason, capitalAssets, issueInvoices, outstandingInvoices)
        await(result) shouldBe Right(DeregisterVatSuccess)
      }
    }
  }

  "Calling .wipeOutstandingInvoices" when {

    "Issue new invoices answer is Yes" should {

      val issueInvoices: Option[YesNo] = Some(Yes)

      "Delete outstanding invoices answer" in {
        setupMockDeleteOutstandingInvoices(Right(DeregisterVatSuccess))

        val result = TestWipeRedundantDataService.wipeOutstandingInvoices(issueInvoices)
        await(result) shouldBe Right(DeregisterVatSuccess)
      }
    }

    "Issue new invoices answer is No" should {

      val issueInvoices: Option[YesNo] = Some(No)

      "Not delete outstanding invoices answer" in {
        setupMockDeleteOutstandingInvoicesNotCalled()

        val result = TestWipeRedundantDataService.wipeOutstandingInvoices(issueInvoices)
        await(result) shouldBe Right(DeregisterVatSuccess)
      }
    }
  }

  "Calling .wipeDataReadyForCeasedTradingJourney" when {

    "All deletions are successful" should {

      "Delete Data to start Ceased Trading Journey" in {

        inSequence {
          setupMockDeleteTaxableTurnover(Right(DeregisterVatSuccess))

          setupMockDeleteWhyTurnoverBelow(Right(DeregisterVatSuccess))
          setupMockDeleteNextTaxableTurnover(Right(DeregisterVatSuccess))

          setupMockDeleteBusinessActivityAnswer(Right(DeregisterVatSuccess))
          setupMockDeleteSicCodeAnswerService(Right(DeregisterVatSuccess))
          setupMockDeleteZeroRatedSuppliesValueAnswer(Right(DeregisterVatSuccess))
          setupMockDeletePurchasesExceedSuppliesAnswer(Right(DeregisterVatSuccess))
        }

        val result = TestWipeRedundantDataService.wipeDataReadyForCeasedTradingJourney
        await(result) shouldBe Right(DeregisterVatSuccess)

      }

    }

    "First deletion is unsuccessful" should {

      "Return an error model" in {

        inSequence {
          setupMockDeleteTaxableTurnover(Left(errorModel))

          setupMockDeleteWhyTurnoverBelowNotCalled()
          setupMockDeleteNextTaxableTurnoverNotCalled()

          setupMockDeleteBusinessActivityAnswerNotCalled()
          setupMockDeleteSicCodeAnswerServiceNotCalled()
          setupMockDeleteZeroRatedSuppliesValueAnswerNotCalled()
          setupMockDeletePurchasesExceedSuppliesAnswerNotCalled()
        }

        val result = TestWipeRedundantDataService.wipeDataReadyForCeasedTradingJourney
        await(result) shouldBe Left(errorModel)
      }
    }

    "Second deletion is unsuccessful" should {

      "Return an error model" in {

        inSequence {
          setupMockDeleteTaxableTurnover(Right(DeregisterVatSuccess))

          setupMockDeleteWhyTurnoverBelow(Left(errorModel))
          setupMockDeleteNextTaxableTurnoverNotCalled()

          setupMockDeleteBusinessActivityAnswerNotCalled()
          setupMockDeleteSicCodeAnswerServiceNotCalled()
          setupMockDeleteZeroRatedSuppliesValueAnswerNotCalled()
          setupMockDeletePurchasesExceedSuppliesAnswerNotCalled()
        }

        val result = TestWipeRedundantDataService.wipeDataReadyForCeasedTradingJourney
        await(result) shouldBe Left(errorModel)
      }
    }
    "Third deletion is unsuccessful" should {

      "Return an error model" in {

        inSequence {
          setupMockDeleteTaxableTurnover(Right(DeregisterVatSuccess))

          setupMockDeleteWhyTurnoverBelow(Right(DeregisterVatSuccess))
          setupMockDeleteNextTaxableTurnover(Left(errorModel))

          setupMockDeleteBusinessActivityAnswerNotCalled()
          setupMockDeleteSicCodeAnswerServiceNotCalled()
          setupMockDeleteZeroRatedSuppliesValueAnswerNotCalled()
          setupMockDeletePurchasesExceedSuppliesAnswerNotCalled()
        }

        val result = TestWipeRedundantDataService.wipeDataReadyForCeasedTradingJourney
        await(result) shouldBe Left(errorModel)
      }
    }
    "Fourth deletion is unsuccessful" should {

      "Return an error model" in {

        inSequence {
          setupMockDeleteTaxableTurnover(Right(DeregisterVatSuccess))

          setupMockDeleteWhyTurnoverBelow(Right(DeregisterVatSuccess))
          setupMockDeleteNextTaxableTurnover(Right(DeregisterVatSuccess))

          setupMockDeleteBusinessActivityAnswer(Left(errorModel))
          setupMockDeleteSicCodeAnswerServiceNotCalled()
          setupMockDeleteZeroRatedSuppliesValueAnswerNotCalled()
          setupMockDeletePurchasesExceedSuppliesAnswerNotCalled()
        }

        val result = TestWipeRedundantDataService.wipeDataReadyForCeasedTradingJourney
        await(result) shouldBe Left(errorModel)
      }
    }
    "Fifth deletion is unsuccessful" should {

      "Return an error model" in {

        inSequence {
          setupMockDeleteTaxableTurnover(Right(DeregisterVatSuccess))

          setupMockDeleteWhyTurnoverBelow(Right(DeregisterVatSuccess))
          setupMockDeleteNextTaxableTurnover(Right(DeregisterVatSuccess))

          setupMockDeleteBusinessActivityAnswer(Right(DeregisterVatSuccess))
          setupMockDeleteSicCodeAnswerService(Left(errorModel))
          setupMockDeleteZeroRatedSuppliesValueAnswerNotCalled()
          setupMockDeletePurchasesExceedSuppliesAnswerNotCalled()
        }

        val result = TestWipeRedundantDataService.wipeDataReadyForCeasedTradingJourney
        await(result) shouldBe Left(errorModel)
      }
    }
    "Sixth deletion is unsuccessful" should {

      "Return an error model" in {

        inSequence {
          setupMockDeleteTaxableTurnover(Right(DeregisterVatSuccess))

          setupMockDeleteWhyTurnoverBelow(Right(DeregisterVatSuccess))
          setupMockDeleteNextTaxableTurnover(Right(DeregisterVatSuccess))

          setupMockDeleteBusinessActivityAnswer(Right(DeregisterVatSuccess))
          setupMockDeleteSicCodeAnswerService(Right(DeregisterVatSuccess))
          setupMockDeleteZeroRatedSuppliesValueAnswer(Left(errorModel))
          setupMockDeletePurchasesExceedSuppliesAnswerNotCalled()
        }

        val result = TestWipeRedundantDataService.wipeDataReadyForCeasedTradingJourney
        await(result) shouldBe Left(errorModel)
      }
    }
    "Seventh deletion is unsuccessful" should {

      "Return an error model" in {

        inSequence {
          setupMockDeleteTaxableTurnover(Right(DeregisterVatSuccess))

          setupMockDeleteWhyTurnoverBelow(Right(DeregisterVatSuccess))
          setupMockDeleteNextTaxableTurnover(Right(DeregisterVatSuccess))

          setupMockDeleteBusinessActivityAnswer(Right(DeregisterVatSuccess))
          setupMockDeleteSicCodeAnswerService(Right(DeregisterVatSuccess))
          setupMockDeleteZeroRatedSuppliesValueAnswer(Right(DeregisterVatSuccess))
          setupMockDeletePurchasesExceedSuppliesAnswer(Left(errorModel))
        }

        val result = TestWipeRedundantDataService.wipeDataReadyForCeasedTradingJourney
        await(result) shouldBe Left(errorModel)
      }
    }
  }

  "Calling .wipeDataReadyForBelowThresholdJourney" when {

    "All deletions are successful" should {

      "Delete Data to start Below Threshold Journey" in {

        inSequence {
          setupMockDeleteCeasedTradingDate(Right(DeregisterVatSuccess))

          setupMockDeleteBusinessActivityAnswer(Right(DeregisterVatSuccess))
          setupMockDeleteSicCodeAnswerService(Right(DeregisterVatSuccess))
          setupMockDeleteZeroRatedSuppliesValueAnswer(Right(DeregisterVatSuccess))
          setupMockDeletePurchasesExceedSuppliesAnswer(Right(DeregisterVatSuccess))
        }

        val result = TestWipeRedundantDataService.wipeDataReadyForBelowThresholdJourney
        await(result) shouldBe Right(DeregisterVatSuccess)
      }
    }


    "First deletion is unsuccessful" should {

      "Return an error model" in {

        inSequence {
          setupMockDeleteCeasedTradingDate(Left(errorModel))

          setupMockDeleteBusinessActivityAnswerNotCalled()
          setupMockDeleteSicCodeAnswerServiceNotCalled()
          setupMockDeleteZeroRatedSuppliesValueAnswerNotCalled()
          setupMockDeletePurchasesExceedSuppliesAnswerNotCalled()
        }

        val result = TestWipeRedundantDataService.wipeDataReadyForBelowThresholdJourney
        await(result) shouldBe Left(errorModel)
      }
    }

    "Second deletion is unsuccessful" should {

      "Return an error model" in {

        inSequence {
          setupMockDeleteCeasedTradingDate(Right(DeregisterVatSuccess))

          setupMockDeleteBusinessActivityAnswer(Left(errorModel))
          setupMockDeleteSicCodeAnswerServiceNotCalled()
          setupMockDeleteZeroRatedSuppliesValueAnswerNotCalled()
          setupMockDeletePurchasesExceedSuppliesAnswerNotCalled()
        }

        val result = TestWipeRedundantDataService.wipeDataReadyForBelowThresholdJourney
        await(result) shouldBe Left(errorModel)
      }
    }

    "Third deletion is unsuccessful" should {

      "Return an error model" in {

        inSequence {
          setupMockDeleteCeasedTradingDate(Right(DeregisterVatSuccess))

          setupMockDeleteBusinessActivityAnswer(Right(DeregisterVatSuccess))
          setupMockDeleteSicCodeAnswerService(Left(errorModel))
          setupMockDeleteZeroRatedSuppliesValueAnswerNotCalled()
          setupMockDeletePurchasesExceedSuppliesAnswerNotCalled()
        }

        val result = TestWipeRedundantDataService.wipeDataReadyForBelowThresholdJourney
        await(result) shouldBe Left(errorModel)
      }
    }

    "Fourth deletion is unsuccessful" should {

      "Return an error model" in {

        inSequence {
          setupMockDeleteCeasedTradingDate(Right(DeregisterVatSuccess))

          setupMockDeleteBusinessActivityAnswer(Right(DeregisterVatSuccess))
          setupMockDeleteSicCodeAnswerService(Right(DeregisterVatSuccess))
          setupMockDeleteZeroRatedSuppliesValueAnswer(Left(errorModel))
          setupMockDeletePurchasesExceedSuppliesAnswerNotCalled()
        }

        val result = TestWipeRedundantDataService.wipeDataReadyForBelowThresholdJourney
        await(result) shouldBe Left(errorModel)
      }
    }

    "Fifth deletion is unsuccessful" should {

      "Return an error model" in {

        inSequence {
          setupMockDeleteCeasedTradingDate(Right(DeregisterVatSuccess))

          setupMockDeleteBusinessActivityAnswer(Right(DeregisterVatSuccess))
          setupMockDeleteSicCodeAnswerService(Right(DeregisterVatSuccess))
          setupMockDeleteZeroRatedSuppliesValueAnswer(Right(DeregisterVatSuccess))
          setupMockDeletePurchasesExceedSuppliesAnswer(Left(errorModel))
        }

        val result = TestWipeRedundantDataService.wipeDataReadyForBelowThresholdJourney
        await(result) shouldBe Left(errorModel)
      }
    }
  }

  "Calling .wipeDataReadyForZeroRatedJourney" when {

    "All deletions are successful" should {
      "Delete Data ready for Zero Rated Journey" in {

        inSequence {
          setupMockDeleteCeasedTradingDate(Right(DeregisterVatSuccess))

          setupMockDeleteWhyTurnoverBelow(Right(DeregisterVatSuccess))
          setupMockDeleteTaxableTurnover(Right(DeregisterVatSuccess))
        }
        val result = TestWipeRedundantDataService.wipeDataReadyForZeroRatedJourney
        await(result) shouldBe Right(DeregisterVatSuccess)
      }
    }

    "First deletion is unsuccessful" should {
      "Not first answer" in {

        inSequence {
          setupMockDeleteCeasedTradingDate(Left(errorModel))

          setupMockDeleteWhyTurnoverBelowNotCalled()
          setupMockDeleteTaxableTurnoverNotCalled()
        }
        val result = TestWipeRedundantDataService.wipeDataReadyForZeroRatedJourney
        await(result) shouldBe Left(errorModel)
      }
    }

    "Second deletion is unsuccessful" should {
      "Not second answer" in {

        inSequence {
          setupMockDeleteCeasedTradingDate(Right(DeregisterVatSuccess))

          setupMockDeleteWhyTurnoverBelow(Left(errorModel))
          setupMockDeleteTaxableTurnoverNotCalled()
        }
        val result = TestWipeRedundantDataService.wipeDataReadyForZeroRatedJourney
        await(result) shouldBe Left(errorModel)
      }
    }


    "Third deletion is unsuccessful" should {
      "Not third answer" in {

        inSequence {
          setupMockDeleteCeasedTradingDate(Right(DeregisterVatSuccess))

          setupMockDeleteWhyTurnoverBelow(Right(DeregisterVatSuccess))
          setupMockDeleteTaxableTurnover(Left(errorModel))
        }
        val result = TestWipeRedundantDataService.wipeDataReadyForZeroRatedJourney
        await(result) shouldBe Left(errorModel)
      }
    }

  }


  "Calling .wipeRedundantDeregReasonJourneyData" when {

    "Deregistration reason is Ceased" should {

      val deregReason: Option[DeregistrationReason] = Some(Ceased)

      "Call .wipe to remove redundant data" in {

        inSequence {
          setupMockDeleteTaxableTurnover(Right(DeregisterVatSuccess))

          setupMockDeleteWhyTurnoverBelow(Right(DeregisterVatSuccess))
          setupMockDeleteNextTaxableTurnover(Right(DeregisterVatSuccess))

          setupMockDeleteBusinessActivityAnswer(Right(DeregisterVatSuccess))
          setupMockDeleteSicCodeAnswerService(Right(DeregisterVatSuccess))
          setupMockDeleteZeroRatedSuppliesValueAnswer(Right(DeregisterVatSuccess))
          setupMockDeletePurchasesExceedSuppliesAnswer(Right(DeregisterVatSuccess))
        }

        val result = TestWipeRedundantDataService.wipeRedundantDeregReasonJourneyData(deregReason)
        await(result) shouldBe Right(DeregisterVatSuccess)
      }
    }

    "Deregistration reason is Below Threshold" should {

      val deregReason: Option[DeregistrationReason] = Some(BelowThreshold)

      "Delete Ceased Trading date answer" in {

        inSequence {
          setupMockDeleteCeasedTradingDate(Right(DeregisterVatSuccess))

          setupMockDeleteBusinessActivityAnswer(Right(DeregisterVatSuccess))
          setupMockDeleteSicCodeAnswerService(Right(DeregisterVatSuccess))
          setupMockDeleteZeroRatedSuppliesValueAnswer(Right(DeregisterVatSuccess))
          setupMockDeletePurchasesExceedSuppliesAnswer(Right(DeregisterVatSuccess))
        }

        val result = TestWipeRedundantDataService.wipeRedundantDeregReasonJourneyData(deregReason)
        await(result) shouldBe Right(DeregisterVatSuccess)
      }
    }

    "Deregistration reason is Zero Rated" should {

      val deregReason: Option[DeregistrationReason] = Some(ZeroRated)

      "Delete Ceased Trading date, Why Turnover Below and Next Taxable Turnover answers" in {
        inSequence {
          setupMockDeleteCeasedTradingDate(Right(DeregisterVatSuccess))

          setupMockDeleteWhyTurnoverBelow(Right(DeregisterVatSuccess))
          setupMockDeleteTaxableTurnover(Right(DeregisterVatSuccess))
        }

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

        "Dereg Reason is Ceased" should {

          "make calls to wipe relevant data" in {

            inSequence {
              setupMockGetDeregReason(Right(Some(Ceased)))
              setupMockGetCapitalAssets(Right(Some(YesNoAmountModel(No, None))))
              setupMockGetIssueNewInvoices(Right(Some(No)))
              setupMockGetOutstandingInvoices(Right(Some(No)))

              setupMockDeleteTaxableTurnover(Right(DeregisterVatSuccess))
              setupMockDeleteWhyTurnoverBelow(Right(DeregisterVatSuccess))
              setupMockDeleteNextTaxableTurnover(Right(DeregisterVatSuccess))
              setupMockDeleteBusinessActivityAnswer(Right(DeregisterVatSuccess))
              setupMockDeleteSicCodeAnswerService(Right(DeregisterVatSuccess))
              setupMockDeleteZeroRatedSuppliesValueAnswer(Right(DeregisterVatSuccess))
              setupMockDeletePurchasesExceedSuppliesAnswer(Right(DeregisterVatSuccess))
              setupMockDeleteDeregDate(Right(DeregisterVatSuccess))
            }

            val result = TestWipeRedundantDataService.wipeRedundantData
            await(result) shouldBe Right(DeregisterVatSuccess)
          }
        }

        "Dereg Reason is Below Threshold" should {

          "make calls to wipe relevant data" in {

            inSequence {
              setupMockGetDeregReason(Right(Some(BelowThreshold)))
              setupMockGetCapitalAssets(Right(Some(YesNoAmountModel(No, None))))
              setupMockGetIssueNewInvoices(Right(Some(No)))
              setupMockGetOutstandingInvoices(Right(Some(No)))

              setupMockDeleteCeasedTradingDate(Right(DeregisterVatSuccess))
              setupMockDeleteBusinessActivityAnswer(Right(DeregisterVatSuccess))
              setupMockDeleteSicCodeAnswerService(Right(DeregisterVatSuccess))
              setupMockDeleteZeroRatedSuppliesValueAnswer(Right(DeregisterVatSuccess))
              setupMockDeletePurchasesExceedSuppliesAnswer(Right(DeregisterVatSuccess))
            }

            val result = TestWipeRedundantDataService.wipeRedundantData
            await(result) shouldBe Right(DeregisterVatSuccess)
          }

        }

        "Dereg Reason is Zero Rated" should {

          "make calls to wipe relevant data" in {

            inSequence {
              setupMockGetDeregReason(Right(Some(ZeroRated)))
              setupMockGetCapitalAssets(Right(Some(YesNoAmountModel(No, None))))
              setupMockGetIssueNewInvoices(Right(Some(No)))
              setupMockGetOutstandingInvoices(Right(Some(No)))

              setupMockDeleteCeasedTradingDate(Right(DeregisterVatSuccess))
              setupMockDeleteWhyTurnoverBelow(Right(DeregisterVatSuccess))
              setupMockDeleteTaxableTurnover(Right(DeregisterVatSuccess))
            }

            val result = TestWipeRedundantDataService.wipeRedundantData
            await(result) shouldBe Right(DeregisterVatSuccess)
          }

        }
      }
        "a data deletion for Outstanding Invoices is unsuccessful" should {

          "return an error model" in {

            inSequence {
              setupMockGetDeregReason(Right(Some(ZeroRated)))
              setupMockGetCapitalAssets(Right(Some(YesNoAmountModel(No, None))))
              setupMockGetIssueNewInvoices(Right(Some(Yes)))
              setupMockGetOutstandingInvoices(Right(Some(No)))

              setupMockDeleteCeasedTradingDate(Right(DeregisterVatSuccess))
              setupMockDeleteWhyTurnoverBelow(Right(DeregisterVatSuccess))
              setupMockDeleteTaxableTurnover(Right(DeregisterVatSuccess))

              setupMockDeleteOutstandingInvoices(Left(errorModel))
            }

            val result = TestWipeRedundantDataService.wipeRedundantData
            await(result) shouldBe Left(errorModel)
          }
        }
      "a data deletion for Dereg Date is unsuccessful" should {

        "return an error model" in {

          inSequence {
            setupMockGetDeregReason(Right(Some(Ceased)))
            setupMockGetCapitalAssets(Right(Some(YesNoAmountModel(No, None))))
            setupMockGetIssueNewInvoices(Right(Some(No)))
            setupMockGetOutstandingInvoices(Right(Some(No)))

            setupMockDeleteTaxableTurnover(Right(DeregisterVatSuccess))
            setupMockDeleteWhyTurnoverBelow(Right(DeregisterVatSuccess))
            setupMockDeleteNextTaxableTurnover(Right(DeregisterVatSuccess))
            setupMockDeleteBusinessActivityAnswer(Right(DeregisterVatSuccess))
            setupMockDeleteSicCodeAnswerService(Right(DeregisterVatSuccess))
            setupMockDeleteZeroRatedSuppliesValueAnswer(Right(DeregisterVatSuccess))
            setupMockDeletePurchasesExceedSuppliesAnswer(Right(DeregisterVatSuccess))

            setupMockDeleteDeregDate(Left(errorModel))
          }

          val result = TestWipeRedundantDataService.wipeRedundantData
          await(result) shouldBe Left(errorModel)
        }
      }

    }

    "a data retrieval is unsuccessful" should {
      "reason is Dereg Reason" should {
        "return an error model" in {

          inSequence {
            setupMockGetDeregReason(Left(errorModel))
          }

          val result = TestWipeRedundantDataService.wipeRedundantData
          await(result) shouldBe Left(errorModel)
        }
      }
      "reason is Capital Assets" should {
        "return an error model" in {

          inSequence {
            setupMockGetDeregReason(Right(Some(Ceased)))
            setupMockGetCapitalAssets(Left(errorModel))
          }

          val result = TestWipeRedundantDataService.wipeRedundantData
          await(result) shouldBe Left(errorModel)
        }
      }
      "reason is Issue Invoices" should {
        "return an error model" in {

          inSequence {
            setupMockGetDeregReason(Right(Some(Ceased)))
            setupMockGetCapitalAssets(Right(Some(YesNoAmountModel(No, None))))
            setupMockGetIssueNewInvoices(Left(errorModel))
          }

          val result = TestWipeRedundantDataService.wipeRedundantData
          await(result) shouldBe Left(errorModel)
        }
      }
      "reason is Outstanding Invoices" should {
        "return an error model" in {

          inSequence {
            setupMockGetDeregReason(Right(Some(Ceased)))
            setupMockGetCapitalAssets(Right(Some(YesNoAmountModel(No, None))))
            setupMockGetIssueNewInvoices(Right(Some(No)))
            setupMockGetOutstandingInvoices(Left(errorModel))
          }

          val result = TestWipeRedundantDataService.wipeRedundantData
          await(result) shouldBe Left(errorModel)
        }
      }
    }

  }
}
