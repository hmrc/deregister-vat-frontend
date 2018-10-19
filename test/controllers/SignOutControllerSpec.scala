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
import play.api.mvc.Result
import play.api.test.Helpers._
import services.mocks.MockDeleteAllStoredAnswersService

import scala.concurrent.Future

class SignOutControllerSpec extends ControllerBaseSpec with MockDeleteAllStoredAnswersService {


  object TestSignOutController extends SignOutController(
    messagesApi,
    mockAuthPredicate,
    mockDeleteAllStoredAnswersService,
    serviceErrorHandler,
    mockConfig
  )

  "navigating to signout page" when {

    "authorised" should {
      "return 303 and navigate to the survey url" in {
        lazy val result: Future[Result] = TestSignOutController.signOut(authorised = true)(request)

        setupMockDeleteAllStoredAnswers(Right(DeregisterVatSuccess))
        mockAuthResult(Future.successful(mockAuthorisedIndividual))

        status(result) shouldBe SEE_OTHER
        redirectLocation(result) shouldBe Some(mockConfig.signOutUrl)
      }
    }

    "unauthorised" should {
      "return 303 and navigate to sign out url" in {
        lazy val result: Future[Result] = TestSignOutController.signOut(authorised = false)(request)

        setupMockDeleteAllStoredAnswers(Right(DeregisterVatSuccess))
        mockAuthResult(Future.successful(mockAuthorisedIndividual))

        status(result) shouldBe SEE_OTHER
        redirectLocation(result) shouldBe Some(mockConfig.unauthorisedSignOutUrl)
      }
    }

    "there is an error deleting the answers" should {
      "throw an internal server error" in {
        lazy val result: Future[Result] = TestSignOutController.signOut(authorised = true)(request)

        setupMockDeleteAllStoredAnswers(Left(ErrorModel(INTERNAL_SERVER_ERROR,"bad things")))
        mockAuthResult(Future.successful(mockAuthorisedIndividual))

        status(result) shouldBe INTERNAL_SERVER_ERROR
      }
    }
  }

  "signing out on timeout" when {

    "deleting answers is successful" should {

      "return 303 and navigate to the expected sign out url" in {
        lazy val result: Future[Result] = TestSignOutController.timeout(request)

        setupMockDeleteAllStoredAnswers(Right(DeregisterVatSuccess))
        mockAuthResult(Future.successful(mockAuthorisedIndividual))

        status(result) shouldBe SEE_OTHER
        redirectLocation(result) shouldBe Some(mockConfig.unauthorisedSignOutUrl)
      }
    }

    "deleting answers is unsuccessful" should {

      "throw an internal server error" in {
        lazy val result: Future[Result] = TestSignOutController.timeout(request)

        setupMockDeleteAllStoredAnswers(Left(ErrorModel(INTERNAL_SERVER_ERROR,"bad things")))
        mockAuthResult(Future.successful(mockAuthorisedIndividual))

        status(result) shouldBe INTERNAL_SERVER_ERROR
      }
    }
  }
}
