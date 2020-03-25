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
import org.scalamock.scalatest.MockFactory
import play.api.libs.json.Format
import services.ChooseDeregDateAnswerService
import uk.gov.hmrc.http.HeaderCarrier
import utils.TestUtil

import scala.concurrent.ExecutionContext

trait MockChooseDeregDateAnswerService extends MockStoredAnswersService {

  val mockChooseDeregDateAnswerService: ChooseDeregDateAnswerService = mock[ChooseDeregDateAnswerService]

  def setupMockGetChooseDeregDate(response: Either[ErrorModel, Option[YesNo]])(implicit user: User[_]): Unit =
    setupMockGetAnswers(mockChooseDeregDateAnswerService)(response)

  def setupMockStoreChooseDeregDate(data: YesNo)(response: Either[ErrorModel, DeregisterVatResponse])(implicit user: User[_]): Unit =
    setupMockStoreAnswers(mockChooseDeregDateAnswerService)(data)(response)

  def setupMockDeleteChooseDeregDate(response: Either[ErrorModel, DeregisterVatResponse])(implicit user: User[_]): Unit =
    setupMockDeleteAnswer(mockChooseDeregDateAnswerService)(response)

  def setupMockDeleteChooseDeregDateNotCalled()(implicit user: User[_]): Unit =
    setupMockDeleteAnswerNotCalled(mockChooseDeregDateAnswerService)
}
