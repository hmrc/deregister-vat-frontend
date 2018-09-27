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

package services.mocks

import models._
import org.scalamock.scalatest.MockFactory
import play.api.libs.json.Format
import services._
import uk.gov.hmrc.http.HeaderCarrier
import utils.TestUtil

import scala.concurrent.ExecutionContext

abstract class MockStoredAnswersService[T] extends TestUtil with MockFactory {

  val mockStoredAnswersService: StoredAnswersService[T]

  def setupMockGetAnswers(response: Either[ErrorModel, Option[T]])(implicit user: User[_]): Unit = {
    (mockStoredAnswersService.getAnswer(_: User[_], _: Format[T], _: HeaderCarrier, _: ExecutionContext))
      .expects(user, *, *, *)
      .returns(response)
  }

  def setupMockStoreAnswers(data: T)(response: Either[ErrorModel, DeregisterVatResponse])(implicit user: User[_]): Unit = {
    (mockStoredAnswersService.storeAnswer(_: T)(_: User[_], _: Format[T], _: HeaderCarrier, _: ExecutionContext))
      .expects(data, user, *, *, *)
      .returns(response)
  }

  def setupMockDeleteAnswer(response: Either[ErrorModel, DeregisterVatResponse])(implicit user: User[_]): Unit = {
    (mockStoredAnswersService.deleteAnswer(_: User[_], _: HeaderCarrier, _: ExecutionContext))
      .expects(user, *, *)
      .returns(response)
  }
}

object MockDeregReasonAnswerService extends MockStoredAnswersService[DeregistrationReason] {
  override val mockStoredAnswersService: DeregReasonAnswerService = mock[DeregReasonAnswerService]
}

object MockTaxableTurnoverAnswerService extends MockStoredAnswersService[TaxableTurnoverModel] {
  override val mockStoredAnswersService: TaxableTurnoverAnswerService = mock[TaxableTurnoverAnswerService]
}

object MockNextTaxableTurnoverAnswerService extends MockStoredAnswersService[TaxableTurnoverModel] {
  override val mockStoredAnswersService: NextTaxableTurnoverAnswerService = mock[NextTaxableTurnoverAnswerService]
}

object MockCeasedTradingDateAnswerService extends MockStoredAnswersService[DateModel] {
  override val mockStoredAnswersService: CeasedTradingDateAnswerService = mock[CeasedTradingDateAnswerService]
}

object MockStocksAnswerService extends MockStoredAnswersService[YesNoAmountModel] {
  override val mockStoredAnswersService: StocksAnswerService = mock[StocksAnswerService]
}

object MockAccountingMethodAnswerService extends MockStoredAnswersService[VATAccountsModel] {
  override val mockStoredAnswersService: AccountingMethodAnswerService = mock[AccountingMethodAnswerService]
}

object MockCapitalAssetsAnswerService extends MockStoredAnswersService[YesNoAmountModel] {
  override val mockStoredAnswersService: CapitalAssetsAnswerService = mock[CapitalAssetsAnswerService]
}

object MockWhyTurnoverBelowAnswerService extends MockStoredAnswersService[WhyTurnoverBelowModel] {
  override val mockStoredAnswersService: WhyTurnoverBelowAnswerService = mock[WhyTurnoverBelowAnswerService]
}

object MockIssueNewInvoicesAnswerService extends MockStoredAnswersService[YesNo] {
  override val mockStoredAnswersService: IssueNewInvoicesAnswerService = mock[IssueNewInvoicesAnswerService]
}

object MockOptionTaxAnswerService extends MockStoredAnswersService[YesNoAmountModel] {
  override val mockStoredAnswersService: OptionTaxAnswerService = mock[OptionTaxAnswerService]
}

object MockDeregDateAnswerService extends MockStoredAnswersService[DeregistrationDateModel] {
  override val mockStoredAnswersService: DeregDateAnswerService = mock[DeregDateAnswerService]
}

object MockOutstandingInvoicesService extends MockStoredAnswersService[YesNo] {
  override val mockStoredAnswersService: OutstandingInvoicesAnswerService = mock[OutstandingInvoicesAnswerService]
}
