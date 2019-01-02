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

import mocks.MockAuth
import play.api.http.Status
import play.api.mvc.Results.Ok
import play.api.mvc.{Action, AnyContent}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.MissingBearerToken

import scala.concurrent.Future

class AuthPredicateSpec extends MockAuth {

  object TestAuthPredicate extends AuthPredicate(mockEnrolmentsAuthService, serviceErrorHandler, mockAuthoriseAsAgent, messagesApi, mockConfig)

  def target(): Action[AnyContent] = {
    TestAuthPredicate.async {
      implicit user =>
        Future.successful(Ok)
    }
  }

  "AuthPredicate" when {

    "calling .invokeBlock" when {

      "the user is an individual" when {

        "the user is enrolled to HMRC-MTD-VAT" should {

          lazy val result = target()(FakeRequest())

          "return 200" in {
            mockAuthResult(mockAuthorisedIndividual)
            status(result) shouldBe Status.OK
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
            mockAuthResult(mockUnauthorisedAgent, isAgent = true)
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
