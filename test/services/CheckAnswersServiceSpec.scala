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

import assets.constants.CheckYourAnswersTestConstants._
import models._
import services.mocks._
import utils.TestUtil


class CheckAnswersServiceSpec extends TestUtil {

  object TestCheckAnswersService extends CheckAnswersService(
    MockAccountingMethodAnswerService.mockStoredAnswersService,
    MockCapitalAssetsAnswerService.mockStoredAnswersService,
    MockCeasedTradingDateAnswerService.mockStoredAnswersService,
    MockDeregDateAnswerService.mockStoredAnswersService,
    MockDeregReasonAnswerService.mockStoredAnswersService,
    MockNextTaxableTurnoverAnswerService.mockStoredAnswersService,
    MockOptionTaxAnswerService.mockStoredAnswersService,
    MockIssueNewInvoicesAnswerService.mockStoredAnswersService,
    MockStocksAnswerService.mockStoredAnswersService,
    MockTaxableTurnoverAnswerService.mockStoredAnswersService,
    MockWhyTurnoverBelowAnswerService.mockStoredAnswersService,
    MockOutstandingInvoicesService.mockStoredAnswersService,
    mockConfig
  )

  val errorModel: ErrorModel = ErrorModel(500,"error model 1")

  "The CheckAnswersService.checkYourAnswersModel" when {

    "retrieving every answer" should {

      "return a CheckYourAnswerModel with every answer" in {

        MockDeregReasonAnswerService.setupMockGetAnswers(Right(Some(Ceased)))
        MockCeasedTradingDateAnswerService.setupMockGetAnswers(Right(Some(dateModel)))
        MockTaxableTurnoverAnswerService.setupMockGetAnswers(Right(Some(Yes)))
        MockNextTaxableTurnoverAnswerService.setupMockGetAnswers(Right(Some(taxableTurnoverBelow)))
        MockWhyTurnoverBelowAnswerService.setupMockGetAnswers(Right(Some(whyTurnoverBelowAll)))
        MockAccountingMethodAnswerService.setupMockGetAnswers(Right(Some(StandardAccounting)))
        MockOptionTaxAnswerService.setupMockGetAnswers(Right(Some(yesNoAmountYes)))
        MockStocksAnswerService.setupMockGetAnswers(Right(Some(yesNoAmountYes)))
        MockCapitalAssetsAnswerService.setupMockGetAnswers(Right(Some(yesNoAmountYes)))
        MockIssueNewInvoicesAnswerService.setupMockGetAnswers(Right(Some(Yes)))
        MockOutstandingInvoicesService.setupMockGetAnswers(Right(Some(Yes)))
        MockDeregDateAnswerService.setupMockGetAnswers(Right(Some(deregistrationDate)))

        await(TestCheckAnswersService.checkYourAnswersModel()) shouldBe Right(
          CheckYourAnswersModel(
            Some(Ceased),
            Some(dateModel),
            Some(Yes),
            Some(taxableTurnoverBelow),
            Some(whyTurnoverBelowAll),
            Some(StandardAccounting),
            Some(yesNoAmountYes),
            Some(yesNoAmountYes),
            Some(yesNoAmountYes),
            Some(Yes),
            Some(Yes),
            Some(deregistrationDate)
          )
        )
      }
    }

    "retrieving an error instead of the first answer" should {

      "return an error model" in {

        MockDeregReasonAnswerService.setupMockGetAnswers(Left(errorModel))

        await(TestCheckAnswersService.checkYourAnswersModel()) shouldBe Left(errorModel)
      }
    }

    "retrieving an error instead of the last answer" should {

      "return an error model" in {

        MockDeregReasonAnswerService.setupMockGetAnswers(Right(Some(Ceased)))
        MockCeasedTradingDateAnswerService.setupMockGetAnswers(Right(Some(dateModel)))
        MockAccountingMethodAnswerService.setupMockGetAnswers(Right(Some(StandardAccounting)))
        MockTaxableTurnoverAnswerService.setupMockGetAnswers(Right(Some(Yes)))
        MockNextTaxableTurnoverAnswerService.setupMockGetAnswers(Right(Some(taxableTurnoverBelow)))
        MockWhyTurnoverBelowAnswerService.setupMockGetAnswers(Right(Some(whyTurnoverBelowAll)))
        MockOptionTaxAnswerService.setupMockGetAnswers(Right(Some(yesNoAmountYes)))
        MockStocksAnswerService.setupMockGetAnswers(Right(Some(yesNoAmountYes)))
        MockCapitalAssetsAnswerService.setupMockGetAnswers(Right(Some(yesNoAmountYes)))
        MockIssueNewInvoicesAnswerService.setupMockGetAnswers(Right(Some(Yes)))
        MockOutstandingInvoicesService.setupMockGetAnswers(Right(Some(Yes)))
        MockDeregDateAnswerService.setupMockGetAnswers(Left(errorModel))

        await(TestCheckAnswersService.checkYourAnswersModel()) shouldBe Left(errorModel)
      }
    }
  }
}
