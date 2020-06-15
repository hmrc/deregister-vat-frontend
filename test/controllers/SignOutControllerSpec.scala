/*
 * Copyright 2020 HM Revenue & Customs
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

import models.{DeregisterVatSuccess, ErrorModel, User}
import play.api.mvc.{AnyContentAsEmpty, Result}
import play.api.test.Helpers._
import services.mocks.MockDeleteAllStoredAnswersService

import scala.concurrent.Future

class SignOutControllerSpec extends ControllerBaseSpec with MockDeleteAllStoredAnswersService {

  object TestSignOutController extends SignOutController(
    mcc,
    mockAuthPredicate,
    mockDeleteAllStoredAnswersService,
    ec,
    serviceErrorHandler
  )

  "Calling .signout" when {

    "the user is authorised" when {

      "the user is an agent" should {

        lazy val result: Future[Result] = {
          implicit val user: User[AnyContentAsEmpty.type] = agentUserPrefYes
          setupMockDeleteAllStoredAnswers(Right(DeregisterVatSuccess))
          mockAuthResult(mockAuthorisedAgent, isAgent = true)
          TestSignOutController.signOut(authorised = true)(requestWithVRN)
        }

        "return 303" in {
          status(result) shouldBe SEE_OTHER
        }

        "redirect to the correct sign out URL" in {
          redirectLocation(result) shouldBe Some(mockConfig.signOutUrl("VATCA"))
        }
      }

      "the user is a principal entity" should {

        lazy val result: Future[Result] = {
          setupMockDeleteAllStoredAnswers(Right(DeregisterVatSuccess))
          mockAuthResult(mockAuthorisedIndividual)
          TestSignOutController.signOut(authorised = true)(request)
        }

        "return 303" in {
          status(result) shouldBe SEE_OTHER
        }

        "redirect to the correct sign out URL" in {
          redirectLocation(result) shouldBe Some(mockConfig.signOutUrl("VATC"))
        }
      }
    }

    "the user is unauthorised" should {

      lazy val result: Future[Result] = {
        setupMockDeleteAllStoredAnswers(Right(DeregisterVatSuccess))
        mockAuthResult(mockAuthorisedIndividual)
        TestSignOutController.signOut(authorised = false)(request)
      }

      "return 303" in {
        status(result) shouldBe SEE_OTHER
      }

      "redirect to the unauthorised sign out URL" in {
        redirectLocation(result) shouldBe Some(mockConfig.unauthorisedSignOutUrl)
      }
    }

    "there is an error deleting the answers" should {

      "throw an internal server error" in {

        lazy val result: Future[Result] = {
          setupMockDeleteAllStoredAnswers(Left(ErrorModel(INTERNAL_SERVER_ERROR, "bad things")))
          mockAuthResult(mockAuthorisedIndividual)
          TestSignOutController.signOut(authorised = true)(request)
        }

        status(result) shouldBe INTERNAL_SERVER_ERROR
      }
    }
  }

  "Calling .timeout" should {

    lazy val result: Future[Result] = TestSignOutController.timeout(request)

    "return 303" in {
      status(result) shouldBe SEE_OTHER
    }

    "redirect to the unauthorised sign out URL" in {
      redirectLocation(result) shouldBe Some(mockConfig.unauthorisedSignOutUrl)
    }
  }
}
