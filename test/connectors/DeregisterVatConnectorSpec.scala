/*
 * Copyright 2022 HM Revenue & Customs
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
import org.scalatest.concurrent.ScalaFutures.convertScalaFuture
import utils.TestUtil

import scala.concurrent.Future


class DeregisterVatConnectorSpec extends TestUtil with MockHttp {

  object TestDeregisterVatConnector extends DeregisterVatConnector(mockHttp, mockConfig)

  "DeregisterVatConnector" when {

    s"given vrn: $vrn and key: $testKey for the url" should {

      "return the correct url" in {
        TestDeregisterVatConnector.url(vrn,testKey) shouldBe s"${mockConfig.deregisterVatUrl}/deregister-vat/data/$vrn/$testKey"
      }
    }

    s"given vrn: $vrn only for the url" should {

      "return the correct url" in {
        TestDeregisterVatConnector.url(vrn) shouldBe s"${mockConfig.deregisterVatUrl}/deregister-vat/data/$vrn"
      }
    }

    "Calling .getAnswers" when {

      "A valid response is parsed" should {

        "return a valid model" in {
          setupMockHttpGet[TestModel](TestDeregisterVatConnector.url(vrn, testKey))(Future.successful(Right(testModel)))
          TestDeregisterVatConnector.getAnswers[TestModel](vrn, testKey).futureValue shouldBe Right(testModel)
        }
      }

      "An error response is parsed" should {

        "return an error model" in {
          setupMockHttpGet[TestModel](TestDeregisterVatConnector.url(vrn, testKey))(Future.successful(Left(errorModel)))
          TestDeregisterVatConnector.getAnswers[TestModel](vrn, testKey).futureValue shouldBe Left(errorModel)
        }
      }
    }

    "Calling .putAnswers" when {

      "A valid response is parsed" should {

        "return a DeregisterVatSuccess object" in {
          setupMockHttpPut[TestModel](TestDeregisterVatConnector.url(vrn, testKey), testModel)(Future.successful(Right(DeregisterVatSuccess)))
          TestDeregisterVatConnector.putAnswers[TestModel](vrn, testKey, testModel).futureValue shouldBe Right(DeregisterVatSuccess)
        }
      }

      "An invalid response is parsed" should {

        "return an error model" in {
          setupMockHttpPut[TestModel](TestDeregisterVatConnector.url(vrn, testKey), testModel)(Future.successful(Left(errorModel)))
          TestDeregisterVatConnector.putAnswers[TestModel](vrn, testKey, testModel).futureValue shouldBe Left(errorModel)
        }
      }
    }

    "Calling .deleteAnswer" when {

      "A valid response is parsed" should {

        "return a DeregisterVatSuccess object" in {
          setupMockHttpDelete[TestModel](TestDeregisterVatConnector.url(vrn, testKey))(Future.successful(Right(DeregisterVatSuccess)))
          TestDeregisterVatConnector.deleteAnswer(vrn, testKey).futureValue shouldBe Right(DeregisterVatSuccess)
        }
      }

      "An invalid response is parsed" should {

        "return an error model" in {
          setupMockHttpDelete[TestModel](TestDeregisterVatConnector.url(vrn, testKey))(Future.successful(Left(errorModel)))
          TestDeregisterVatConnector.deleteAnswer(vrn, testKey).futureValue shouldBe Left(errorModel)
        }
      }
    }

    "Calling .deleteAllAnswers" when {

      "A valid response is parsed" should {

        "return a DeregisterVatSuccess object" in {
          setupMockHttpDelete[TestModel](TestDeregisterVatConnector.url(vrn))(Future.successful(Right(DeregisterVatSuccess)))
          TestDeregisterVatConnector.deleteAllAnswers(vrn).futureValue shouldBe Right(DeregisterVatSuccess)
        }
      }

      "An invalid response is parsed" should {

        "return an error model" in {
          setupMockHttpDelete[TestModel](TestDeregisterVatConnector.url(vrn))(Future.successful(Left(errorModel)))
          TestDeregisterVatConnector.deleteAllAnswers(vrn).futureValue shouldBe Left(errorModel)
        }
      }
    }
  }
}
