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

import assets.constants.BaseTestConstants.{vrn, _}
import assets.constants.DeregistrationInfoTestConstants.deregistrationInfoMinModel
import connectors.mocks.MockVatSubscriptionConnector
import models.VatSubscriptionSuccess
import services.mocks._
import utils.TestUtil


class VatSubscriptionServiceSpec extends TestUtil with MockVatSubscriptionConnector {

  object TestVatSubscriptionService extends UpdateDeregistrationService(
    mockVatSubscriptionConnector,
    MockAccountingMethodAnswerService.mockStoredAnswersService,
    MockCapitalAssetsAnswerService.mockStoredAnswersService,
    MockCeasedTradingDateAnswerService.mockStoredAnswersService,
    MockDeregDateAnswerService.mockStoredAnswersService,
    MockDeregReasonAnswerService.mockStoredAnswersService,
    MockIssueNewInvoicesAnswerService.mockStoredAnswersService,
    MockNextTaxableTurnoverAnswerService.mockStoredAnswersService,
    MockOptionTaxAnswerService.mockStoredAnswersService,
    MockOutstandingInvoicesService.mockStoredAnswersService,
    MockStocksAnswerService.mockStoredAnswersService,
    MockTaxableTurnoverAnswerService.mockStoredAnswersService,
    MockWhyTurnoverBelowAnswerService.mockStoredAnswersService
  )

  "The VatSubscriptionService" when {

    "Calling the .storeAnswer method" when {

      "a success response is returned from the connector" should {

        "return the expected model" in {
          setupMockStoreAnswers[TestModel](vrn, deregistrationInfoMinModel)(Right(VatSubscriptionSuccess))
          MockAccountingMethodAnswerService.setupMockGetAnswers(Right(VatSubscriptionSuccess))
          await(TestVatSubscriptionService.updateDereg) shouldBe Right(VatSubscriptionSuccess)
        }
      }

      "an error response is returned form the connector" should {

        "return the expected error model" in {
          setupMockStoreAnswers[TestModel](vrn, deregistrationInfoMinModel)(Left(errorModel))
          await(TestVatSubscriptionService.updateDereg) shouldBe Left(errorModel)
        }
      }
    }
  }
}
