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

package services

import assets.constants.BaseTestConstants.{vrn, _}
import connectors.DeregisterVatConnector
import connectors.mocks.MockDeregisterVatConnector
import models.DeregisterVatSuccess
import utils.TestUtil


class StoredAnswersServiceSpec extends TestUtil with MockDeregisterVatConnector {

  object TestStoreAnswersService extends StoredAnswersService[TestModel] {
    override val deregisterVatConnector: DeregisterVatConnector = mockDeregisterVatConnector
    override val answerKey: String = testKey
  }

  "The StoredAnswersService" when {

    "Calling the .getAnswer method" when {

      "a success response is returned from the connector" should {

        "return the expected model" in {
          setupMockGetAnswers[TestModel](vrn, testKey)(Right(Some(testModel)))
          await(TestStoreAnswersService.getAnswer) shouldBe Right(Some(testModel))
        }
      }

      "an error response is returned form the connector" should {

        "return the expected error model" in {
          setupMockGetAnswers[TestModel](vrn, testKey)(Left(errorModel))
          await(TestStoreAnswersService.getAnswer) shouldBe Left(errorModel)
        }
      }
    }

    "Calling the .storeAnswer method" when {

      "a success response is returned from the connector" should {

        "return the expected model" in {
          setupMockStoreAnswers[TestModel](vrn, testKey, testModel)(Right(DeregisterVatSuccess))
          await(TestStoreAnswersService.storeAnswer(testModel)) shouldBe Right(DeregisterVatSuccess)
        }
      }

      "an error response is returned form the connector" should {

        "return the expected error model" in {
          setupMockStoreAnswers[TestModel](vrn, testKey, testModel)(Left(errorModel))
          await(TestStoreAnswersService.storeAnswer(testModel)) shouldBe Left(errorModel)
        }
      }
    }

    "Calling the .deleteAnswer method" when {

      "a success response is returned from the connector" should {

        "return the expected model" in {
          setupMockDeleteAnswer(vrn, testKey)(Right(DeregisterVatSuccess))
          await(TestStoreAnswersService.deleteAnswer) shouldBe Right(DeregisterVatSuccess)
        }
      }

      "an error response is returned form the connector" should {

        "return the expected error model" in {
          setupMockDeleteAnswer(vrn, testKey)(Left(errorModel))
          await(TestStoreAnswersService.deleteAnswer) shouldBe Left(errorModel)
        }
      }
    }
  }
}
