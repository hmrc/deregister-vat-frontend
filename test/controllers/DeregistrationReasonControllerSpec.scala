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

import play.api.http.Status
import play.api.mvc.{AnyContentAsFormUrlEncoded, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers.{contentType, _}
import services.EnrolmentsAuthService
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.Retrieval
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class DeregistrationReasonControllerSpec extends ControllerBaseSpec {

  private trait Test {
    val authResult: Future[Enrolments]
    val mockAuthConnector: AuthConnector = mock[AuthConnector]

    private def setup() {
      (mockAuthConnector.authorise(_: Predicate, _: Retrieval[Enrolments])(_: HeaderCarrier, _: ExecutionContext))
        .expects(*, *, *, *)
        .returns(authResult)
    }

    val mockAuthorisedFunctions: AuthorisedFunctions = new EnrolmentsAuthService(mockAuthConnector)

    def target: DeregistrationReasonController = {
      setup()
      new DeregistrationReasonController(messagesApi, mockAuthorisedFunctions, mockConfig)
    }
  }

  "the user is authorised" when {

    val goodEnrolments: Enrolments = Enrolments(
      Set(
        Enrolment(
          "HMRC-MTD-VAT",
          Seq(EnrolmentIdentifier("", "999999999")),
          "Active")
      )
    )

    "Calling the .show action" when {

      "the user does not have a pre selected option" should {

        "return 200 (OK)" in new Test {
          override val authResult: Future[Enrolments] = Future.successful(goodEnrolments)
          private val result = target.show()(request)

          status(result) shouldBe Status.OK
        }

        "return HTML" in new Test {
          override val authResult: Future[Enrolments] = Future.successful(goodEnrolments)
          private val result = target.show()(request)

          contentType(result) shouldBe Some("text/html")
          charset(result) shouldBe Some("utf-8")
        }
      }

      "the user is has pre selected option" should {

        "return 200 (OK)" in new Test {

          override val authResult: Future[Enrolments] = Future.successful(goodEnrolments)
          val result: Future[Result] = target.show()(request)

          status(result) shouldBe Status.OK
        }

        "return HTML" in new Test {
          override val authResult: Future[Enrolments] = Future.successful(goodEnrolments)
          val result: Future[Result] = target.show()(request)

          contentType(result) shouldBe Some("text/html")
          charset(result) shouldBe Some("utf-8")
        }
      }
    }

    "Calling the .submit action" when {

      "the user submits after selecting an 'stoppedTrading' option" should {

        lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
          FakeRequest("POST", "/").withFormUrlEncodedBody(("reason", "stoppedTrading"))

        "return 303 (SEE OTHER)" in new Test {

          override val authResult: Future[Enrolments] = Future.successful(goodEnrolments)
          private val result = target.submit()(request)

          status(result) shouldBe Status.SEE_OTHER
          redirectLocation(result) shouldBe Some(controllers.routes.HelloWorldController.helloWorld().url)
        }
      }

      "the user submits after selecting the 'turnoverBelowThreshold' option" should {

        lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
          FakeRequest("POST", "/").withFormUrlEncodedBody(("reason", "turnoverBelowThreshold"))

        "return 303 (SEE OTHER)" in new Test {

          override val authResult: Future[Enrolments] = Future.successful(goodEnrolments)
          private val result = target.submit()(request)

          status(result) shouldBe Status.SEE_OTHER
          redirectLocation(result) shouldBe Some(controllers.routes.HelloWorldController.helloWorld().url)
        }
      }

      "the user submits after selecting the 'other' option" should {

        lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
          FakeRequest("POST", "/").withFormUrlEncodedBody(("reason", "other"))

        "return 303 (SEE OTHER)" in new Test {

          override val authResult: Future[Enrolments] = Future.successful(goodEnrolments)
          private val result = target.submit()(request)

          status(result) shouldBe Status.SEE_OTHER
          redirectLocation(result) shouldBe Some(mockConfig.govUkCancelVatRegistration)
        }
      }

      "the user submits without selecting an option" should {

        lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
          FakeRequest("POST", "/").withFormUrlEncodedBody(("reason", ""))

        "return 400 (BAD REQUEST)" in new Test {
          override val authResult: Future[Enrolments] = Future.successful(goodEnrolments)
          private val result = target.submit()(request)

          status(result) shouldBe Status.BAD_REQUEST
        }

        "return HTML" in new Test {
          override val authResult: Future[Enrolments] = Future.successful(goodEnrolments)
          private val result = target.submit()(request)

          contentType(result) shouldBe Some("text/html")
          charset(result) shouldBe Some("utf-8")
        }
      }
    }
  }

  "the user is not authenticated" when {

    "Calling the .show action" when {

      "return 401 (Unauthorised)" in new Test {
        override val authResult: Future[Nothing] = Future.failed(MissingBearerToken())
        private val result = target.show()(request)

        status(result) shouldBe Status.UNAUTHORIZED
      }
    }

    "Calling the .submit action" when {

      lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
        FakeRequest("POST", "/").withFormUrlEncodedBody(("reason", ""))

      "return 401 (Unauthorised)" in new Test {
        override val authResult: Future[Nothing] = Future.failed(MissingBearerToken())
        private val result = target.submit()(request)

        status(result) shouldBe Status.UNAUTHORIZED
      }
    }
  }

  "the user is not authorised" should {

    "Calling the .show action" when {

      "return 403 (Forbidden)" in new Test {
        override val authResult: Future[Nothing] = Future.failed(InsufficientEnrolments())
        private val result = target.show()(request)

        status(result) shouldBe Status.FORBIDDEN
      }
    }

    "Calling the .submit action" when {

      lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
        FakeRequest("POST", "/").withFormUrlEncodedBody(("reason", "stoppedTrading"))

      "return 403 (Forbidden)" in new Test {
        override val authResult: Future[Nothing] = Future.failed(InsufficientEnrolments())
        private val result = target.submit()(request)

        status(result) shouldBe Status.FORBIDDEN
      }
    }
  }
}