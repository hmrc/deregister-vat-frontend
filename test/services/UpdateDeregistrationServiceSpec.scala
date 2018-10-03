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

import assets.constants.BaseTestConstants.vrn
import assets.constants.DateModelTestConstants._
import assets.constants.TaxableTurnoverTestConstants._
import assets.constants.DeregistrationInfoTestConstants._
import assets.constants.WhyTurnoverBelowTestConstants._
import assets.constants.YesNoAmountTestConstants._
import connectors.mocks.MockVatSubscriptionConnector
import models._
import play.api.http.Status.INTERNAL_SERVER_ERROR
import services.mocks._
import utils.TestUtil


class UpdateDeregistrationServiceSpec extends TestUtil with MockVatSubscriptionConnector {

  object TestUpdateDeregistrationService extends UpdateDeregistrationService(
    MockDeregReasonAnswerService.mockStoredAnswersService,
    MockCeasedTradingDateAnswerService.mockStoredAnswersService,
    MockTaxableTurnoverAnswerService.mockStoredAnswersService,
    MockNextTaxableTurnoverAnswerService.mockStoredAnswersService,
    MockWhyTurnoverBelowAnswerService.mockStoredAnswersService,
    MockAccountingMethodAnswerService.mockStoredAnswersService,
    MockOptionTaxAnswerService.mockStoredAnswersService,
    MockStocksAnswerService.mockStoredAnswersService,
    MockCapitalAssetsAnswerService.mockStoredAnswersService,
    MockIssueNewInvoicesAnswerService.mockStoredAnswersService,
    MockOutstandingInvoicesService.mockStoredAnswersService,
    MockDeregDateAnswerService.mockStoredAnswersService,
    mockVatSubscriptionConnector
  )

  "The UpdateDeregistrationService" when {

    "Calling the .updateDereg method" when {

      "a success response is returned from the connector" should {

        "return the expected model" in {

          MockDeregReasonAnswerService.setupMockGetAnswers(Right(Some(BelowThreshold)))
          MockCeasedTradingDateAnswerService.setupMockGetAnswers(Right(Some(todayDateModel)))
          MockAccountingMethodAnswerService.setupMockGetAnswers(Right(Some(CashAccounting)))
          MockTaxableTurnoverAnswerService.setupMockGetAnswers(Right(Some(taxableTurnoverBelow)))
          MockNextTaxableTurnoverAnswerService.setupMockGetAnswers(Right(Some(taxableTurnoverBelow)))
          MockWhyTurnoverBelowAnswerService.setupMockGetAnswers(Right(Some(whyTurnoverBelowOne)))
          MockOptionTaxAnswerService.setupMockGetAnswers(Right(Some(ottModel)))
          MockStocksAnswerService.setupMockGetAnswers(Right(Some(stocksModel)))
          MockCapitalAssetsAnswerService.setupMockGetAnswers(Right(Some(assetsModel)))
          MockIssueNewInvoicesAnswerService.setupMockGetAnswers(Right(Some(Yes)))
          MockOutstandingInvoicesService.setupMockGetAnswers(Right(Some(Yes)))
          MockDeregDateAnswerService.setupMockGetAnswers(Right(Some(deregistrationDateModel)))
          setupMockSubmit(vrn, deregistrationInfoMaxModel)(Right(VatSubscriptionSuccess))
          await(TestUpdateDeregistrationService.updateDereg) shouldBe Right(VatSubscriptionSuccess)
        }
      }

      "an error response is returned from the connector" in {

        MockDeregReasonAnswerService.setupMockGetAnswers(Right(Some(BelowThreshold)))
        MockCeasedTradingDateAnswerService.setupMockGetAnswers(Right(Some(todayDateModel)))
        MockAccountingMethodAnswerService.setupMockGetAnswers(Right(Some(CashAccounting)))
        MockTaxableTurnoverAnswerService.setupMockGetAnswers(Right(Some(taxableTurnoverBelow)))
        MockNextTaxableTurnoverAnswerService.setupMockGetAnswers(Right(Some(taxableTurnoverBelow)))
        MockWhyTurnoverBelowAnswerService.setupMockGetAnswers(Right(Some(whyTurnoverBelowOne)))
        MockOptionTaxAnswerService.setupMockGetAnswers(Right(Some(ottModel)))
        MockStocksAnswerService.setupMockGetAnswers(Right(Some(stocksModel)))
        MockCapitalAssetsAnswerService.setupMockGetAnswers(Right(Some(assetsModel)))
        MockIssueNewInvoicesAnswerService.setupMockGetAnswers(Right(Some(Yes)))
        MockOutstandingInvoicesService.setupMockGetAnswers(Right(Some(Yes)))
        MockDeregDateAnswerService.setupMockGetAnswers(Right(Some(deregistrationDateModel)))
        setupMockSubmit(vrn, deregistrationInfoMaxModel)(Left(ErrorModel(INTERNAL_SERVER_ERROR, "error")))
        await(TestUpdateDeregistrationService.updateDereg) shouldBe Left(ErrorModel(INTERNAL_SERVER_ERROR, "error"))
      }


      "an error is returned from one of the AnswerServices" in {

        MockDeregReasonAnswerService.setupMockGetAnswers(Left(ErrorModel(INTERNAL_SERVER_ERROR, "error")))
        await(TestUpdateDeregistrationService.updateDereg) shouldBe Left(ErrorModel(INTERNAL_SERVER_ERROR, "error"))
      }
    }
  }
}

