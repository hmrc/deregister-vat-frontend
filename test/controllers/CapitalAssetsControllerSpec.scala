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

class CapitalAssetsControllerSpec extends ControllerBaseSpec {

  object TestCapitalAssetsController extends CapitalAssetsController(messagesApi, mockAuthorisedFunctions, mockConfig)

  "the user is authorised" when {

    "Calling the .show action" when {

      "the user does not have a pre selected option" should {

        lazy val result = TestCapitalAssetsController.show()(request)

        "return 200 (OK)" in {
          mockAuthResult(individualAuthorised)
          status(result) shouldBe Status.OK
        }

        "return HTML" in {
          contentType(result) shouldBe Some("text/html")
          charset(result) shouldBe Some("utf-8")
        }
      }

      //TODO: These tests need to be updated once a stored value is retrieved from Mongo
      "the user is has pre selected option" ignore {

        lazy val result = TestCapitalAssetsController.show()(request)

        "return 200 (OK)" in {
          mockAuthResult(individualAuthorised)
          status(result) shouldBe Status.OK
        }

        "return HTML" in {
          contentType(result) shouldBe Some("text/html")
          charset(result) shouldBe Some("utf-8")
        }
      }

      authChecks(".show", TestCapitalAssetsController.show(), request)

    }

    "Calling the .submit action" when {

      "the user submits after selecting an 'Yes' option" should {

        lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
          FakeRequest("POST", "/").withFormUrlEncodedBody(("yes_no", "yes"))
        lazy val result = TestCapitalAssetsController.submit()(request)


        "return 303 (SEE OTHER)" in {
          mockAuthResult(individualAuthorised)
          status(result) shouldBe Status.SEE_OTHER
        }

        //TODO: This needs to be update as part of the routing sub-task
        "redirect to the HelloWorld controller" in {
          redirectLocation(result) shouldBe Some(controllers.routes.HelloWorldController.helloWorld().url)
        }
      }

      "the user submits after selecting the 'No' option" should {

        lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
          FakeRequest("POST", "/").withFormUrlEncodedBody(("yes_no", "no"))
        lazy val result = TestCapitalAssetsController.submit()(request)

        "return 303 (SEE OTHER)" in {
          mockAuthResult(individualAuthorised)
          status(result) shouldBe Status.SEE_OTHER
        }

        //TODO: This needs to be update as part of the routing sub-task
        "redirect to the HelloWorld controller" in {
          redirectLocation(result) shouldBe Some(controllers.routes.HelloWorldController.helloWorld().url)
        }
      }

      "the user submits without selecting an option" should {

        lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
          FakeRequest("POST", "/").withFormUrlEncodedBody(("yes_no", ""))
        lazy val result = TestCapitalAssetsController.submit()(request)

        "return 400 (BAD REQUEST)" in {
          mockAuthResult(individualAuthorised)
          status(result) shouldBe Status.BAD_REQUEST
        }

        "return HTML" in {
          contentType(result) shouldBe Some("text/html")
          charset(result) shouldBe Some("utf-8")
        }
      }
    }

    authChecks(".submit", TestCapitalAssetsController.submit(), FakeRequest("POST", "/").withFormUrlEncodedBody(("yes_no", "yes")))

  }
}
