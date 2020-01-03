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
import services.NextTaxableTurnoverAnswerService
import uk.gov.hmrc.http.HeaderCarrier
import utils.TestUtil

import scala.concurrent.ExecutionContext

trait MockNextTaxableTurnoverAnswerService extends MockStoredAnswersService {

  val mockNextTaxableTurnoverAnswerService: NextTaxableTurnoverAnswerService = mock[NextTaxableTurnoverAnswerService]

  def setupMockGetNextTaxableTurnover(response: Either[ErrorModel, Option[NumberInputModel]])(implicit user: User[_]): Unit =
    setupMockGetAnswers(mockNextTaxableTurnoverAnswerService)(response)

  def setupMockStoreNextTaxableTurnover(data: NumberInputModel)(response: Either[ErrorModel, DeregisterVatResponse])(implicit user: User[_]): Unit =
    setupMockStoreAnswers(mockNextTaxableTurnoverAnswerService)(data)(response)

  def setupMockDeleteNextTaxableTurnover(response: Either[ErrorModel, DeregisterVatResponse])(implicit user: User[_]): Unit =
    setupMockDeleteAnswer(mockNextTaxableTurnoverAnswerService)(response)

  def setupMockDeleteNextTaxableTurnoverNotCalled()(implicit user: User[_]): Unit =
    setupMockDeleteAnswerNotCalled(mockNextTaxableTurnoverAnswerService)

}
