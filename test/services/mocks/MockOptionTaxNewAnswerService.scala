/*
 * Copyright 2024 HM Revenue & Customs
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

import models.{YesNo, ErrorModel, User, DeregisterVatResponse}
import services.OptionTaxNewAnswerService

import scala.concurrent.Future

trait MockOptionTaxNewAnswerService extends MockStoredAnswersService {

  val mockOptionTaxNewAnswerService: OptionTaxNewAnswerService = mock[OptionTaxNewAnswerService]

  def setupMockGetOptionTax(response: Either[ErrorModel, Option[YesNo]])(implicit user: User[_]): Unit =
    setupMockGetAnswers(mockOptionTaxNewAnswerService)(Future.successful(response))

  def setupMockStoreOptionTax(data: YesNo)(response: Either[ErrorModel, DeregisterVatResponse])(implicit user: User[_]): Unit =
    setupMockStoreAnswers(mockOptionTaxNewAnswerService)(data)(Future.successful(response))

  def setupMockDeleteOptionTax(response: Either[ErrorModel, DeregisterVatResponse])(implicit user: User[_]): Unit =
    setupMockDeleteAnswer(mockOptionTaxNewAnswerService)(Future.successful(response))

  def setupMockOptionTaxNotCalled()(implicit user: User[_]): Unit =
    setupMockDeleteAnswerNotCalled(mockOptionTaxNewAnswerService)

}
