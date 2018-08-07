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

package controllers.predicates

import assets.mocks.MockAuth
import play.api.http.Status
import play.api.mvc.{Action, AnyContent}
import play.api.mvc.Results.Ok
import play.api.test.FakeRequest
import uk.gov.hmrc.auth.core.MissingBearerToken

import scala.concurrent.Future

class AuthPredicateSpec extends MockAuth {

  def target(predicate: AuthPredicate): Action[AnyContent] = {
    predicate.async {
      implicit user =>
        Future.successful(Ok)
    }
  }

  "AuthPredicate" when {

    "calling .invokeBlock" when {

      "the user is an individual" when {

        "the user is enrolled to HMRC-MTD-VAT" should {
          lazy val predicate = setup(mockAuthorisedIndividual)
          lazy val result = target(predicate)(FakeRequest())

          "return 200" in {
            status(result) shouldBe Status.OK
          }

          "the user is not enrolled to HMRC-MTD-VAT" should {
            lazy val predicate = setup(mockUnauthorisedIndividual)
            lazy val result = target(predicate)(FakeRequest())

            "return 403" in {
              status(result) shouldBe Status.FORBIDDEN
            }
          }
        }

        "the user is an agent" when {

          "the user is enrolled to HMRC-AS-AGENT and has delegated authority for the client" should {

            lazy val predicate = setup(mockAuthorisedAgent, isAgent = true)
            lazy val result = target(predicate)(FakeRequest())

            "return 200" in {
              status(result) shouldBe Status.OK
            }
          }
        }

        "there is no session" should {
          lazy val predicate = setup(Future.failed(MissingBearerToken()))
          lazy val result = target(predicate)(FakeRequest())

          "return 401" in {
            status(result) shouldBe Status.UNAUTHORIZED
          }
        }

        "user has no affinity group" should {
          lazy val predicate = setup(mockNoAffinityGroup)
          lazy val result = target(predicate)(FakeRequest())

          "return 500" in {
            status(result) shouldBe Status.INTERNAL_SERVER_ERROR
          }
        }
      }
    }
  }
}
