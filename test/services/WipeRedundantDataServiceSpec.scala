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

package services

import models._
import org.scalamock.scalatest.MockFactory
import org.scalatest.concurrent.ScalaFutures.convertScalaFuture
import services.mocks._
import utils.TestUtil

import scala.concurrent.Future

class WipeRedundantDataServiceSpec extends TestUtil with MockFactory with MockDeregReasonAnswerService
  with MockCeasedTradingDateAnswerService with MockCapitalAssetsAnswerService with MockTaxableTurnoverAnswerService
  with MockIssueNewInvoicesAnswerService with MockOutstandingInvoicesService with MockWhyTurnoverBelowAnswerService
  with MockChooseDeregDateAnswerService with MockDeregDateAnswerService with MockNextTaxableTurnoverAnswerService
  with MockBusinessActivityAnswerService with MockZeroRatedSuppliesValueService with MockPurchasesExceedSuppliesAnswerService
  with MockSicCodeAnswerService {

  object TestWipeRedundantDataService extends WipeRedundantDataService(
    mockDeregReasonAnswerService,
    mockCeasedTradingDateAnswerService,
    mockCapitalAssetsAnswerService,
    mockTaxableTurnoverAnswerService,
    mockNextTaxableTurnoverAnswerService,
    mockIssueNewInvoicesAnswerService,
    mockOutstandingInvoicesService,
    mockWhyTurnoverBelowAnswerService,
    mockChooseDeregDateAnswerService,
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
          setupMockDeleteChooseDeregDate(Future.successful(Right(DeregisterVatSuccess)))
          setupMockDeleteDeregDate(Future.successful(Right(DeregisterVatSuccess)))
          val result = TestWipeRedundantDataService.wipeDeregDate(deregReason, capitalAssets, issueInvoices, outstandingInvoices)
          result.futureValue shouldBe Right(DeregisterVatSuccess)
        }
      }

      "Capital assets answer is Yes, all others are No" should {

        val capitalAssets: Option[YesNoAmountModel] = Some(YesNoAmountModel(Yes, Some(1)))
        val issueInvoices: Option[YesNo] = Some(No)
        val outstandingInvoices: Option[YesNo] = Some(No)

        "Not delete deregistration date answer" in {
          setupMockDeleteChooseDeregDateNotCalled()
          val result = TestWipeRedundantDataService.wipeDeregDate(deregReason, capitalAssets, issueInvoices, outstandingInvoices)
          result.futureValue shouldBe Right(DeregisterVatSuccess)
        }
      }

      "Issue new invoices answer is Yes, all others are No" should {

        val capitalAssets: Option[YesNoAmountModel] = Some(YesNoAmountModel(No, None))
        val issueInvoices: Option[YesNo] = Some(Yes)
        val outstandingInvoices: Option[YesNo] = Some(No)

        "Not delete deregistration date answer" in {
          setupMockDeleteChooseDeregDateNotCalled()

          val result = TestWipeRedundantDataService.wipeDeregDate(deregReason, capitalAssets, issueInvoices, outstandingInvoices)
          result.futureValue shouldBe Right(DeregisterVatSuccess)
        }
      }

      "Outstanding invoices answer is Yes, all others are No" should {

        val capitalAssets: Option[YesNoAmountModel] = Some(YesNoAmountModel(No, None))
        val issueInvoices: Option[YesNo] = Some(No)
        val outstandingInvoices: Option[YesNo] = Some(Yes)

        "Not delete deregistration date answer" in {
          setupMockDeleteChooseDeregDateNotCalled()

          val result = TestWipeRedundantDataService.wipeDeregDate(deregReason, capitalAssets, issueInvoices, outstandingInvoices)
          result.futureValue shouldBe Right(DeregisterVatSuccess)
        }
      }
    }

    "Deregistration reason is Below Threshold" should {

      val deregReason: Option[DeregistrationReason] = Some(BelowThreshold)
      val capitalAssets: Option[YesNoAmountModel] = Some(YesNoAmountModel(Yes, Some(1)))
      val issueInvoices: Option[YesNo] = Some(Yes)
      val outstandingInvoices: Option[YesNo] = Some(Yes)

      "Not delete deregistration date answer" in {
        setupMockDeleteChooseDeregDateNotCalled()

        val result = TestWipeRedundantDataService.wipeDeregDate(deregReason, capitalAssets, issueInvoices, outstandingInvoices)
        result.futureValue shouldBe Right(DeregisterVatSuccess)
      }
    }
  }

  "Calling .wipeOutstandingInvoices" when {

    "Issue new invoices answer is Yes" should {

      val issueInvoices: Option[YesNo] = Some(Yes)

      "Delete outstanding invoices answer" in {
        setupMockDeleteOutstandingInvoices(Future.successful(Right(DeregisterVatSuccess)))

        val result = TestWipeRedundantDataService.wipeOutstandingInvoices(issueInvoices)
        result.futureValue shouldBe Right(DeregisterVatSuccess)
      }
    }

    "Issue new invoices answer is No" should {

      val issueInvoices: Option[YesNo] = Some(No)

      "Not delete outstanding invoices answer" in {
        setupMockDeleteOutstandingInvoicesNotCalled()

        val result = TestWipeRedundantDataService.wipeOutstandingInvoices(issueInvoices)
        result.futureValue shouldBe Right(DeregisterVatSuccess)
      }
    }
  }

  "Calling .wipeSicCode" when {

    "business activity answer is Yes" should {

      val businessActivity: Option[YesNo] = Some(Yes)

      "Not delete sic code answer" in {
        setupMockDeleteSicCodeAnswerServiceNotCalled()

        val result = TestWipeRedundantDataService.wipeSicCode(businessActivity)
        result.futureValue shouldBe Right(DeregisterVatSuccess)
      }
    }

    "business activity answer is No" should {

      val businessActivity: Option[YesNo] = Some(No)

      "Delete sic code answer" in {
        setupMockDeleteSicCodeAnswerService(Future.successful(Right(DeregisterVatSuccess)))

        val result = TestWipeRedundantDataService.wipeSicCode(businessActivity)
        result.futureValue shouldBe Right(DeregisterVatSuccess)
      }
    }
  }

  "Calling .wipeDataReadyForCeasedTradingJourney" when {

    "All deletions are successful" should {

      "Delete Data to start Ceased Trading Journey" in {

        inSequence {
          setupMockDeleteTaxableTurnover(Future.successful(Right(DeregisterVatSuccess)))

          setupMockDeleteWhyTurnoverBelow(Future.successful(Right(DeregisterVatSuccess)))
          setupMockDeleteNextTaxableTurnover(Future.successful(Right(DeregisterVatSuccess)))

          setupMockDeleteBusinessActivityAnswer(Future.successful(Right(DeregisterVatSuccess)))
          setupMockDeleteSicCodeAnswerService(Future.successful(Right(DeregisterVatSuccess)))
          setupMockDeleteZeroRatedSuppliesValueAnswer(Future.successful(Right(DeregisterVatSuccess)))
          setupMockDeletePurchasesExceedSuppliesAnswer(Future.successful(Right(DeregisterVatSuccess)))
        }

        val result = TestWipeRedundantDataService.wipeDataReadyForCeasedTradingJourney
        result.futureValue shouldBe Right(DeregisterVatSuccess)

      }

    }

    "First deletion is unsuccessful" should {

      "Return an error model" in {

        inSequence {
          setupMockDeleteTaxableTurnover(Future.successful(Left(errorModel)))

          setupMockDeleteWhyTurnoverBelowNotCalled()
          setupMockDeleteNextTaxableTurnoverNotCalled()

          setupMockDeleteBusinessActivityAnswerNotCalled()
          setupMockDeleteSicCodeAnswerServiceNotCalled()
          setupMockDeleteZeroRatedSuppliesValueAnswerNotCalled()
          setupMockDeletePurchasesExceedSuppliesAnswerNotCalled()
        }

        val result = TestWipeRedundantDataService.wipeDataReadyForCeasedTradingJourney
        result.futureValue shouldBe Left(errorModel)
      }
    }

    "Second deletion is unsuccessful" should {

      "Return an error model" in {

        inSequence {
          setupMockDeleteTaxableTurnover(Future.successful(Right(DeregisterVatSuccess)))

          setupMockDeleteWhyTurnoverBelow(Future.successful(Left(errorModel)))
          setupMockDeleteNextTaxableTurnoverNotCalled()

          setupMockDeleteBusinessActivityAnswerNotCalled()
          setupMockDeleteSicCodeAnswerServiceNotCalled()
          setupMockDeleteZeroRatedSuppliesValueAnswerNotCalled()
          setupMockDeletePurchasesExceedSuppliesAnswerNotCalled()
        }

        val result = TestWipeRedundantDataService.wipeDataReadyForCeasedTradingJourney
        result.futureValue shouldBe Left(errorModel)
      }
    }
    "Third deletion is unsuccessful" should {

      "Return an error model" in {

        inSequence {
          setupMockDeleteTaxableTurnover(Future.successful(Right(DeregisterVatSuccess)))

          setupMockDeleteWhyTurnoverBelow(Future.successful(Right(DeregisterVatSuccess)))
          setupMockDeleteNextTaxableTurnover(Future.successful(Left(errorModel)))

          setupMockDeleteBusinessActivityAnswerNotCalled()
          setupMockDeleteSicCodeAnswerServiceNotCalled()
          setupMockDeleteZeroRatedSuppliesValueAnswerNotCalled()
          setupMockDeletePurchasesExceedSuppliesAnswerNotCalled()
        }

        val result = TestWipeRedundantDataService.wipeDataReadyForCeasedTradingJourney
        result.futureValue shouldBe Left(errorModel)
      }
    }
    "Fourth deletion is unsuccessful" should {

      "Return an error model" in {

        inSequence {
          setupMockDeleteTaxableTurnover(Future.successful(Right(DeregisterVatSuccess)))

          setupMockDeleteWhyTurnoverBelow(Future.successful(Right(DeregisterVatSuccess)))
          setupMockDeleteNextTaxableTurnover(Future.successful(Right(DeregisterVatSuccess)))

          setupMockDeleteBusinessActivityAnswer(Future.successful(Left(errorModel)))
          setupMockDeleteSicCodeAnswerServiceNotCalled()
          setupMockDeleteZeroRatedSuppliesValueAnswerNotCalled()
          setupMockDeletePurchasesExceedSuppliesAnswerNotCalled()
        }

        val result = TestWipeRedundantDataService.wipeDataReadyForCeasedTradingJourney
        result.futureValue shouldBe Left(errorModel)
      }
    }
    "Fifth deletion is unsuccessful" should {

      "Return an error model" in {

        inSequence {
          setupMockDeleteTaxableTurnover(Future.successful(Right(DeregisterVatSuccess)))

          setupMockDeleteWhyTurnoverBelow(Future.successful(Right(DeregisterVatSuccess)))
          setupMockDeleteNextTaxableTurnover(Future.successful(Right(DeregisterVatSuccess)))

          setupMockDeleteBusinessActivityAnswer(Future.successful(Right(DeregisterVatSuccess)))
          setupMockDeleteSicCodeAnswerService(Future.successful(Left(errorModel)))
          setupMockDeleteZeroRatedSuppliesValueAnswerNotCalled()
          setupMockDeletePurchasesExceedSuppliesAnswerNotCalled()
        }

        val result = TestWipeRedundantDataService.wipeDataReadyForCeasedTradingJourney
        result.futureValue shouldBe Left(errorModel)
      }
    }
    "Sixth deletion is unsuccessful" should {

      "Return an error model" in {

        inSequence {
          setupMockDeleteTaxableTurnover(Future.successful(Right(DeregisterVatSuccess)))

          setupMockDeleteWhyTurnoverBelow(Future.successful(Right(DeregisterVatSuccess)))
          setupMockDeleteNextTaxableTurnover(Future.successful(Right(DeregisterVatSuccess)))

          setupMockDeleteBusinessActivityAnswer(Future.successful(Right(DeregisterVatSuccess)))
          setupMockDeleteSicCodeAnswerService(Future.successful(Right(DeregisterVatSuccess)))
          setupMockDeleteZeroRatedSuppliesValueAnswer(Future.successful(Left(errorModel)))
          setupMockDeletePurchasesExceedSuppliesAnswerNotCalled()
        }

        val result = TestWipeRedundantDataService.wipeDataReadyForCeasedTradingJourney
        result.futureValue shouldBe Left(errorModel)
      }
    }
    "Seventh deletion is unsuccessful" should {

      "Return an error model" in {

        inSequence {
          setupMockDeleteTaxableTurnover(Future.successful(Right(DeregisterVatSuccess)))

          setupMockDeleteWhyTurnoverBelow(Future.successful(Right(DeregisterVatSuccess)))
          setupMockDeleteNextTaxableTurnover(Future.successful(Right(DeregisterVatSuccess)))

          setupMockDeleteBusinessActivityAnswer(Future.successful(Right(DeregisterVatSuccess)))
          setupMockDeleteSicCodeAnswerService(Future.successful(Right(DeregisterVatSuccess)))
          setupMockDeleteZeroRatedSuppliesValueAnswer(Future.successful(Right(DeregisterVatSuccess)))
          setupMockDeletePurchasesExceedSuppliesAnswer(Future.successful(Left(errorModel)))
        }

        val result = TestWipeRedundantDataService.wipeDataReadyForCeasedTradingJourney
        result.futureValue shouldBe Left(errorModel)
      }
    }
  }

  "Calling .wipeDataReadyForBelowThresholdJourney" when {

    "All deletions are successful" should {

      "Delete Data to start Below Threshold Journey" in {

        inSequence {
          setupMockDeleteCeasedTradingDate(Future.successful(Right(DeregisterVatSuccess)))

          setupMockDeleteBusinessActivityAnswer(Future.successful(Right(DeregisterVatSuccess)))
          setupMockDeleteSicCodeAnswerService(Future.successful(Right(DeregisterVatSuccess)))
          setupMockDeleteZeroRatedSuppliesValueAnswer(Future.successful(Right(DeregisterVatSuccess)))
          setupMockDeletePurchasesExceedSuppliesAnswer(Future.successful(Right(DeregisterVatSuccess)))
        }

        val result = TestWipeRedundantDataService.wipeDataReadyForBelowThresholdJourney
        result.futureValue shouldBe Right(DeregisterVatSuccess)
      }
    }


    "First deletion is unsuccessful" should {

      "Return an error model" in {

        inSequence {
          setupMockDeleteCeasedTradingDate(Future.successful(Left(errorModel)))

          setupMockDeleteBusinessActivityAnswerNotCalled()
          setupMockDeleteSicCodeAnswerServiceNotCalled()
          setupMockDeleteZeroRatedSuppliesValueAnswerNotCalled()
          setupMockDeletePurchasesExceedSuppliesAnswerNotCalled()
        }

        val result = TestWipeRedundantDataService.wipeDataReadyForBelowThresholdJourney
        result.futureValue shouldBe Left(errorModel)
      }
    }

    "Second deletion is unsuccessful" should {

      "Return an error model" in {

        inSequence {
          setupMockDeleteCeasedTradingDate(Future.successful(Right(DeregisterVatSuccess)))

          setupMockDeleteBusinessActivityAnswer(Future.successful(Left(errorModel)))
          setupMockDeleteSicCodeAnswerServiceNotCalled()
          setupMockDeleteZeroRatedSuppliesValueAnswerNotCalled()
          setupMockDeletePurchasesExceedSuppliesAnswerNotCalled()
        }

        val result = TestWipeRedundantDataService.wipeDataReadyForBelowThresholdJourney
        result.futureValue shouldBe Left(errorModel)
      }
    }

    "Third deletion is unsuccessful" should {

      "Return an error model" in {

        inSequence {
          setupMockDeleteCeasedTradingDate(Future.successful(Right(DeregisterVatSuccess)))

          setupMockDeleteBusinessActivityAnswer(Future.successful(Right(DeregisterVatSuccess)))
          setupMockDeleteSicCodeAnswerService(Future.successful(Left(errorModel)))
          setupMockDeleteZeroRatedSuppliesValueAnswerNotCalled()
          setupMockDeletePurchasesExceedSuppliesAnswerNotCalled()
        }

        val result = TestWipeRedundantDataService.wipeDataReadyForBelowThresholdJourney
        result.futureValue shouldBe Left(errorModel)
      }
    }

    "Fourth deletion is unsuccessful" should {

      "Return an error model" in {

        inSequence {
          setupMockDeleteCeasedTradingDate(Future.successful(Right(DeregisterVatSuccess)))

          setupMockDeleteBusinessActivityAnswer(Future.successful(Right(DeregisterVatSuccess)))
          setupMockDeleteSicCodeAnswerService(Future.successful(Right(DeregisterVatSuccess)))
          setupMockDeleteZeroRatedSuppliesValueAnswer(Future.successful(Left(errorModel)))
          setupMockDeletePurchasesExceedSuppliesAnswerNotCalled()
        }

        val result = TestWipeRedundantDataService.wipeDataReadyForBelowThresholdJourney
        result.futureValue shouldBe Left(errorModel)
      }
    }

    "Fifth deletion is unsuccessful" should {

      "Return an error model" in {

        inSequence {
          setupMockDeleteCeasedTradingDate(Future.successful(Right(DeregisterVatSuccess)))

          setupMockDeleteBusinessActivityAnswer(Future.successful(Right(DeregisterVatSuccess)))
          setupMockDeleteSicCodeAnswerService(Future.successful(Right(DeregisterVatSuccess)))
          setupMockDeleteZeroRatedSuppliesValueAnswer(Future.successful(Right(DeregisterVatSuccess)))
          setupMockDeletePurchasesExceedSuppliesAnswer(Future.successful(Left(errorModel)))
        }

        val result = TestWipeRedundantDataService.wipeDataReadyForBelowThresholdJourney
        result.futureValue shouldBe Left(errorModel)
      }
    }
  }

  "Calling .wipeDataReadyForZeroRatedJourney" when {

    "All deletions are successful" should {
      "Delete Data ready for Zero Rated Journey" in {

        inSequence {
          setupMockDeleteCeasedTradingDate(Future.successful(Right(DeregisterVatSuccess)))

          setupMockDeleteWhyTurnoverBelow(Future.successful(Right(DeregisterVatSuccess)))
          setupMockDeleteTaxableTurnover(Future.successful(Right(DeregisterVatSuccess)))
        }
        val result = TestWipeRedundantDataService.wipeDataReadyForZeroRatedJourney
        result.futureValue shouldBe Right(DeregisterVatSuccess)
      }
    }

    "First deletion is unsuccessful" should {
      "Not first answer" in {

        inSequence {
          setupMockDeleteCeasedTradingDate(Future.successful(Left(errorModel)))

          setupMockDeleteWhyTurnoverBelowNotCalled()
          setupMockDeleteTaxableTurnoverNotCalled()
        }
        val result = TestWipeRedundantDataService.wipeDataReadyForZeroRatedJourney
        result.futureValue shouldBe Left(errorModel)
      }
    }

    "Second deletion is unsuccessful" should {
      "Not second answer" in {

        inSequence {
          setupMockDeleteCeasedTradingDate(Future.successful(Right(DeregisterVatSuccess)))

          setupMockDeleteWhyTurnoverBelow(Future.successful(Left(errorModel)))
          setupMockDeleteTaxableTurnoverNotCalled()
        }
        val result = TestWipeRedundantDataService.wipeDataReadyForZeroRatedJourney
        result.futureValue shouldBe Left(errorModel)
      }
    }


    "Third deletion is unsuccessful" should {
      "Not third answer" in {

        inSequence {
          setupMockDeleteCeasedTradingDate(Future.successful(Right(DeregisterVatSuccess)))

          setupMockDeleteWhyTurnoverBelow(Future.successful(Right(DeregisterVatSuccess)))
          setupMockDeleteTaxableTurnover(Future.successful(Left(errorModel)))
        }
        val result = TestWipeRedundantDataService.wipeDataReadyForZeroRatedJourney
        result.futureValue shouldBe Left(errorModel)
      }
    }

  }


  "Calling .wipeRedundantDeregReasonJourneyData" when {

    "Deregistration reason is Ceased" should {

      val deregReason: Option[DeregistrationReason] = Some(Ceased)

      "Call .wipe to remove redundant data" in {

        inSequence {
          setupMockDeleteTaxableTurnover(Future.successful(Right(DeregisterVatSuccess)))

          setupMockDeleteWhyTurnoverBelow(Future.successful(Right(DeregisterVatSuccess)))
          setupMockDeleteNextTaxableTurnover(Future.successful(Right(DeregisterVatSuccess)))

          setupMockDeleteBusinessActivityAnswer(Future.successful(Right(DeregisterVatSuccess)))
          setupMockDeleteSicCodeAnswerService(Future.successful(Right(DeregisterVatSuccess)))
          setupMockDeleteZeroRatedSuppliesValueAnswer(Future.successful(Right(DeregisterVatSuccess)))
          setupMockDeletePurchasesExceedSuppliesAnswer(Future.successful(Right(DeregisterVatSuccess)))
        }

        val result = TestWipeRedundantDataService.wipeRedundantDeregReasonJourneyData(deregReason)
        result.futureValue shouldBe Right(DeregisterVatSuccess)
      }
    }

    "Deregistration reason is Below Threshold" should {

      val deregReason: Option[DeregistrationReason] = Some(BelowThreshold)

      "Delete Ceased Trading date answer" in {

        inSequence {
          setupMockDeleteCeasedTradingDate(Future.successful(Right(DeregisterVatSuccess)))

          setupMockDeleteBusinessActivityAnswer(Future.successful(Right(DeregisterVatSuccess)))
          setupMockDeleteSicCodeAnswerService(Future.successful(Right(DeregisterVatSuccess)))
          setupMockDeleteZeroRatedSuppliesValueAnswer(Future.successful(Right(DeregisterVatSuccess)))
          setupMockDeletePurchasesExceedSuppliesAnswer(Future.successful(Right(DeregisterVatSuccess)))
        }

        val result = TestWipeRedundantDataService.wipeRedundantDeregReasonJourneyData(deregReason)
        result.futureValue shouldBe Right(DeregisterVatSuccess)
      }
    }

    "Deregistration reason is Zero Rated" should {

      val deregReason: Option[DeregistrationReason] = Some(ZeroRated)

      "Delete Ceased Trading date, Why Turnover Below and Next Taxable Turnover answers" in {
        inSequence {
          setupMockDeleteCeasedTradingDate(Future.successful(Right(DeregisterVatSuccess)))

          setupMockDeleteWhyTurnoverBelow(Future.successful(Right(DeregisterVatSuccess)))
          setupMockDeleteTaxableTurnover(Future.successful(Right(DeregisterVatSuccess)))
        }

        val result = TestWipeRedundantDataService.wipeRedundantDeregReasonJourneyData(deregReason)
        result.futureValue shouldBe Right(DeregisterVatSuccess)
      }
    }


    "Deregistration reason is not supplied" should {

      "Perform no deletions" in {
        val result = TestWipeRedundantDataService.wipeRedundantDeregReasonJourneyData(None)
        result.futureValue shouldBe Right(DeregisterVatSuccess)
      }
    }
  }

  "Calling .wipeRedundantData" when {

    "all data retrievals are successful" when {

      "all data deletions are successful" when {

        "Dereg Reason is Ceased" should {

          "make calls to wipe relevant data" in {

            inSequence {
              setupMockGetDeregReason(Future.successful(Right(Some(Ceased))))
              setupMockGetCapitalAssets(Future.successful(Right(Some(YesNoAmountModel(No, None)))))
              setupMockGetIssueNewInvoices(Future.successful(Right(Some(No))))
              setupMockGetOutstandingInvoices(Future.successful(Right(Some(No))))
              setupMockGetBusinessActivityAnswer(Future.successful(Right(None)))
              setupMockGetChooseDeregDate(Future.successful(Right(Some(Yes))))

              setupMockDeleteTaxableTurnover(Future.successful(Right(DeregisterVatSuccess)))
              setupMockDeleteWhyTurnoverBelow(Future.successful(Right(DeregisterVatSuccess)))
              setupMockDeleteNextTaxableTurnover(Future.successful(Right(DeregisterVatSuccess)))
              setupMockDeleteBusinessActivityAnswer(Future.successful(Right(DeregisterVatSuccess)))
              setupMockDeleteSicCodeAnswerService(Future.successful(Right(DeregisterVatSuccess)))
              setupMockDeleteZeroRatedSuppliesValueAnswer(Future.successful(Right(DeregisterVatSuccess)))
              setupMockDeletePurchasesExceedSuppliesAnswer(Future.successful(Right(DeregisterVatSuccess)))
              setupMockDeleteChooseDeregDate(Future.successful(Right(DeregisterVatSuccess)))
              setupMockDeleteDeregDate(Future.successful(Right(DeregisterVatSuccess)))
            }

            val result = TestWipeRedundantDataService.wipeRedundantData
            result.futureValue shouldBe Right(DeregisterVatSuccess)
          }
        }

        "Dereg Reason is Below Threshold" should {

          "make calls to wipe relevant data" in {

            inSequence {
              setupMockGetDeregReason(Future.successful(Right(Some(BelowThreshold))))
              setupMockGetCapitalAssets(Future.successful(Right(Some(YesNoAmountModel(No, None)))))
              setupMockGetIssueNewInvoices(Future.successful(Right(Some(No))))
              setupMockGetOutstandingInvoices(Future.successful(Right(Some(No))))
              setupMockGetBusinessActivityAnswer(Future.successful(Right(None)))
              setupMockGetChooseDeregDate(Future.successful(Right(Some(Yes))))

              setupMockDeleteCeasedTradingDate(Future.successful(Right(DeregisterVatSuccess)))
              setupMockDeleteBusinessActivityAnswer(Future.successful(Right(DeregisterVatSuccess)))
              setupMockDeleteSicCodeAnswerService(Future.successful(Right(DeregisterVatSuccess)))
              setupMockDeleteZeroRatedSuppliesValueAnswer(Future.successful(Right(DeregisterVatSuccess)))
              setupMockDeletePurchasesExceedSuppliesAnswer(Future.successful(Right(DeregisterVatSuccess)))
            }

            val result = TestWipeRedundantDataService.wipeRedundantData
            result.futureValue shouldBe Right(DeregisterVatSuccess)
          }

        }

        "Dereg Reason is Zero Rated and business activity answer is yes" should {

          "make calls to wipe relevant data" in {

            inSequence {
              setupMockGetDeregReason(Future.successful(Right(Some(ZeroRated))))
              setupMockGetCapitalAssets(Future.successful(Right(Some(YesNoAmountModel(No, None)))))
              setupMockGetIssueNewInvoices(Future.successful(Right(Some(No))))
              setupMockGetOutstandingInvoices(Future.successful(Right(Some(No))))
              setupMockGetBusinessActivityAnswer(Future.successful(Right(Some(Yes))))
              setupMockGetChooseDeregDate(Future.successful(Right(Some(Yes))))

              setupMockDeleteCeasedTradingDate(Future.successful(Right(DeregisterVatSuccess)))
              setupMockDeleteWhyTurnoverBelow(Future.successful(Right(DeregisterVatSuccess)))
              setupMockDeleteTaxableTurnover(Future.successful(Right(DeregisterVatSuccess)))
            }

            val result = TestWipeRedundantDataService.wipeRedundantData
            result.futureValue shouldBe Right(DeregisterVatSuccess)
          }

        }

        "Dereg Reason is Zero Rated and business activity answer is no" should {

          "make calls to wipe relevant data" in {

            inSequence {
              setupMockGetDeregReason(Future.successful(Right(Some(ZeroRated))))
              setupMockGetCapitalAssets(Future.successful(Right(Some(YesNoAmountModel(No, None)))))
              setupMockGetIssueNewInvoices(Future.successful(Right(Some(No))))
              setupMockGetOutstandingInvoices(Future.successful(Right(Some(No))))
              setupMockGetBusinessActivityAnswer(Future.successful(Right(Some(No))))
              setupMockGetChooseDeregDate(Future.successful(Right(Some(Yes))))

              setupMockDeleteCeasedTradingDate(Future.successful(Right(DeregisterVatSuccess)))
              setupMockDeleteWhyTurnoverBelow(Future.successful(Right(DeregisterVatSuccess)))
              setupMockDeleteTaxableTurnover(Future.successful(Right(DeregisterVatSuccess)))
              setupMockDeleteSicCodeAnswerService(Future.successful(Right(DeregisterVatSuccess)))
            }

            val result = TestWipeRedundantDataService.wipeRedundantData
            result.futureValue shouldBe Right(DeregisterVatSuccess)
          }

        }
      }
        "a data deletion for Outstanding Invoices is unsuccessful" should {

          "return an error model" in {

            inSequence {
              setupMockGetDeregReason(Future.successful(Right(Some(ZeroRated))))
              setupMockGetCapitalAssets(Future.successful(Right(Some(YesNoAmountModel(No, None)))))
              setupMockGetIssueNewInvoices(Future.successful(Right(Some(Yes))))
              setupMockGetOutstandingInvoices(Future.successful(Right(Some(No))))
              setupMockGetBusinessActivityAnswer(Future.successful(Right(Some(Yes))))
              setupMockGetChooseDeregDate(Future.successful(Right(Some(Yes))))

              setupMockDeleteCeasedTradingDate(Future.successful(Right(DeregisterVatSuccess)))
              setupMockDeleteWhyTurnoverBelow(Future.successful(Right(DeregisterVatSuccess)))
              setupMockDeleteTaxableTurnover(Future.successful(Right(DeregisterVatSuccess)))

              setupMockDeleteOutstandingInvoices(Future.successful(Left(errorModel)))
            }

            val result = TestWipeRedundantDataService.wipeRedundantData
            result.futureValue shouldBe Left(errorModel)
          }
        }
      "a data deletion for Dereg Date is unsuccessful" should {

        "return an error model" in {

          inSequence {
            setupMockGetDeregReason(Future.successful(Right(Some(Ceased))))
            setupMockGetCapitalAssets(Future.successful(Right(Some(YesNoAmountModel(No, None)))))
            setupMockGetIssueNewInvoices(Future.successful(Right(Some(No))))
            setupMockGetOutstandingInvoices(Future.successful(Right(Some(No))))
            setupMockGetBusinessActivityAnswer(Future.successful(Right(None)))
            setupMockGetChooseDeregDate(Future.successful(Right(Some(Yes))))

            setupMockDeleteTaxableTurnover(Future.successful(Right(DeregisterVatSuccess)))
            setupMockDeleteWhyTurnoverBelow(Future.successful(Right(DeregisterVatSuccess)))
            setupMockDeleteNextTaxableTurnover(Future.successful(Right(DeregisterVatSuccess)))
            setupMockDeleteBusinessActivityAnswer(Future.successful(Right(DeregisterVatSuccess)))
            setupMockDeleteSicCodeAnswerService(Future.successful(Right(DeregisterVatSuccess)))
            setupMockDeleteZeroRatedSuppliesValueAnswer(Future.successful(Right(DeregisterVatSuccess)))
            setupMockDeletePurchasesExceedSuppliesAnswer(Future.successful(Right(DeregisterVatSuccess)))

            setupMockDeleteChooseDeregDate(Future.successful(Left(errorModel)))
          }

          val result = TestWipeRedundantDataService.wipeRedundantData
          result.futureValue shouldBe Left(errorModel)
        }
      }

    }

    "a data retrieval is unsuccessful" should {
      "reason is Dereg Reason" should {
        "return an error model" in {

          inSequence {
            setupMockGetDeregReason(Future.successful(Left(errorModel)))
          }

          val result = TestWipeRedundantDataService.wipeRedundantData
          result.futureValue shouldBe Left(errorModel)
        }
      }
      "reason is Capital Assets" should {
        "return an error model" in {

          inSequence {
            setupMockGetDeregReason(Future.successful(Right(Some(Ceased))))
            setupMockGetCapitalAssets(Future.successful(Left(errorModel)))
          }

          val result = TestWipeRedundantDataService.wipeRedundantData
          result.futureValue shouldBe Left(errorModel)
        }
      }
      "reason is Issue Invoices" should {
        "return an error model" in {

          inSequence {
            setupMockGetDeregReason(Future.successful(Right(Some(Ceased))))
            setupMockGetCapitalAssets(Future.successful(Right(Some(YesNoAmountModel(No, None)))))
            setupMockGetIssueNewInvoices(Future.successful(Left(errorModel)))
          }

          val result = TestWipeRedundantDataService.wipeRedundantData
          result.futureValue shouldBe Left(errorModel)
        }
      }
      "reason is Outstanding Invoices" should {
        "return an error model" in {

          inSequence {
            setupMockGetDeregReason(Future.successful(Right(Some(Ceased))))
            setupMockGetCapitalAssets(Future.successful(Right(Some(YesNoAmountModel(No, None)))))
            setupMockGetIssueNewInvoices(Future.successful(Right(Some(No))))
            setupMockGetOutstandingInvoices(Future.successful(Left(errorModel)))
          }

          val result = TestWipeRedundantDataService.wipeRedundantData
          result.futureValue shouldBe Left(errorModel)
        }
      }
      "reason is business activity" should {
        "return an error model" in {

          inSequence {
            setupMockGetDeregReason(Future.successful(Right(Some(Ceased))))
            setupMockGetCapitalAssets(Future.successful(Right(Some(YesNoAmountModel(No, None)))))
            setupMockGetIssueNewInvoices(Future.successful(Right(Some(No))))
            setupMockGetOutstandingInvoices(Future.successful(Right(Some(No))))
            setupMockGetBusinessActivityAnswer(Future.successful(Left(errorModel)))
          }

          val result = TestWipeRedundantDataService.wipeRedundantData
          result.futureValue shouldBe Left(errorModel)
        }
      }
    }

  }
}
