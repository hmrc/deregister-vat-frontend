/*
 * Copyright 2019 HM Revenue & Customs
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
import services.BusinessActivityAnswerService

trait MockBusinessActivityAnswerService extends MockStoredAnswersService{

  val mockBusinessActivityAnswerService: BusinessActivityAnswerService = mock[BusinessActivityAnswerService]

  def setupMockDeleteBusinessActivityAnswer(response: Either[ErrorModel, DeregisterVatResponse])(implicit user: User[_]): Unit =
    setupMockDeleteAnswer(mockBusinessActivityAnswerService)(response)

  def setupMockDeleteBusinessActivityAnswerNotCalled()(implicit user: User[_]): Unit =
    setupMockDeleteAnswerNotCalled(mockBusinessActivityAnswerService)

  def setupMockGetBusinessActivityAnswer(response: Either[ErrorModel, Option[YesNo]])(implicit user: User[_]): Unit =
    setupMockGetAnswers(mockBusinessActivityAnswerService)(response)

  def setupMockStoreBusinessActivityAnswer(data: YesNo)(response: Either[ErrorModel, DeregisterVatResponse])(implicit user: User[_]): Unit =
    setupMockStoreAnswers(mockBusinessActivityAnswerService)(data)(response)
}
