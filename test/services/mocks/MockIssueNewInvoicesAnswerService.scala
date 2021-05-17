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

package services.mocks

import models._
import services.IssueNewInvoicesAnswerService

import scala.concurrent.Future

trait MockIssueNewInvoicesAnswerService extends MockStoredAnswersService {

  val mockIssueNewInvoicesAnswerService: IssueNewInvoicesAnswerService = mock[IssueNewInvoicesAnswerService]

  def setupMockGetIssueNewInvoices(response: Future[Either[ErrorModel, Option[YesNo]]])(implicit user: User[_]): Unit =
    setupMockGetAnswers(mockIssueNewInvoicesAnswerService)(response)

  def setupMockStoreIssueNewInvoices(data: YesNo)(response: Future[Either[ErrorModel, DeregisterVatResponse]])(implicit user: User[_]): Unit =
    setupMockStoreAnswers(mockIssueNewInvoicesAnswerService)(data)(response)


  def setupMockDeleteIssueNewInvoices(response: Future[Either[ErrorModel, DeregisterVatResponse]])(implicit user: User[_]): Unit =
    setupMockDeleteAnswer(mockIssueNewInvoicesAnswerService)(response)


  def setupMockIssueNewInvoicesNotCalled()(implicit user: User[_]): Unit =
    setupMockDeleteAnswerNotCalled(mockIssueNewInvoicesAnswerService)

}
