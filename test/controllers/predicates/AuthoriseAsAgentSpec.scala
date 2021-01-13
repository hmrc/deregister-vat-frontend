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

import mocks.MockAuth
import play.api.http.Status
import play.api.mvc.Results.Ok
import play.api.mvc.{Action, AnyContent}
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.Retrieval
import uk.gov.hmrc.http.HeaderCarrier
import utils.TestUtil

import scala.concurrent.{ExecutionContext, Future}

class AuthoriseAsAgentSpec extends TestUtil with MockAuth {

  def mockAuth(authResponse: Future[Enrolments]): Unit = {
    (mockAuthConnector.authorise(_: Predicate, _: Retrieval[_])(_: HeaderCarrier, _: ExecutionContext))
      .expects(*, *, *, *)
      .returns(authResponse)
  }

  def target(): Action[AnyContent] = {
    mockAuthoriseAsAgent.async {
        Future.successful(Ok)
    }
  }

  "AuthoriseAsAgent" when {

    "calling .invokeBlock" when {

      "the Agent is enrolled to HMRC-AS-AGENT" when {

        "the Agent has delegated authority for the client" should {

          val authResponse = Future.successful(
            Enrolments(
              Set(
                Enrolment(
                  "HMRC-AS-AGENT",
                  Seq(EnrolmentIdentifier("AgentReferenceNumber", "XAIT0000000000")),
                  "Activated",
                  Some("mtd-vat-auth")
                )
              )
            )
          )

          "return 200" in {
            mockAuth(authResponse)
            val result = target()(requestWithVRN)
            status(result) shouldBe Status.OK
          }
        }

        "the Agent does not have delegated authority for the client" when {

          val authResponse = Future.failed(InsufficientEnrolments())
          lazy val result = target()(requestWithVRN)

          "return 303" in {
            mockAuth(authResponse)
            status(result) shouldBe Status.SEE_OTHER
          }

          "redirect to the VAT Agent Client Lookup Unauthorised view" in {
            redirectLocation(result) shouldBe Some(mockConfig.agentClientUnauthorisedUrl)
          }
        }

        "there is no Client VRN in session" should {

          lazy val result = target()(request)

          "return 303" in {
            mockConfig.features.stubAgentClientLookup(true)
            status(result) shouldBe Status.SEE_OTHER
          }

          "redirect to the test only stubbed endpoint" in {
            redirectLocation(result) shouldBe Some(mockConfig.agentClientLookupUrl)
          }
        }

      }

      "the Agent is not enrolled to HMRC-AS-AGENT" should {

        val authResponse = Future.successful(
          Enrolments(
            Set(
              Enrolment("OTHER_ENROLMENT",
                Seq(EnrolmentIdentifier("", "")),
                "Activated"
              )
            )
          )
        )
        lazy val result = target()(requestWithVRN)

        "return 303" in {
          mockAuth(authResponse)
          status(result) shouldBe Status.SEE_OTHER
        }

        "redirect to the VAT Agent Client Lookup Unauthorised view" in {
          redirectLocation(result) shouldBe Some(mockConfig.agentClientUnauthorisedUrl)
        }
      }

      "there is no active session" should {

        lazy val result = target()(requestWithVRN)

        "return 303" in {
          mockAuth(Future.failed(MissingBearerToken()))
          status(result) shouldBe Status.SEE_OTHER
        }

        s"redirect to ${mockConfig.signInUrl}" in {
          redirectLocation(result) shouldBe Some(mockConfig.signInUrl)
        }
      }
    }
  }
}