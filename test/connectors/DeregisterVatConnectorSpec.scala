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

import assets.constants.BaseTestConstants.vrn
import mocks.MockHttp
import models._
import play.api.http.Status
import utils.TestUtil


class DeregisterVatConnectorSpec extends TestUtil with MockHttp{

  val errorModel = ErrorModel(Status.INTERNAL_SERVER_ERROR,"Error")
  val testKey = "testKey"
  val testModel = VATAccountsModel("s")

  object TestDeregisterVatConnector extends DeregisterVatConnector(mockHttp, mockConfig)

  "DeregisterVatConnector" when {

    s"given vrn: $vrn and key: key for the url" should {

      "return the correct url" in {
        TestDeregisterVatConnector.url(vrn,testKey) shouldBe s"${mockConfig.deregisterVatUrl}/data/$vrn/$testKey"
      }
    }

    s"successfully calling .getAnswers" should {

      "return a valid model" in {
        setupMockHttpGet[VATAccountsModel](TestDeregisterVatConnector.url(vrn, testKey))(Right(testModel))
        await(TestDeregisterVatConnector.getAnswers(vrn, testKey)(Ceased.format)) shouldBe Right(testModel)
      }
    }

    s"successfully calling .putAnswers" should {

      "return a DeregisterVatResponse model" in {
        setupMockHttpPut[VATAccountsModel](TestDeregisterVatConnector.url(vrn, testKey), testModel)(Right(DeregisterVatSuccess))
        await(TestDeregisterVatConnector.putAnswers(vrn, testKey, testModel)(VATAccountsModel.format)) shouldBe Right(DeregisterVatSuccess)
      }
    }

    s"successfully calling .deleteAnswers" should {

      "return a DeregisterVatResponse model" in {
        setupMockHttpDelete[VATAccountsModel](TestDeregisterVatConnector.url(vrn, testKey))(Right(DeregisterVatSuccess))
        await(TestDeregisterVatConnector.deleteAnswers(vrn, testKey)(VATAccountsModel.format)) shouldBe Right(DeregisterVatSuccess)
      }
    }
  }
}
