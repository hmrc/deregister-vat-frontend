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

import config.ServiceErrorHandler
import org.jsoup.Jsoup
import org.scalamock.scalatest.MockFactory
import play.api.http.Status
import play.api.mvc.Results.Ok
import play.api.mvc.{Action, AnyContent}
import play.api.test.FakeRequest
import services.EnrolmentsAuthService
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.Retrieval
import uk.gov.hmrc.http.HeaderCarrier
import utils.TestUtil

import scala.concurrent.{ExecutionContext, Future}

class AuthoriseAsAgentSpec extends TestUtil with MockFactory {

  def setup(authResponse: Future[_]): AuthoriseAsAgent = {
    val mockAuthConnector = mock[AuthConnector]

    (mockAuthConnector.authorise(_: Predicate, _: Retrieval[_])(_: HeaderCarrier, _: ExecutionContext))
      .expects(*, *, *, *)
      .returns(authResponse)

    val mockEnrolmentsAuthService: EnrolmentsAuthService = new EnrolmentsAuthService(mockAuthConnector)
    val serviceErrorHandler = injector.instanceOf[ServiceErrorHandler]
    new AuthoriseAsAgent(mockEnrolmentsAuthService, serviceErrorHandler, messagesApi, mockConfig)
  }

  def target(predicate: AuthoriseAsAgent): Action[AnyContent] = {
    predicate.async {
      implicit user =>
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

          lazy val predicate = setup(authResponse)
          lazy val result = target(predicate)(requestWithVRN)

          "return 200" in {
            status(result) shouldBe Status.OK
          }
        }

        "the Agent does not have delegated authority for the client" should {

          val authResponse = Future.failed(InsufficientEnrolments())

          lazy val predicate = setup(authResponse)
          lazy val result = target(predicate)(requestWithVRN)

          "return 401" in {
            status(result) shouldBe Status.UNAUTHORIZED
          }

          "render Unauthorised view" in {
            Jsoup.parse(bodyOf(result)).title shouldBe "You can’t use this service yet"
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

        lazy val predicate = setup(authResponse)
        lazy val result = target(predicate)(requestWithVRN)

        "return 401" in {
          status(result) shouldBe Status.UNAUTHORIZED
        }

        "render Unauthorised view" in {
          Jsoup.parse(bodyOf(result)).title shouldBe "You can’t use this service yet"
        }
      }
    }
  }
}