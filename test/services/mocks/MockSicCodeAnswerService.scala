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
import services.SicCodeAnswerService

import scala.concurrent.Future


trait MockSicCodeAnswerService extends MockStoredAnswersService {

  val mockSicCodeAnswerService: SicCodeAnswerService = mock[SicCodeAnswerService]

  def setupMockGetSicCode(response: Either[ErrorModel, Option[String]])(implicit user: User[_]): Unit =
    setupMockGetAnswers(mockSicCodeAnswerService)(Future.successful(response))

  def setupMockStoreSicCode(data: String)(response: Either[ErrorModel, DeregisterVatResponse])(implicit user: User[_]): Unit =
    setupMockStoreAnswers(mockSicCodeAnswerService)(data)(Future.successful(response))

  def setupMockDeleteSicCodeAnswerService(response: Either[ErrorModel, DeregisterVatResponse])(implicit user: User[_]): Unit =
    setupMockDeleteAnswer(mockSicCodeAnswerService)(Future.successful(response))

  def setupMockDeleteSicCodeAnswerServiceNotCalled()(implicit user: User[_]): Unit =
    setupMockDeleteAnswerNotCalled(mockSicCodeAnswerService)
}
