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

package services

import assets.constants.CheckYourAnswersTestConstants._
import assets.constants.NextTaxableTurnoverTestConstants._
import assets.constants.WhyTurnoverBelowTestConstants._
import assets.constants.YesNoAmountTestConstants._
import models._
import services.mocks._
import utils.TestUtil


class CheckAnswersServiceSpec extends TestUtil with MockDeregReasonAnswerService
  with MockCeasedTradingDateAnswerService with MockCapitalAssetsAnswerService with MockTaxableTurnoverAnswerService
  with MockIssueNewInvoicesAnswerService with MockOutstandingInvoicesService with MockWhyTurnoverBelowAnswerService
  with MockDeregDateAnswerService with MockNextTaxableTurnoverAnswerService with MockStocksAnswerService with MockOptionTaxAnswerService
  with MockAccountingMethodAnswerService {

  object TestCheckAnswersService extends CheckAnswersService(
    mockAccountingMethodAnswerService,
    mockCapitalAssetsAnswerService,
    mockCeasedTradingDateAnswerService,
    mockDeregDateAnswerService,
    mockDeregReasonAnswerService,
    mockNextTaxableTurnoverAnswerService,
    mockOptionTaxAnswerService,
    mockIssueNewInvoicesAnswerService,
    mockStocksAnswerService,
    mockTaxableTurnoverAnswerService,
    mockWhyTurnoverBelowAnswerService,
    mockOutstandingInvoicesService,
    mockConfig
  )

  val errorModel: ErrorModel = ErrorModel(500,"error model 1")

  "The CheckAnswersService.checkYourAnswersModel" when {

    "retrieving every answer" should {

      "return a CheckYourAnswerModel with every answer" in {

        setupMockGetDeregReason(Right(Some(Ceased)))
        setupMockGetCeasedTradingDate(Right(Some(dateModel)))
        setupMockGetAccountingMethod(Right(Some(StandardAccounting)))
        setupMockGetTaxableTurnover(Right(Some(Yes)))
        setupMockGetNextTaxableTurnover(Right(Some(nextTaxableTurnoverBelow)))
        setupMockGetWhyTurnoverBelow(Right(Some(whyTurnoverBelowAll)))
        setupMockGetOptionTax(Right(Some(ottModel)))
        setupMockGetCapitalAssets(Right(Some(assetsModel)))
        setupMockGetStocks(Right(Some(stocksModel)))
        setupMockGetIssueNewInvoices(Right(Some(Yes)))
        setupMockGetOutstandingInvoices(Right(Some(Yes)))
        setupMockGetDeregDate(Right(Some(deregistrationDate)))

        await(TestCheckAnswersService.checkYourAnswersModel()) shouldBe Right(
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
            Some(deregistrationDate)
          )
        )
      }
    }

    "retrieving an error instead of the first answer" should {

      "return an error model" in {

        setupMockGetDeregReason(Left(errorModel))

        await(TestCheckAnswersService.checkYourAnswersModel()) shouldBe Left(errorModel)
      }
    }

    "retrieving an error instead of the last answer" should {

      "return an error model" in {

        setupMockGetDeregReason(Right(Some(Ceased)))
        setupMockGetCeasedTradingDate(Right(Some(dateModel)))
        setupMockGetAccountingMethod(Right(Some(StandardAccounting)))
        setupMockGetTaxableTurnover(Right(Some(Yes)))
        setupMockGetNextTaxableTurnover(Right(Some(nextTaxableTurnoverBelow)))
        setupMockGetWhyTurnoverBelow(Right(Some(whyTurnoverBelowAll)))
        setupMockGetOptionTax(Right(Some(ottModel)))
        setupMockGetCapitalAssets(Right(Some(assetsModel)))
        setupMockGetStocks(Right(Some(stocksModel)))
        setupMockGetIssueNewInvoices(Right(Some(Yes)))
        setupMockGetOutstandingInvoices(Right(Some(Yes)))
        setupMockGetDeregDate(Left(errorModel))

        await(TestCheckAnswersService.checkYourAnswersModel()) shouldBe Left(errorModel)
      }
    }
  }
}
