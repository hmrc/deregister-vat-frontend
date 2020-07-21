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

package services.mocks

import models._
import services.AccountingMethodAnswerService

trait MockAccountingMethodAnswerService extends MockStoredAnswersService {

  val mockAccountingMethodAnswerService: AccountingMethodAnswerService = mock[AccountingMethodAnswerService]

  def setupMockGetAccountingMethod(response: Either[ErrorModel, Option[VATAccountsModel]])(implicit user: User[_]): Unit =
    setupMockGetAnswers(mockAccountingMethodAnswerService)(response)

  def setupMockStoreAccountingMethod(data: VATAccountsModel)(response: Either[ErrorModel, DeregisterVatResponse])(implicit user: User[_]): Unit =
    setupMockStoreAnswers(mockAccountingMethodAnswerService)(data)(response)

  def setupMockDeleteAccountingMethod(response: Either[ErrorModel, DeregisterVatResponse])(implicit user: User[_]): Unit =
    setupMockDeleteAnswer(mockAccountingMethodAnswerService)(response)

  def setupMockDeleteAccountingMethodNotCalled()(implicit user: User[_]): Unit =
    setupMockDeleteAnswerNotCalled(mockAccountingMethodAnswerService)
}
