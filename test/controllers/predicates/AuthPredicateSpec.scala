/*
 * Copyright 2024 HM Revenue & Customs
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
import common.SessionKeys
import mocks.MockAuth
import models.ErrorModel
import play.api.http.Status
import play.api.mvc.Results.Ok
import play.api.mvc.{Action, AnyContent}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.MissingBearerToken
import views.html.errors.client.Unauthorised

import scala.concurrent.Future

class AuthPredicateSpec extends MockAuth {

  override lazy val unauthorised: Unauthorised = injector.instanceOf[Unauthorised]

  object TestAuthPredicate extends AuthPredicate(
    unauthorised, insolventError, mockEnrolmentsAuthService, mockCustomerDetailsService, serviceErrorHandler, mockAuthoriseAsAgent, mcc)(mockConfig)

  def target: Action[AnyContent] = {
    TestAuthPredicate.async {
        Future.successful(Ok)
    }
  }

  "AuthPredicate" when {

    "calling .invokeBlock" when {

      "the user is an individual" when {

        "the user is enrolled to HMRC-MTD-VAT" when {

          "they have a value in session for their insolvency status" when {

            "the value is 'true' (insolvent user not continuing to trade)" should {

              "return Forbidden (403)" in {
                mockAuthResult(mockAuthorisedIndividual)
                status(target(insolventRequest)) shouldBe Status.FORBIDDEN
              }
            }

            "the value is 'false' (user permitted to trade)" should {

              "return OK (200)" in {
                mockAuthResult(mockAuthorisedIndividual)
                status(target(request)) shouldBe Status.OK
              }
            }
          }

          "they do not have a value in session for their insolvency status" when {

            "they are insolvent and not continuing to trade" should {

              lazy val result = {
                mockAuthResult(mockAuthorisedIndividual)
                setupMockCustomerDetails(vrn)(Right(customerDetailsInsolvent))
                target(FakeRequest())
              }

              "return Forbidden (403)" in {
                status(result) shouldBe Status.FORBIDDEN
              }

              "add the insolvent flag to the session" in {
                session(result).get(SessionKeys.insolventWithoutAccessKey) shouldBe Some("true")
              }
            }

            "they are permitted to trade" should {

              lazy val result = {
                mockAuthResult(mockAuthorisedIndividual)
                setupMockCustomerDetails(vrn)(Right(customerDetailsMax))
                target(FakeRequest())
              }

              "return OK (200)" in {
                status(result) shouldBe Status.OK
              }

              "add the insolvent flag to the session" in {
                session(result).get(SessionKeys.insolventWithoutAccessKey) shouldBe Some("false")
              }
            }

            "there is an error returned from the customer information API" should {

              lazy val result = {
                mockAuthResult(mockAuthorisedIndividual)
                setupMockCustomerDetails(vrn)(Left(ErrorModel(Status.INTERNAL_SERVER_ERROR, "Bad things!")))
                target(FakeRequest())
              }

              "return Internal Server Error (500)" in {
                status(result) shouldBe Status.INTERNAL_SERVER_ERROR
              }
            }
          }
        }

        "the user is not enrolled to HMRC-MTD-VAT" should {

          lazy val result = target()(FakeRequest())

          "return 403" in {
            mockAuthResult(mockUnauthorisedIndividual)
            status(result) shouldBe Status.FORBIDDEN
          }
        }
      }

      "the user is an agent" when {

        "the user is enrolled to HMRC-AS-AGENT and has delegated authority for the client" should {

          lazy val result = target()(requestWithVRN)

          "return 200" in {
            mockAuthResult(mockAuthorisedAgent, isAgent = true)
            status(result) shouldBe Status.OK
          }
        }

        "the user is not enrolled to HMRC-AS-AGENT" should {

          lazy val result = target()(requestWithVRN)

          "return 303" in {
            mockAuthResult(mockUnauthorisedAgent, isAgent = true, isUnauthorisedAgent = true)
            status(result) shouldBe Status.SEE_OTHER
          }

          "redirect to Agent unauthorised" in {
            redirectLocation(result) shouldBe Some(mockConfig.agentClientUnauthorisedUrl)
          }
        }
      }

      "there is no session" should {

        lazy val result = target()(FakeRequest())

        "return 303" in {
          mockAuthResult(Future.failed(MissingBearerToken()))
          status(result) shouldBe Status.SEE_OTHER
        }
      }

      "user has no affinity group" should {

        lazy val result = target()(FakeRequest())

        "return 500" in {
          mockAuthResult(mockNoAffinityGroup)
          status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        }
      }
    }
  }
}
