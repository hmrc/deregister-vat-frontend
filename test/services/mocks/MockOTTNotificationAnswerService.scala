/*
 * Copyright 2026 HM Revenue & Customs
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

import models.{DeregisterVatResponse, ErrorModel, User, YesNo}
import services.OTTNotificationAnswerService

import scala.concurrent.Future

trait MockOTTNotificationAnswerService extends MockStoredAnswersService {

  val mockOTTNotificationAnswerService: OTTNotificationAnswerService = mock[OTTNotificationAnswerService]

  def setupMockGetOTTNotification(response: Either[ErrorModel, Option[YesNo]])(implicit user:User[_]): Unit =
    setupMockGetAnswers(mockOTTNotificationAnswerService)(Future.successful(response))

  def setupMockStoreOTTNotification(data: YesNo)(response: Either[ErrorModel, DeregisterVatResponse])(implicit user:User[_]): Unit =
    setupMockStoreAnswers(mockOTTNotificationAnswerService)(data)(Future.successful(response))
}
