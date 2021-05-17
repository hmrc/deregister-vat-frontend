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

import assets.constants.CheckYourAnswersTestConstants._
import assets.constants.NextTaxableTurnoverTestConstants._
import assets.constants.WhyTurnoverBelowTestConstants._
import assets.constants.YesNoAmountTestConstants._
import models._
import org.scalatest.concurrent.ScalaFutures.convertScalaFuture
import services.mocks._
import utils.TestUtil

import scala.concurrent.Future

class CheckAnswersServiceSpec extends TestUtil with MockDeregReasonAnswerService
  with MockCeasedTradingDateAnswerService with MockCapitalAssetsAnswerService with MockTaxableTurnoverAnswerService
  with MockIssueNewInvoicesAnswerService with MockOutstandingInvoicesService with MockWhyTurnoverBelowAnswerService
  with MockChooseDeregDateAnswerService with MockNextTaxableTurnoverAnswerService with MockStocksAnswerService
  with MockOptionTaxAnswerService with MockAccountingMethodAnswerService with MockBusinessActivityAnswerService
  with MockSicCodeAnswerService with MockZeroRatedSuppliesValueService with MockPurchasesExceedSuppliesAnswerService
  with MockDeregDateAnswerService {

  object TestCheckAnswersService extends CheckAnswersService(
    mockAccountingMethodAnswerService,
    mockCapitalAssetsAnswerService,
    mockCeasedTradingDateAnswerService,
    mockChooseDeregDateAnswerService,
    mockDeregDateAnswerService,
    mockDeregReasonAnswerService,
    mockNextTaxableTurnoverAnswerService,
    mockOptionTaxAnswerService,
    mockIssueNewInvoicesAnswerService,
    mockStocksAnswerService,
    mockTaxableTurnoverAnswerService,
    mockWhyTurnoverBelowAnswerService,
    mockOutstandingInvoicesService,
    mockBusinessActivityAnswerService,
    mockSicCodeAnswerService,
    mockZeroRatedSuppliesValueService,
    mockPurchasesExceedSuppliesAnswerService,
    mockConfig
  )

  val errorModel: ErrorModel = ErrorModel(500,"error model 1")

  "The CheckAnswersService.checkYourAnswersModel" when {

    "retrieving every answer" should {

      "return a CheckYourAnswerModel with every answer" in {

        setupMockGetDeregReason(Future.successful(Right(Some(Ceased))))
        setupMockGetCeasedTradingDate(Future.successful(Right(Some(dateModel))))
        setupMockGetAccountingMethod(Future.successful(Right(Some(StandardAccounting))))
        setupMockGetTaxableTurnover(Future.successful(Right(Some(Yes))))
        setupMockGetNextTaxableTurnover(Future.successful(Right(Some(nextTaxableTurnoverBelow))))
        setupMockGetWhyTurnoverBelow(Future.successful(Right(Some(whyTurnoverBelowAll))))
        setupMockGetOptionTax(Future.successful(Right(Some(ottModel))))
        setupMockGetCapitalAssets(Future.successful(Right(Some(assetsModel))))
        setupMockGetStocks(Future.successful(Right(Some(stocksModel))))
        setupMockGetIssueNewInvoices(Future.successful(Right(Some(Yes))))
        setupMockGetOutstandingInvoices(Future.successful(Right(Some(Yes))))
        setupMockGetChooseDeregDate(Future.successful(Right(Some(Yes))))
        setupMockGetDeregDate(Future.successful(Right(Some(dateModel))))
        setupMockGetBusinessActivityAnswer(Future.successful(Right(Some(Yes))))
        setupMockGetSicCode(Future.successful(Right(Some(sicCodeValue))))
        setupMockGetZeroRatedSupplies(Future.successful(Right(Some(zeroRatedSuppliesValue))))
        setupMockGetPurchasesExceedSuppliesAnswer(Future.successful(Right(Some(Yes))))

        TestCheckAnswersService.checkYourAnswersModel().futureValue shouldBe Right(
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
            Some(Yes),
            Some(dateModel),
            Some(Yes),
            Some(sicCodeValue),
            Some(zeroRatedSuppliesValue),
            Some(Yes)
          )
        )
      }
    }

    "retrieving an error instead of the first answer" should {

      "return an error model" in {

        setupMockGetDeregReason(Future.successful(Left(errorModel)))

        TestCheckAnswersService.checkYourAnswersModel().futureValue shouldBe Left(errorModel)
      }
    }

    "retrieving an error instead of the last answer" should {

      "return an error model" in {

        setupMockGetDeregReason(Future.successful(Right(Some(Ceased))))
        setupMockGetCeasedTradingDate(Future.successful(Right(Some(dateModel))))
        setupMockGetAccountingMethod(Future.successful(Right(Some(StandardAccounting))))
        setupMockGetTaxableTurnover(Future.successful(Right(Some(Yes))))
        setupMockGetNextTaxableTurnover(Future.successful(Right(Some(nextTaxableTurnoverBelow))))
        setupMockGetWhyTurnoverBelow(Future.successful(Right(Some(whyTurnoverBelowAll))))
        setupMockGetOptionTax(Future.successful(Right(Some(ottModel))))
        setupMockGetCapitalAssets(Future.successful(Right(Some(assetsModel))))
        setupMockGetStocks(Future.successful(Right(Some(stocksModel))))
        setupMockGetIssueNewInvoices(Future.successful(Right(Some(Yes))))
        setupMockGetOutstandingInvoices(Future.successful(Right(Some(Yes))))
        setupMockGetChooseDeregDate(Future.successful(Right(Some(Yes))))
        setupMockGetDeregDate(Future.successful(Right(Some(dateModel))))
        setupMockGetBusinessActivityAnswer(Future.successful(Right(Some(Yes))))
        setupMockGetSicCode(Future.successful(Right(Some(sicCodeValue))))
        setupMockGetZeroRatedSupplies(Future.successful(Right(Some(zeroRatedSuppliesValue))))
        setupMockGetPurchasesExceedSuppliesAnswer(Future.successful(Left(errorModel)))

        TestCheckAnswersService.checkYourAnswersModel().futureValue shouldBe Left(errorModel)
      }
    }
  }
}
