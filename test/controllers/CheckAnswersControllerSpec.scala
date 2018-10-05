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

package controllers

import assets.constants.CheckYourAnswersTestConstants._
import models._
import play.api.http.Status
import play.api.test.Helpers.{contentType, _}
import services.mocks.MockCheckAnswersService

import scala.concurrent.Future

class CheckAnswersControllerSpec extends ControllerBaseSpec with MockCheckAnswersService {

  object TestCheckAnswersController extends CheckAnswersController(messagesApi, mockAuthPredicate, mockCheckAnswersService, mockConfig)

  "the user is authorised" when {

    "Calling the .show action" when {

      "a Right(CheckYourAnswersModel) is returned" should {

        lazy val result = TestCheckAnswersController.show()(request)

        "return 200 (OK)" in {
          setupMockCheckYourAnswersModel(Right(
            CheckYourAnswersModel(
              Some(Ceased),
              Some(dateModel),
              Some(Yes),
              Some(taxableTurnoverBelow),
              Some(whyTurnoverBelowAll),
              Some(StandardAccounting),
              Some(yesNoAmountNo),
              Some(yesNoAmountNo),
              Some(yesNoAmountNo),
              Some(Yes),
              Some(Yes),
              Some(deregistrationDate)
            )
          ))
          mockAuthResult(Future.successful(mockAuthorisedIndividual))
          status(result) shouldBe Status.OK
        }

        "return HTML" in {
          contentType(result) shouldBe Some("text/html")
          charset(result) shouldBe Some("utf-8")
        }

        "display the back url" in {
          document(result).getElementsByClass("link-back").attr("href") shouldBe controllers.routes.DeregistrationDateController.show().url

        }
      }


      "No deregistration date for DeregDateAnswerService is returned" should {

        lazy val result = TestCheckAnswersController.show()(request)

        "return 200 (OK)" in {
          setupMockCheckYourAnswersModel(Right(
            CheckYourAnswersModel(
              Some(Ceased),
              Some(dateModel),
              Some(Yes),
              Some(taxableTurnoverBelow),
              Some(whyTurnoverBelowAll),
              Some(StandardAccounting),
              Some(yesNoAmountNo),
              Some(yesNoAmountNo),
              Some(yesNoAmountNo),
              Some(Yes),
              Some(Yes),
              None
            )
          ))
          mockAuthResult(Future.successful(mockAuthorisedIndividual))
          status(result) shouldBe Status.OK
        }

        "return HTML" in {
          contentType(result) shouldBe Some("text/html")
          charset(result) shouldBe Some("utf-8")
        }

        "display the back url" in {
          document(result).getElementsByClass("link-back").attr("href") shouldBe controllers.routes.OutstandingInvoicesController.show().url
        }
      }

      "a Left(ErrorModel) is returned" should {

        lazy val result = TestCheckAnswersController.show()(request)

        "return 500 (INTERNAL SERVER ERROR)" in {
          setupMockCheckYourAnswersModel(Left(ErrorModel(INTERNAL_SERVER_ERROR,"Error")))
          mockAuthResult(Future.successful(mockAuthorisedIndividual))
          status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        }
      }

      authChecks(".show", TestCheckAnswersController.show(), request)
    }
  }
}
