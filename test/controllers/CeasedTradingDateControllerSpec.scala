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
import play.api.mvc.AnyContentAsFormUrlEncoded
import play.api.test.FakeRequest
import play.api.test.Helpers.{contentType, _}

import scala.concurrent.Future

class CeasedTradingDateControllerSpec extends ControllerBaseSpec {

  object TestCeasedTradingDateController extends CeasedTradingDateController(messagesApi, mockAuthPredicate, mockConfig)

  "the user is authorised" when {

    "Calling the .show action" when {

      "the user does not have a pre selected option" should {

        lazy val result = TestCeasedTradingDateController.show()(request)

        "return 200 (OK)" in {
          mockAuthResult(Future.successful(mockAuthorisedIndividual))
          status(result) shouldBe Status.OK
        }

        "return HTML" in {
          contentType(result) shouldBe Some("text/html")
          charset(result) shouldBe Some("utf-8")
        }
      }

      "the user is has previously entered values" should {

        lazy val result = TestCeasedTradingDateController.show()(request)

        "return 200 (OK)" in {
          mockAuthResult(Future.successful(mockAuthorisedIndividual))
          status(result) shouldBe Status.OK
        }

        "return HTML" in {
          contentType(result) shouldBe Some("text/html")
          charset(result) shouldBe Some("utf-8")
        }
      }

      authChecks(".show", TestCeasedTradingDateController.show(), request)
    }

    "Calling the .submit action" when {

      "the user submits entering a date" should {

        lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
          FakeRequest("POST", "/").withFormUrlEncodedBody(
            ("ceasedTradingDateDay", "1"),
            ("ceasedTradingDateMonth", "1"),
            ("ceasedTradingDateYear", "2018")
          )
        lazy val result = TestCeasedTradingDateController.submit()(request)

        "return 303 (SEE OTHER)" in {
          mockAuthResult(Future.successful(mockAuthorisedIndividual))
          status(result) shouldBe Status.SEE_OTHER
        }

        //TODO: This needs to be updated as part of the routing sub-task
        s"redirect to the ${controllers.routes.HelloWorldController.helloWorld().url}" in {
          redirectLocation(result) shouldBe Some(controllers.routes.HelloWorldController.helloWorld().url)
        }
      }

      "the user submits without entering any dates" should {

        lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
          FakeRequest("POST", "/").withFormUrlEncodedBody(
            ("ceasedTradingDateDay", ""),
            ("ceasedTradingDateMonth", ""),
            ("ceasedTradingDateYear", "")
          )
        lazy val result = TestCeasedTradingDateController.submit()(request)

        "return 400 (BAD REQUEST)" in {
          mockAuthResult(Future.successful(mockAuthorisedIndividual))
          status(result) shouldBe Status.BAD_REQUEST
        }

        "return HTML" in {
          contentType(result) shouldBe Some("text/html")
          charset(result) shouldBe Some("utf-8")
        }
      }
    }

    authChecks(".submit", TestCeasedTradingDateController.submit(), FakeRequest("POST", "/").withFormUrlEncodedBody(
      ("ceasedTradingDateDay", "1"),
      ("ceasedTradingDateMonth", "1"),
      ("ceasedTradingDateYear", "2018")
    ))
  }
}
