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

package connectors

import _root_.mocks.MockHttp
import assets.constants.BaseTestConstants.{vrn, _}
import models._
import utils.TestUtil


class DeregisterVatConnectorSpec extends TestUtil with MockHttp {

  object TestDeregisterVatConnector extends DeregisterVatConnector(mockHttp, mockConfig)

  "DeregisterVatConnector" when {

    s"given vrn: $vrn and key: $testKey for the url" should {

      "return the correct url" in {
        TestDeregisterVatConnector.url(vrn,testKey) shouldBe s"${mockConfig.deregisterVatUrl}/data/$vrn/$testKey"
      }
    }

    s"given vrn: $vrn only for the url" should {

      "return the correct url" in {
        TestDeregisterVatConnector.url(vrn) shouldBe s"${mockConfig.deregisterVatUrl}/data/$vrn"
      }
    }

    s"Calling .getAnswers" when {

      "A valid response is parsed" should {

        "return a valid model" in {
          setupMockHttpGet[TestModel](TestDeregisterVatConnector.url(vrn, testKey))(Right(testModel))
          await(TestDeregisterVatConnector.getAnswers[TestModel](vrn, testKey)) shouldBe Right(testModel)
        }
      }

      "An error response is parsed" should {

        "return an error model" in {
          setupMockHttpGet[TestModel](TestDeregisterVatConnector.url(vrn, testKey))(Left(errorModel))
          await(TestDeregisterVatConnector.getAnswers[TestModel](vrn, testKey)) shouldBe Left(errorModel)
        }
      }
    }

    s"Calling .putAnswers" when {

      "A valid response is parsed" should {

        "return a DeregisterVatSuccess object" in {
          setupMockHttpPut[TestModel](TestDeregisterVatConnector.url(vrn, testKey), testModel)(Right(DeregisterVatSuccess))
          await(TestDeregisterVatConnector.putAnswers[TestModel](vrn, testKey, testModel)) shouldBe Right(DeregisterVatSuccess)
        }
      }

      "An invalid response is parsed" should {

        "return an error model" in {
          setupMockHttpPut[TestModel](TestDeregisterVatConnector.url(vrn, testKey), testModel)(Left(errorModel))
          await(TestDeregisterVatConnector.putAnswers[TestModel](vrn, testKey, testModel)) shouldBe Left(errorModel)
        }
      }
    }

    s"Calling .deleteAnswer" when {

      "A valid response is parsed" should {

        "return a DeregisterVatSuccess object" in {
          setupMockHttpDelete[TestModel](TestDeregisterVatConnector.url(vrn, testKey))(Right(DeregisterVatSuccess))
          await(TestDeregisterVatConnector.deleteAnswer(vrn, testKey)) shouldBe Right(DeregisterVatSuccess)
        }
      }

      "An invalid response is parsed" should {

        "return an error model" in {
          setupMockHttpDelete[TestModel](TestDeregisterVatConnector.url(vrn, testKey))(Left(errorModel))
          await(TestDeregisterVatConnector.deleteAnswer(vrn, testKey)) shouldBe Left(errorModel)
        }
      }
    }

    s"Calling .deleteAllAnswers" when {

      "A valid response is parsed" should {

        "return a DeregisterVatSuccess object" in {
          setupMockHttpDelete[TestModel](TestDeregisterVatConnector.url(vrn))(Right(DeregisterVatSuccess))
          await(TestDeregisterVatConnector.deleteAllAnswers(vrn)) shouldBe Right(DeregisterVatSuccess)
        }
      }

      "An invalid response is parsed" should {

        "return an error model" in {
          setupMockHttpDelete[TestModel](TestDeregisterVatConnector.url(vrn))(Left(errorModel))
          await(TestDeregisterVatConnector.deleteAllAnswers(vrn)) shouldBe Left(errorModel)
        }
      }
    }
  }
}
