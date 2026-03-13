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

package services

import assets.constants.BaseTestConstants.{vrn, errorModel}
import connectors.mocks.MockDeregisterVatConnector
import models.{DeregisterVatSuccess, Yes}
import org.scalatest.concurrent.ScalaFutures.convertScalaFuture
import utils.TestUtil

import scala.concurrent.Future

class OTTNotificationAnswerServiceSpec extends TestUtil with MockDeregisterVatConnector {

  object OTTNotificationAnswerService extends OTTNotificationAnswerService(mockDeregisterVatConnector)

  ".answerKey" should {
    "return the key 'ottNotification'" in {
      OTTNotificationAnswerService.answerKey shouldBe "ottNotification"
    }
  }

  ".getAnswer" when {
    "a success response is returned from the connector" should {
      "return the expected model" in {
        setupMockGetAnswers(vrn, "ottNotification")(Future.successful(Right(Some(Yes))))
        OTTNotificationAnswerService.getAnswer.futureValue shouldBe Right(Some(Yes))
      }
    }

    "an error response is returned from the connector" should {
      "return the expected error model" in {
        setupMockGetAnswers(vrn, "ottNotification")(Future.successful(Left(errorModel)))
        OTTNotificationAnswerService.getAnswer.futureValue shouldBe Left(errorModel)
      }
    }
  }

  ".storeAnswer" when {
    "a success response is returned from the connector" should {
      "return the expected model" in {
        setupMockStoreAnswers(vrn, "ottNotification", Yes)(Future.successful(Right(DeregisterVatSuccess)))
        OTTNotificationAnswerService.storeAnswer(Yes).futureValue shouldBe Right(DeregisterVatSuccess)
      }
    }

    "an error response is returned from the connector" should {
      "return the expected error model" in {
        setupMockStoreAnswers(vrn, "ottNotification", Yes)(Future.successful(Left(errorModel)))
        OTTNotificationAnswerService.storeAnswer(Yes).futureValue shouldBe Left(errorModel)
      }
    }
  }

  ".deleteAnswer" when {
    "a success response is returned from the connector" should {
      "return the expected model" in {
        setupMockDeleteAnswer(vrn, "ottNotification")(Future.successful(Right(DeregisterVatSuccess)))
        OTTNotificationAnswerService.deleteAnswer.futureValue shouldBe Right(DeregisterVatSuccess)
      }
    }

    "an error response is returned from the connector" should {
      "return the expected error model" in {
        setupMockDeleteAnswer(vrn, "ottNotification")(Future.successful(Left(errorModel)))
        OTTNotificationAnswerService.deleteAnswer.futureValue shouldBe Left(errorModel)
      }
    }
  }

}