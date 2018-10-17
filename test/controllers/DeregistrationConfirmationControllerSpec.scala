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

import models.{DeregisterVatSuccess, ErrorModel}
import play.api.http.Status
import play.api.test.Helpers._
import services.mocks.MockDeleteAllStoredAnswersService

import scala.concurrent.Future

class DeregistrationConfirmationControllerSpec extends ControllerBaseSpec with MockDeleteAllStoredAnswersService {

  object TestDeregistrationConfirmationController
    extends DeregistrationConfirmationController(messagesApi, mockAuthPredicate, mockDeleteAllStoredAnswersService, serviceErrorHandler, mockConfig)

  "the user is authorised" when {

    "Calling the .show action" should {

      lazy val result = TestDeregistrationConfirmationController.show()(request)

      "return 200 (OK)" in {
        setupMockDeleteAllStoredAnswers(Right(DeregisterVatSuccess))
        mockAuthResult(Future.successful(mockAuthorisedIndividual))
        status(result) shouldBe Status.OK
      }

      "return HTML" in {
        contentType(result) shouldBe Some("text/html")
        charset(result) shouldBe Some("utf-8")
      }

    }

    lazy val result2 = TestDeregistrationConfirmationController.show()(request)

    "throw an ISE if there's an error deleting the stored answers" in {
      setupMockDeleteAllStoredAnswers(Left(ErrorModel(INTERNAL_SERVER_ERROR, "bad things")))
      mockAuthResult(Future.successful(mockAuthorisedIndividual))
      status(result2) shouldBe Status.INTERNAL_SERVER_ERROR
    }
  }

}
