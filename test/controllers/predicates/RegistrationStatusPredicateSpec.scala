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

package controllers.predicates

import assets.constants.BaseTestConstants.vrn
import common.{Constants, SessionKeys}
import mocks.MockAuth
import models.{ErrorModel, User}
import play.api.test.FakeRequest
import services.mocks.MockCustomerDetailsService
import play.api.test.Helpers._
import assets.constants.CustomerDetailsTestConstants._

class RegistrationStatusPredicateSpec extends MockAuth with MockCustomerDetailsService {

  "RegistrationStatusPredicate" when {

    val mockRegStatusPredicate: RegistrationStatusPredicate =
      new RegistrationStatusPredicate(
        mockCustomerDetailsService,
        serviceErrorHandler,
        messagesApi,
        mockConfig,
        ec
      )

    "user has 'registrationStatus' in session" when {

      "session key is set to pending" should {

        lazy val fakeRequest = FakeRequest().withSession(
          SessionKeys.registrationStatusKey -> Constants.pending
        )

        lazy val result = {
          await(mockRegStatusPredicate.refine(User(vrn)(fakeRequest))).left.get
        }

        "return SEE_OTHER" in {
          status(result) shouldBe SEE_OTHER
        }

        s"redirect to ${mockConfig.vatSummaryFrontendUrl}" in {
          redirectLocation(result) shouldBe Some(mockConfig.vatSummaryFrontendUrl)
        }
      }

      "session key is set to registered" should {

        lazy val fakeRequest = FakeRequest().withSession(
          SessionKeys.registrationStatusKey -> Constants.registered
        )

        lazy val result = {
          await(mockRegStatusPredicate.refine(User(vrn)(fakeRequest)))
        }

        "allow the request through" in {
          result shouldBe Right(user)
        }
      }

      "session key is invalid" should {

        lazy val fakeRequest = FakeRequest().withSession(
          SessionKeys.registrationStatusKey -> "error"
        )

        lazy val result = {
          await(mockRegStatusPredicate.refine(User(vrn)(fakeRequest))).left.get
        }

        "return INTERNAL_SERVER_ERROR" in {
          status(result) shouldBe INTERNAL_SERVER_ERROR
        }
      }
    }

    "user has no 'registrationStatus' in session" when {

      "call to customer details is successful" when {

        "user has a pending dereg change" should {

          lazy val result = {
            setupMockPendingDereg(vrn)(Right(pendingDeregTrue))
            await(mockRegStatusPredicate.refine(User(vrn)(request))).left.get
          }

          "add 'registrationStatus' = pending to session" in {
            session(result).get(SessionKeys.registrationStatusKey) shouldBe Some(Constants.pending)
          }

          "return SEE_OTHER" in {
            status(result) shouldBe SEE_OTHER
          }

          s"redirect to ${mockConfig.vatSummaryFrontendUrl}" in {
            redirectLocation(result) shouldBe Some(mockConfig.vatSummaryFrontendUrl)
          }
        }

        "user is an agent and has a pending dereg change" should {

          lazy val result = {
            setupMockPendingDereg(vrn)(Right(pendingDeregTrue))
            await(mockRegStatusPredicate.refine(User(vrn, arn = Some("arn"))(request))).left.get
          }

          "add 'registrationStatus' = pending to session" in {
            session(result).get(SessionKeys.registrationStatusKey) shouldBe Some(Constants.pending)
          }

          "return SEE_OTHER" in {
            status(result) shouldBe SEE_OTHER
          }

          s"redirect to ${mockConfig.agentClientLookupAgentHubPath}" in {
            redirectLocation(result) shouldBe Some(mockConfig.agentClientLookupAgentHubPath)
          }
        }

        "user has no pending dereg change" should {

          lazy val result = {
            setupMockPendingDereg(vrn)(Right(pendingDeregFalse))
            await(mockRegStatusPredicate.refine(User(vrn)(request))).left.get
          }

          "add 'registrationStatus' = registered to session" in {
            session(result).get(SessionKeys.registrationStatusKey) shouldBe Some(Constants.registered)
          }

          "return SEE_OTHER" in {
            status(result) shouldBe SEE_OTHER
          }

          s"redirect to ${controllers.routes.DeregisterForVATController.redirect().url}" in {
            redirectLocation(result) shouldBe Some(controllers.routes.DeregisterForVATController.redirect().url)
          }
        }

        "no in-flight data is returned" should {

          lazy val result = {
            setupMockPendingDereg(vrn)(Right(noPendingDereg))
            await(mockRegStatusPredicate.refine(User(vrn)(request))).left.get
          }

          "add 'registrationStatus' = registered to session" in {
            session(result).get(SessionKeys.registrationStatusKey) shouldBe Some(Constants.registered)
          }

          "return SEE_OTHER" in {
            status(result) shouldBe SEE_OTHER
          }

          s"redirect to ${controllers.routes.DeregisterForVATController.redirect().url}" in {
            redirectLocation(result) shouldBe Some(controllers.routes.DeregisterForVATController.redirect().url)
          }
        }
      }

      "call to customer details is unsuccessful" should {

        lazy val result = {
          setupMockPendingDereg(vrn)(Left(ErrorModel(1, "")))
          await(mockRegStatusPredicate.refine(User(vrn)(request))).left.get
        }

        "return INTERNAL_SERVER_ERROR" in {
          status(result) shouldBe INTERNAL_SERVER_ERROR
        }
      }
    }
  }
}
