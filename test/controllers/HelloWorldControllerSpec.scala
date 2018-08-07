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

import assets.mocks.MockAuth
import controllers.predicates.AuthPredicate
import play.api.http.Status
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core._

import scala.concurrent.Future

class HelloWorldControllerSpec extends MockAuth {

  private trait Test {
    val authResult: Future[_]

    def target: HelloWorldController = {
      val predicate: AuthPredicate = setup(authResult)
      new HelloWorldController(messagesApi, predicate, mockConfig)
    }
  }

  "Calling the .helloWorld action" when {

    "the user is authorised" should {

      "return 200 (OK)" in new Test {
        override val authResult: Future[_] = Future.successful(mockAuthorisedIndividual)
        private val result = target.helloWorld()(user)

        status(result) shouldBe Status.OK
      }

      "return HTML" in new Test {
        override val authResult: Future[_] = Future.successful(mockAuthorisedIndividual)
        private val result = target.helloWorld()(user)

        contentType(result) shouldBe Some("text/html")
        charset(result) shouldBe Some("utf-8")
      }
    }

    "the user is not authenticated" should {

      "return 401 (Unauthorised)" in new Test {
        override val authResult: Future[_] = Future.failed(MissingBearerToken())
        private val result = target.helloWorld()(user)

        status(result) shouldBe Status.UNAUTHORIZED
      }
    }

    "the user is not authorised" should {

      "return 403 (Forbidden)" in new Test {
        override val authResult: Future[_] = Future.failed(InsufficientEnrolments())
        private val result = target.helloWorld()(user)

        status(result) shouldBe Status.FORBIDDEN
      }
    }
  }
}