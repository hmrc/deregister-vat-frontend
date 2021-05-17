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
import services.StocksAnswerService

import scala.concurrent.Future

trait MockStocksAnswerService extends MockStoredAnswersService {

  val mockStocksAnswerService: StocksAnswerService = mock[StocksAnswerService]

  def setupMockGetStocks(response: Future[Either[ErrorModel, Option[YesNoAmountModel]]])(implicit user: User[_]): Unit =
    setupMockGetAnswers(mockStocksAnswerService)(response)

  def setupMockStoreStocks(data: YesNoAmountModel)(response: Future[Either[ErrorModel, DeregisterVatResponse]])(implicit user: User[_]): Unit =
    setupMockStoreAnswers(mockStocksAnswerService)(data)(response)

  def setupMockDeleteStocks(response: Future[Either[ErrorModel, DeregisterVatResponse]])(implicit user: User[_]): Unit =
    setupMockDeleteAnswer(mockStocksAnswerService)(response)

  def setupMockDeleteStocksNotCalled()(implicit user: User[_]): Unit =
    setupMockDeleteAnswerNotCalled(mockStocksAnswerService)

}
