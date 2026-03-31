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

import assets.constants.BaseTestConstants.{errorModel, vrn}
import connectors.mocks.MockDeregisterVatConnector
import models.{DeregisterVatSuccess, NumberInputModel}
import org.scalatest.concurrent.ScalaFutures.convertScalaFuture
import utils.TestUtil

import scala.concurrent.Future

class OptionTaxValueAnswerServiceSpec extends TestUtil with MockDeregisterVatConnector {

  object OptionTaxValueAnswerService extends OptionTaxValueAnswerService(mockDeregisterVatConnector)

  ".answerKey" should {
    "return the key 'ottNotification'" in {
      OptionTaxValueAnswerService.answerKey shouldBe "optionTaxValue"
    }
  }

  ".getAnswer" when {
    "a success response is returned from the connector" should {
      "return the expected model" in {
        setupMockGetAnswers(vrn, "optionTaxValue")(Future.successful(Right(Some(NumberInputModel(BigDecimal(1))))))
        OptionTaxValueAnswerService.getAnswer.futureValue shouldBe Right(Some(NumberInputModel(BigDecimal(1))))
      }
    }

    "an error response is returned from the connector" should {
      "return the expected error model" in {
        setupMockGetAnswers(vrn, "optionTaxValue")(Future.successful(Left(errorModel)))
        OptionTaxValueAnswerService.getAnswer.futureValue shouldBe Left(errorModel)
      }
    }
  }

  ".storeAnswer" when {
    "a success response is returned from the connector" should {
      "return the expected model" in {
        setupMockStoreAnswers(vrn, "optionTaxValue", NumberInputModel(BigDecimal(1)))(Future.successful(Right(DeregisterVatSuccess)))
        OptionTaxValueAnswerService.storeAnswer(NumberInputModel(BigDecimal(1))).futureValue shouldBe Right(DeregisterVatSuccess)
      }
    }

    "an error response is returned from the connector" should {
      "return the expected error model" in {
        setupMockStoreAnswers(vrn, "optionTaxValue", NumberInputModel(BigDecimal(1)))(Future.successful(Left(errorModel)))
        OptionTaxValueAnswerService.storeAnswer(NumberInputModel(BigDecimal(1))).futureValue shouldBe Left(errorModel)
      }
    }
  }

  ".deleteAnswer" when {
    "a success response is returned from the connector" should {
      "return the expected model" in {
        setupMockDeleteAnswer(vrn, "optionTaxValue")(Future.successful(Right(DeregisterVatSuccess)))
        OptionTaxValueAnswerService.deleteAnswer.futureValue shouldBe Right(DeregisterVatSuccess)
      }
    }

    "an error response is returned from the connector" should {
      "return the expected error model" in {
        setupMockDeleteAnswer(vrn, "optionTaxValue")(Future.successful(Left(errorModel)))
        OptionTaxValueAnswerService.deleteAnswer.futureValue shouldBe Left(errorModel)
      }
    }
  }

}
