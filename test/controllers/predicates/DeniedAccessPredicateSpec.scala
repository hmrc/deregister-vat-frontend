/*
 * Copyright 2021 HM Revenue & Customs
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
import assets.constants.CustomerDetailsTestConstants._
import common.{Constants, SessionKeys}
import mocks.MockAuth
import models.{ErrorModel, User}
import org.scalatest.concurrent.ScalaFutures.convertScalaFuture
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.mocks.MockCustomerDetailsService

import scala.concurrent.Future

class DeniedAccessPredicateSpec extends MockAuth with MockCustomerDetailsService {

  "RegistrationStatusPredicate" when {

    val mockRegStatusPredicate: DeniedAccessPredicate =
      new DeniedAccessPredicate(
        mockCustomerDetailsService,
        serviceErrorHandler,
        mcc,
        messagesApi,
        mockConfig
      )

    "there is a registration status in session" when {

      "session key is set to pending" when {

        lazy val fakeRequest = FakeRequest().withSession(
          SessionKeys.registrationStatus -> Constants.pending
        )

        "the user is a principal entity" should {
          lazy val result = {
            Future.successful(mockRegStatusPredicate.refine(User(vrn)(fakeRequest)).futureValue.left.get)
          }

          "return SEE_OTHER" in {
            status(result) shouldBe SEE_OTHER
          }

          s"redirect to ${mockConfig.vatSummaryFrontendUrl}" in {
            redirectLocation(result) shouldBe Some(mockConfig.vatSummaryFrontendUrl)
          }
        }

        "the user is an agent" should {
          lazy val result = {
            Future.successful(mockRegStatusPredicate.refine(User(vrn, arn = Some("arn"))(fakeRequest)).futureValue.left.get)
          }

          "return SEE_OTHER" in {
            status(result) shouldBe SEE_OTHER
          }

          s"redirect to ${mockConfig.agentClientLookupAgentHubPath}" in {
            redirectLocation(result) shouldBe Some(mockConfig.agentClientLookupAgentHubPath)
          }
        }
      }

      "session key is set to deregistered" when {

        lazy val fakeRequest = FakeRequest().withSession(
          SessionKeys.registrationStatus -> Constants.deregistered
        )

        "the user is a principal entity" should {

          lazy val result = {
            Future.successful(mockRegStatusPredicate.refine(User(vrn)(fakeRequest)).futureValue.left.get)
          }

          "return SEE_OTHER" in {
            status(result) shouldBe SEE_OTHER
          }

          s"redirect to ${mockConfig.vatSummaryFrontendUrl}" in {
            redirectLocation(result) shouldBe Some(mockConfig.vatSummaryFrontendUrl)
          }
        }

        "the user is an agent" should {
          lazy val result = {
            Future.successful(mockRegStatusPredicate.refine(User(vrn, arn = Some("arn"))(fakeRequest)).futureValue.left.get)
          }

          "return SEE_OTHER" in {
            status(result) shouldBe SEE_OTHER
          }

          s"redirect to ${mockConfig.agentClientLookupAgentHubPath}" in {
            redirectLocation(result) shouldBe Some(mockConfig.agentClientLookupAgentHubPath)
          }
        }
      }

      "session key is set to registered" should {

        lazy val fakeRequest = FakeRequest().withSession(
          SessionKeys.registrationStatus -> Constants.registered
        )

        lazy val result = {
          mockRegStatusPredicate.refine(User(vrn)(fakeRequest)).futureValue
        }

        "allow the request through" in {
          result shouldBe Right(user)
        }
      }
    }

    "there is no 'registrationStatus' in session" when {

      "the call to customer details is successful" when {

        "the user is has a party type of VAT Group (Z2)" should {

          "the user is a principal entity" should {

            lazy val result = {
              setupMockCustomerDetails(vrn)(Right(customerDetailsVatGroup))
              Future.successful(mockRegStatusPredicate.refine(User(vrn)(request)).futureValue.left.get)
            }

            "return SEE_OTHER" in {
              status(result) shouldBe SEE_OTHER
            }

            s"redirect to ${mockConfig.vatSummaryFrontendUrl}" in {
              redirectLocation(result) shouldBe Some(mockConfig.vatSummaryFrontendUrl)
            }
          }

          "the user is an agent" should {

            lazy val result = {
              setupMockCustomerDetails(vrn)(Right(customerDetailsVatGroup))
              Future.successful(mockRegStatusPredicate.refine(User(vrn, arn = Some("arn"))(request)).futureValue.left.get)
            }

            "return SEE_OTHER" in {
              status(result) shouldBe SEE_OTHER
            }

            s"redirect to ${mockConfig.agentClientLookupAgentHubPath}" in {
              redirectLocation(result) shouldBe Some(mockConfig.agentClientLookupAgentHubPath)
            }
          }

        }

        "there is a pending dereg change" when {

          "the user is a principal entity" should {

            lazy val result = {
              setupMockCustomerDetails(vrn)(Right(customerDetailsPendingDereg))
              Future.successful(mockRegStatusPredicate.refine(User(vrn)(request)).futureValue.left.get)
            }

            "add 'registrationStatus' = pending to session" in {
              session(result).get(SessionKeys.registrationStatus) shouldBe Some(Constants.pending)
            }

            "return SEE_OTHER" in {
              status(result) shouldBe SEE_OTHER
            }

            s"redirect to ${mockConfig.vatSummaryFrontendUrl}" in {
              redirectLocation(result) shouldBe Some(mockConfig.vatSummaryFrontendUrl)
            }
          }

          "the user is an agent" should {

            lazy val result = {
              setupMockCustomerDetails(vrn)(Right(customerDetailsPendingDereg))
              Future.successful(mockRegStatusPredicate.refine(User(vrn, arn = Some("arn"))(request)).futureValue.left.get)
            }

            "add 'registrationStatus' = pending to session" in {
              session(result).get(SessionKeys.registrationStatus) shouldBe Some(Constants.pending)
            }

            "return SEE_OTHER" in {
              status(result) shouldBe SEE_OTHER
            }

            s"redirect to ${mockConfig.agentClientLookupAgentHubPath}" in {
              redirectLocation(result) shouldBe Some(mockConfig.agentClientLookupAgentHubPath)
            }
          }
        }

        "the user has already deregistered" when {

          "the user is a principal entity" should {

            lazy val result = {
              setupMockCustomerDetails(vrn)(Right(customerDetailsAlreadyDeregistered))
              Future.successful(mockRegStatusPredicate.refine(User(vrn)(request)).futureValue.left.get)
            }

            "add 'registrationStatus' = deregistered to session" in {
              session(result).get(SessionKeys.registrationStatus) shouldBe Some(Constants.deregistered)
            }

            "return SEE_OTHER" in {
              status(result) shouldBe SEE_OTHER
            }

            s"redirect to ${mockConfig.vatSummaryFrontendUrl}" in {
              redirectLocation(result) shouldBe Some(mockConfig.vatSummaryFrontendUrl)
            }
          }

          "the user is an agent" should {

            lazy val result = {
              setupMockCustomerDetails(vrn)(Right(customerDetailsAlreadyDeregistered))
              Future.successful(mockRegStatusPredicate.refine(User(vrn, arn = Some("arn"))(request)).futureValue.left.get)
            }

            "add 'registrationStatus' = deregistered to session" in {
              session(result).get(SessionKeys.registrationStatus) shouldBe Some(Constants.deregistered)
            }

            "return SEE_OTHER" in {
              status(result) shouldBe SEE_OTHER
            }

            s"redirect to ${mockConfig.agentClientLookupAgentHubPath}" in {
              redirectLocation(result) shouldBe Some(mockConfig.agentClientLookupAgentHubPath)
            }
          }
        }

        "the user has no pending dereg change" should {

          lazy val result = {
            setupMockCustomerDetails(vrn)(Right(customerDetailsMax))
            Future.successful(mockRegStatusPredicate.refine(User(vrn)(request)).futureValue.left.get)
          }

          "add 'registrationStatus' = registered to session" in {
            session(result).get(SessionKeys.registrationStatus) shouldBe Some(Constants.registered)
          }

          "return SEE_OTHER" in {
            status(result) shouldBe SEE_OTHER
          }

          s"redirect to ${controllers.routes.DeregisterForVATController.show().url}" in {
            redirectLocation(result) shouldBe Some(controllers.routes.DeregisterForVATController.show().url)
          }
        }
      }

      "the call to customer details is unsuccessful" should {

        lazy val result = {
          setupMockCustomerDetails(vrn)(Left(ErrorModel(1, "")))
          Future.successful(mockRegStatusPredicate.refine(User(vrn)(request)).futureValue.left.get)
        }

        "return INTERNAL_SERVER_ERROR" in {
          status(result) shouldBe INTERNAL_SERVER_ERROR
        }
      }
    }
  }
}
