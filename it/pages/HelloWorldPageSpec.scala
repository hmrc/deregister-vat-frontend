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

package pages

import helpers.IntegrationBaseSpec
import play.api.libs.ws.WSResponse
import play.api.http.Status._

class HelloWorldPageSpec extends IntegrationBaseSpec {

  def request(): WSResponse = get("/hello-world")

  "Calling the HelloWorldController.helloWorld" when {

    "the user is authorised" should {

      "return 200 OK" in {

        given.user.isAuthorised

        val response: WSResponse = request()

        response should have(
          httpStatus(OK),
          pageTitle("Hello from deregister-vat-frontend")
        )
      }
    }

    "the user is not authenticated" should {

      "return 401 UNAUTHORIZED" in {

        given.user.isNotAuthenticated

        val response: WSResponse = request()

        response should have(
          httpStatus(UNAUTHORIZED),
          pageTitle("Your session has timed out")
        )
      }
    }

    "the user is not authorised" should {

      "return 403 FORBIDDEN" in {

        given.user.isNotAuthorised

        val response: WSResponse = request()

        response should have(
          httpStatus(FORBIDDEN),
          pageTitle("Unauthorised access")
        )
      }
    }
  }
}
