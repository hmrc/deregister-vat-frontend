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
    MockDeregDateAnswerService.mockStoredAnswersService
  )

  val vrn: String = "999999999"

  "Calling .wipeDeregDate" when {

    "Deregistration reason is Ceased" when {

      val deregReason: Option[DeregistrationReason] = Some(Ceased)

      "Capital assets, Issue new invoices and Outstanding invoices answers are No" should {

        val capitalAssets: Option[YesNoAmountModel] = Some(YesNoAmountModel(No, None))
        val issueInvoices: Option[YesNo] = Some(No)
        val outstandingInvoices: Option[YesNo] = Some(No)

        "Delete deregistration date answer" in {
          (MockDeregDateAnswerService.mockStoredAnswersService.deleteAnswer(_: User[_], _: HeaderCarrier, _: ExecutionContext))
            .expects(user, *, *)
            .returns(Right(DeregisterVatSuccess))

          val result = TestWipeRedundantDataService.wipeDeregDate(deregReason, capitalAssets, issueInvoices, outstandingInvoices)
          await(result) shouldBe Right(DeregisterVatSuccess)
        }
      }

      "Capital assets answer is Yes, all others are No" should {

        val capitalAssets: Option[YesNoAmountModel] = Some(YesNoAmountModel(Yes, Some(1)))
        val issueInvoices: Option[YesNo] = Some(No)
        val outstandingInvoices: Option[YesNo] = Some(No)

        "Not delete deregistration date answer" in {
          (MockDeregDateAnswerService.mockStoredAnswersService.deleteAnswer(_: User[_], _: HeaderCarrier, _: ExecutionContext))
            .expects(user, *, *)
            .never()

          val result = TestWipeRedundantDataService.wipeDeregDate(deregReason, capitalAssets, issueInvoices, outstandingInvoices)
          await(result) shouldBe Right(DeregisterVatSuccess)
        }
      }

      "Issue new invoices answer is Yes, all others are No" should {

        val capitalAssets: Option[YesNoAmountModel] = Some(YesNoAmountModel(No, None))
        val issueInvoices: Option[YesNo] = Some(Yes)
        val outstandingInvoices: Option[YesNo] = Some(No)

        "Not delete deregistration date answer" in {
          (MockDeregDateAnswerService.mockStoredAnswersService.deleteAnswer(_: User[_], _: HeaderCarrier, _: ExecutionContext))
            .expects(user, *, *)
            .never()

          val result = TestWipeRedundantDataService.wipeDeregDate(deregReason, capitalAssets, issueInvoices, outstandingInvoices)
          await(result) shouldBe Right(DeregisterVatSuccess)
        }
      }

      "Outstanding invoices answer is Yes, all others are No" should {

        val capitalAssets: Option[YesNoAmountModel] = Some(YesNoAmountModel(No, None))
        val issueInvoices: Option[YesNo] = Some(No)
        val outstandingInvoices: Option[YesNo] = Some(Yes)

        "Not delete deregistration date answer" in {
          (MockDeregDateAnswerService.mockStoredAnswersService.deleteAnswer(_: User[_], _: HeaderCarrier, _: ExecutionContext))
            .expects(user, *, *)
            .never()

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
        (MockDeregDateAnswerService.mockStoredAnswersService.deleteAnswer(_: User[_], _: HeaderCarrier, _: ExecutionContext))
          .expects(user, *, *)
          .never()

        val result = TestWipeRedundantDataService.wipeDeregDate(deregReason, capitalAssets, issueInvoices, outstandingInvoices)
        await(result) shouldBe Right(DeregisterVatSuccess)
      }
    }
  }

  "Calling .wipeOutstandingInvoices" when {

    "Issue new invoices answer is Yes" should {

      val issueInvoices: Option[YesNo] = Some(Yes)

      "Delete outstanding invoices answer" in {
        (MockOutstandingInvoicesService.mockStoredAnswersService.deleteAnswer(_: User[_], _: HeaderCarrier, _: ExecutionContext))
          .expects(user, *, *)
          .returns(Right(DeregisterVatSuccess))

        val result = TestWipeRedundantDataService.wipeOutstandingInvoices(issueInvoices)
        await(result) shouldBe Right(DeregisterVatSuccess)
      }
    }

    "Issue new invoices answer is No" should {

      val issueInvoices: Option[YesNo] = Some(No)

      "Not delete outstanding invoices answer" in {
        (MockOutstandingInvoicesService.mockStoredAnswersService.deleteAnswer(_: User[_], _: HeaderCarrier, _: ExecutionContext))
          .expects(user, *, *)
          .never()

        val result = TestWipeRedundantDataService.wipeOutstandingInvoices(issueInvoices)
        await(result) shouldBe Right(DeregisterVatSuccess)
      }
    }
  }

  "Calling .wipeBelowThresholdJourney" when {

    "All deletions are successful" should {

      "Delete Turnover below, Taxable turnover and Next taxable turnover answers" in {

        inSequence {
          (MockWhyTurnoverBelowAnswerService.mockStoredAnswersService.deleteAnswer(_: User[_], _: HeaderCarrier, _: ExecutionContext))
            .expects(user, *, *)
            .returns(Right(DeregisterVatSuccess))

          (MockTaxableTurnoverAnswerService.mockStoredAnswersService.deleteAnswer(_: User[_], _: HeaderCarrier, _: ExecutionContext))
            .expects(user, *, *)
            .returns(Right(DeregisterVatSuccess))

          (MockNextTaxableTurnoverAnswerService.mockStoredAnswersService.deleteAnswer(_: User[_], _: HeaderCarrier, _: ExecutionContext))
            .expects(user, *, *)
            .returns(Right(DeregisterVatSuccess))
        }

        val result = TestWipeRedundantDataService.wipeBelowThresholdJourney
        await(result) shouldBe Right(DeregisterVatSuccess)
      }
    }

    val errorModel: ErrorModel = ErrorModel(1, "")

    "First deletion is unsuccessful" should {

      "Not delete Taxable turnover and Next taxable turnover answers" in {

        inSequence {
          (MockWhyTurnoverBelowAnswerService.mockStoredAnswersService.deleteAnswer(_: User[_], _: HeaderCarrier, _: ExecutionContext))
            .expects(user, *, *)
            .returns(Left(errorModel))

          (MockTaxableTurnoverAnswerService.mockStoredAnswersService.deleteAnswer(_: User[_], _: HeaderCarrier, _: ExecutionContext))
            .expects(user, *, *)
            .never()

          (MockNextTaxableTurnoverAnswerService.mockStoredAnswersService.deleteAnswer(_: User[_], _: HeaderCarrier, _: ExecutionContext))
            .expects(user, *, *)
            .never()
        }

        val result = TestWipeRedundantDataService.wipeBelowThresholdJourney
        await(result) shouldBe Left(errorModel)
      }
    }

    "Second deletion is unsuccessful" should {

      "Not delete Next taxable turnover answer" in {

        inSequence {
          (MockWhyTurnoverBelowAnswerService.mockStoredAnswersService.deleteAnswer(_: User[_], _: HeaderCarrier, _: ExecutionContext))
            .expects(user, *, *)
            .returns(Right(DeregisterVatSuccess))

          (MockTaxableTurnoverAnswerService.mockStoredAnswersService.deleteAnswer(_: User[_], _: HeaderCarrier, _: ExecutionContext))
            .expects(user, *, *)
            .returns(Left(errorModel))

          (MockNextTaxableTurnoverAnswerService.mockStoredAnswersService.deleteAnswer(_: User[_], _: HeaderCarrier, _: ExecutionContext))
            .expects(user, *, *)
            .never()
        }

        val result = TestWipeRedundantDataService.wipeBelowThresholdJourney
        await(result) shouldBe Left(errorModel)
      }
    }

    "Third deletion is unsuccessful" should {

      "Return an error model" in {

        inSequence {
          (MockWhyTurnoverBelowAnswerService.mockStoredAnswersService.deleteAnswer(_: User[_], _: HeaderCarrier, _: ExecutionContext))
            .expects(user, *, *)
            .returns(Right(DeregisterVatSuccess))

          (MockTaxableTurnoverAnswerService.mockStoredAnswersService.deleteAnswer(_: User[_], _: HeaderCarrier, _: ExecutionContext))
            .expects(user, *, *)
            .returns(Right(DeregisterVatSuccess))

          (MockNextTaxableTurnoverAnswerService.mockStoredAnswersService.deleteAnswer(_: User[_], _: HeaderCarrier, _: ExecutionContext))
            .expects(user, hc, ec)
            .returns(Left(errorModel))
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
          (MockWhyTurnoverBelowAnswerService.mockStoredAnswersService.deleteAnswer(_: User[_], _: HeaderCarrier, _: ExecutionContext))
            .expects(user, *, *)
            .returns(Right(DeregisterVatSuccess))

          (MockTaxableTurnoverAnswerService.mockStoredAnswersService.deleteAnswer(_: User[_], _: HeaderCarrier, _: ExecutionContext))
            .expects(user, *, *)
            .returns(Right(DeregisterVatSuccess))

          (MockNextTaxableTurnoverAnswerService.mockStoredAnswersService.deleteAnswer(_: User[_], _: HeaderCarrier, _: ExecutionContext))
            .expects(user, *, *)
            .returns(Right(DeregisterVatSuccess))
        }

        val result = TestWipeRedundantDataService.wipeRedundantDeregReasonJourneyData(deregReason)
        await(result) shouldBe Right(DeregisterVatSuccess)
      }
    }

    "Deregistration reason is Below Threshold" should {

      val deregReason: Option[DeregistrationReason] = Some(BelowThreshold)

      "Delete Ceased Trading date answer" in {

        (MockCeasedTradingDateAnswerService.mockStoredAnswersService.deleteAnswer(_: User[_], _: HeaderCarrier, _: ExecutionContext))
          .expects(user, *, *)
          .returns(Right(DeregisterVatSuccess))

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
}
