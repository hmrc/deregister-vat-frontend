/*
 * Copyright 2018 HM Revenue & Customs
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
import connectors.mocks.MockDeregisterVatConnector
import models.DeregisterVatSuccess
import utils.TestUtil


class DeleteAllStoredAnswersServiceSpec extends TestUtil with MockDeregisterVatConnector {

  object TestDeleteAllStoredAnswersService extends DeleteAllStoredAnswersService(mockDeregisterVatConnector)

  "The StoredAnswersService" when {

    "Calling the .deleteAllAnswers method" when {

      "a success response is returned from the connector" should {

        "return the expected model" in {
          setupMockDeleteAllAnswers(vrn)(Right(DeregisterVatSuccess))
          await(TestDeleteAllStoredAnswersService.deleteAllAnswers) shouldBe Right(DeregisterVatSuccess)
        }
      }

      "an error response is returned form the connector" should {

        "return the expected error model" in {
          setupMockDeleteAllAnswers(vrn)(Left(errorModel))
          await(TestDeleteAllStoredAnswersService.deleteAllAnswers) shouldBe Left(errorModel)
        }
      }
    }
  }
}
