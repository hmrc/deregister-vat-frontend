/*
 * Copyright 2019 HM Revenue & Customs
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
import common.SessionKeys
import mocks.MockAuth
import models.{ErrorModel, User}
import play.api.test.FakeRequest
import services.mocks.MockCustomerDetailsService
import play.api.test.Helpers._
import assets.constants.CustomerDetailsTestConstants._

class PendingChangesPredicateSpec extends MockAuth with MockCustomerDetailsService {

  "PendingChangesPredicate" when {

    val mockPendingChangesPredicate: PendingChangesPredicate =
      new PendingChangesPredicate(
        mockCustomerDetailsService,
        serviceErrorHandler,
        messagesApi,
        mockConfig,
        ec
      )

    "user has 'pendingDeregistration' in session" when {

      "session key is set to true" should {

        lazy val fakeRequest = FakeRequest().withSession(
          SessionKeys.pendingDeregKey -> "true"
        )

        lazy val result = {
          await(mockPendingChangesPredicate.refine(User(vrn)(fakeRequest))).left.get
        }

        "return SEE_OTHER" in {
          status(result) shouldBe SEE_OTHER
        }

        s"redirect to ${mockConfig.manageVatSubscriptionFrontendUrl}" in {
          redirectLocation(result) shouldBe Some(mockConfig.manageVatSubscriptionFrontendUrl)
        }
      }

      "session key is set to false" should {

        lazy val fakeRequest = FakeRequest().withSession(
          SessionKeys.pendingDeregKey -> "false"
        )

        lazy val result = {
          await(mockPendingChangesPredicate.refine(User(vrn)(fakeRequest)))
        }

        "allow the request through" in {
          result shouldBe Right(user)
        }
      }

      "session key is invalid" should {

        lazy val fakeRequest = FakeRequest().withSession(
          SessionKeys.pendingDeregKey -> "error"
        )

        lazy val result = {
          await(mockPendingChangesPredicate.refine(User(vrn)(fakeRequest))).left.get
        }

        "return INTERNAL_SERVER_ERROR" in {
          status(result) shouldBe INTERNAL_SERVER_ERROR
        }
      }
    }

    "user has no 'pendingDeregistration' in session" when {

      "call to customer details is successful" when {

        "user has a pending dereg change" should {

          lazy val result = {
            setupMockPendingDereg(vrn)(Right(pendingDeregTrue))
            await(mockPendingChangesPredicate.refine(User(vrn)(request))).left.get
          }

          "add 'pendingDeregKey' = true to session" in {
            session(result).get(SessionKeys.pendingDeregKey) shouldBe Some("true")
          }

          "return SEE_OTHER" in {
            status(result) shouldBe SEE_OTHER
          }

          s"redirect to ${mockConfig.manageVatSubscriptionFrontendUrl}" in {
            redirectLocation(result) shouldBe Some(mockConfig.manageVatSubscriptionFrontendUrl)
          }
        }

        "user has no pending dereg change" should {

          lazy val result = {
            setupMockPendingDereg(vrn)(Right(pendingDeregFalse))
            await(mockPendingChangesPredicate.refine(User(vrn)(request))).left.get
          }

          "add 'pendingDeregKey' = false to session" in {
            session(result).get(SessionKeys.pendingDeregKey) shouldBe Some("false")
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
            await(mockPendingChangesPredicate.refine(User(vrn)(request))).left.get
          }

          "add 'pendingDeregKey' = false to session" in {
            session(result).get(SessionKeys.pendingDeregKey) shouldBe Some("false")
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
          await(mockPendingChangesPredicate.refine(User(vrn)(request))).left.get
        }

        "return INTERNAL_SERVER_ERROR" in {
          status(result) shouldBe INTERNAL_SERVER_ERROR
        }
      }
    }
  }
}
