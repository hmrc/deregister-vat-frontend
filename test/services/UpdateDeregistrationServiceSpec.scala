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

import assets.constants.BaseTestConstants.vrn
import assets.constants.DateModelTestConstants._
import assets.constants.DeregistrationInfoTestConstants._
import assets.constants.NextTaxableTurnoverTestConstants._
import assets.constants.WhyTurnoverBelowTestConstants._
import assets.constants.YesNoAmountTestConstants._
import assets.constants.ZeroRatedTestConstants.zeroRatedSuppliesValue
import audit.mocks.MockAuditConnector
import audit.models.DeregAuditModel
import connectors.mocks.MockVatSubscriptionConnector
import models._
import play.api.http.Status.INTERNAL_SERVER_ERROR
import play.api.mvc.AnyContentAsEmpty
import services.mocks._
import utils.TestUtil

class UpdateDeregistrationServiceSpec     extends TestUtil
  with MockVatSubscriptionConnector       with MockDeregReasonAnswerService
  with MockCeasedTradingDateAnswerService with MockCapitalAssetsAnswerService
  with MockTaxableTurnoverAnswerService   with MockIssueNewInvoicesAnswerService
  with MockOutstandingInvoicesService     with MockWhyTurnoverBelowAnswerService
  with MockDeregDateAnswerService         with MockNextTaxableTurnoverAnswerService
  with MockStocksAnswerService            with MockOptionTaxAnswerService
  with MockAccountingMethodAnswerService  with MockPurchasesExceedSuppliesAnswerService
  with MockSicCodeAnswerService           with MockZeroRatedSuppliesValueService
  with MockAuditConnector {

  object TestUpdateDeregistrationService extends UpdateDeregistrationService(
    mockDeregReasonAnswerService,
    mockCeasedTradingDateAnswerService,
    mockTaxableTurnoverAnswerService,
    mockNextTaxableTurnoverAnswerService,
    mockWhyTurnoverBelowAnswerService,
    mockAccountingMethodAnswerService,
    mockOptionTaxAnswerService,
    mockCapitalAssetsAnswerService,
    mockStocksAnswerService,
    mockIssueNewInvoicesAnswerService,
    mockOutstandingInvoicesService,
    mockDeregDateAnswerService,
    mockPurchasesExceedSuppliesAnswerService,
    mockSicCodeAnswerService,
    mockZeroRatedSuppliesValueService,
    mockAuditConnector,
    mockVatSubscriptionConnector
  )

  "The UpdateDeregistrationService" when {

    "Calling the .updateDereg method" when {

      "a success response is returned from the connector" should {

        "return the expected model" in {

          implicit val user: User[AnyContentAsEmpty.type] = agentUserPrefYes

          setupMockGetDeregReason(Right(Some(BelowThreshold)))
          setupMockGetCeasedTradingDate(Right(Some(todayDateModel)))
          setupMockGetAccountingMethod(Right(Some(CashAccounting)))
          setupMockGetTaxableTurnover(Right(Some(Yes)))
          setupMockGetNextTaxableTurnover(Right(Some(nextTaxableTurnoverBelow)))
          setupMockGetWhyTurnoverBelow(Right(Some(whyTurnoverBelowOne)))
          setupMockGetOptionTax(Right(Some(ottModel)))
          setupMockGetCapitalAssets(Right(Some(assetsModel)))
          setupMockGetStocks(Right(Some(stocksModel)))
          setupMockGetIssueNewInvoices(Right(Some(Yes)))
          setupMockGetOutstandingInvoices(Right(Some(Yes)))
          setupMockGetDeregDate(Right(Some(laterDateModel)))
          setupMockGetPurchasesExceedSuppliesAnswer(Right(Some(Yes)))
          setupMockGetSicCode(Right(Some(sicCodeValue)))
          setupMockGetZeroRatedSupplies(Right(Some(zeroRatedSuppliesValue)))

          setupMockSubmit(vrn, deregistrationInfoMaxModel)(Right(VatSubscriptionSuccess))
          setupMockSendExplicitAudit(DeregAuditModel.auditType, DeregAuditModel(user, deregistrationInfoMaxModel))

          await(TestUpdateDeregistrationService.updateDereg) shouldBe Right(VatSubscriptionSuccess)
        }
      }

      "an error response is returned from the connector" in {

        implicit val user: User[AnyContentAsEmpty.type] = agentUserPrefYes

        setupMockGetDeregReason(Right(Some(BelowThreshold)))
        setupMockGetCeasedTradingDate(Right(Some(todayDateModel)))
        setupMockGetAccountingMethod(Right(Some(CashAccounting)))
        setupMockGetTaxableTurnover(Right(Some(Yes)))
        setupMockGetNextTaxableTurnover(Right(Some(nextTaxableTurnoverBelow)))
        setupMockGetWhyTurnoverBelow(Right(Some(whyTurnoverBelowOne)))
        setupMockGetOptionTax(Right(Some(ottModel)))
        setupMockGetCapitalAssets(Right(Some(assetsModel)))
        setupMockGetStocks(Right(Some(stocksModel)))
        setupMockGetIssueNewInvoices(Right(Some(Yes)))
        setupMockGetOutstandingInvoices(Right(Some(Yes)))
        setupMockGetDeregDate(Right(Some(laterDateModel)))
        setupMockGetPurchasesExceedSuppliesAnswer(Right(Some(Yes)))
        setupMockGetSicCode(Right(Some(sicCodeValue)))
        setupMockGetZeroRatedSupplies(Right(Some(zeroRatedSuppliesValue)))

        setupMockSubmit(vrn, deregistrationInfoMaxModel)(Left(ErrorModel(INTERNAL_SERVER_ERROR, "error")))
        setupMockSendExplicitAudit(DeregAuditModel.auditType, DeregAuditModel(user, deregistrationInfoMaxModel))

        await(TestUpdateDeregistrationService.updateDereg) shouldBe Left(ErrorModel(INTERNAL_SERVER_ERROR, "error"))

      }


      "an error is returned from one of the AnswerServices" in {
        setupMockGetDeregReason(Left(ErrorModel(INTERNAL_SERVER_ERROR, "error")))
        await(TestUpdateDeregistrationService.updateDereg) shouldBe Left(ErrorModel(INTERNAL_SERVER_ERROR, "error"))
      }
    }
  }
}

