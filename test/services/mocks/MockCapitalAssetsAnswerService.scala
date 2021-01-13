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
import services.CapitalAssetsAnswerService

trait MockCapitalAssetsAnswerService extends MockStoredAnswersService {

  val mockCapitalAssetsAnswerService: CapitalAssetsAnswerService = mock[CapitalAssetsAnswerService]

  def setupMockGetCapitalAssets(response: Either[ErrorModel, Option[YesNoAmountModel]])(implicit user: User[_]): Unit =
    setupMockGetAnswers(mockCapitalAssetsAnswerService)(response)

  def setupMockStoreCapitalAssets(data: YesNoAmountModel)(response: Either[ErrorModel, DeregisterVatResponse])(implicit user: User[_]): Unit =
    setupMockStoreAnswers(mockCapitalAssetsAnswerService)(data)(response)

  def setupMockDeleteCapitalAssets(response: Either[ErrorModel, DeregisterVatResponse])(implicit user: User[_]): Unit =
    setupMockDeleteAnswer(mockCapitalAssetsAnswerService)(response)

  def setupMockDeleteCapitalAssetsNotCalled()(implicit user: User[_]): Unit =
    setupMockDeleteAnswerNotCalled(mockCapitalAssetsAnswerService)
}
