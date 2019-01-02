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
import org.scalamock.scalatest.MockFactory
import play.api.libs.json.Format
import services.WhyTurnoverBelowAnswerService
import uk.gov.hmrc.http.HeaderCarrier
import utils.TestUtil

import scala.concurrent.ExecutionContext

trait MockWhyTurnoverBelowAnswerService extends MockStoredAnswersService {

  val mockWhyTurnoverBelowAnswerService: WhyTurnoverBelowAnswerService = mock[WhyTurnoverBelowAnswerService]

  def setupMockGetWhyTurnoverBelow(response: Either[ErrorModel, Option[WhyTurnoverBelowModel]])(implicit user: User[_]): Unit =
    setupMockGetAnswers(mockWhyTurnoverBelowAnswerService)(response)

  def setupMockStoreWhyTurnoverBelow(data: WhyTurnoverBelowModel)(response: Either[ErrorModel, DeregisterVatResponse])(implicit user: User[_]): Unit =
    setupMockStoreAnswers(mockWhyTurnoverBelowAnswerService)(data)(response)

  def setupMockDeleteWhyTurnoverBelow(response: Either[ErrorModel, DeregisterVatResponse])(implicit user: User[_]): Unit =
    setupMockDeleteAnswer(mockWhyTurnoverBelowAnswerService)(response)

  def setupMockDeleteWhyTurnoverBelowNotCalled()(implicit user: User[_]): Unit =
    setupMockDeleteAnswerNotCalled(mockWhyTurnoverBelowAnswerService)

}
