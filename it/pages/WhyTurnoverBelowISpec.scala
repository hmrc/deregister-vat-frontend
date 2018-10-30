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

import assets.IntegrationTestConstants._
import forms.WhyTurnoverBelowForm
import helpers.IntegrationBaseSpec
import models.WhyTurnoverBelowModel
import play.api.http.Status._
import play.api.libs.ws.WSResponse
import stubs.DeregisterVatStub
import services.WhyTurnoverBelowAnswerService


class WhyTurnoverBelowISpec extends IntegrationBaseSpec {

  "Calling the GET Why Turnover Below endpoint" when {

    def getRequest(): WSResponse = get("/reasons-for-low-turnover")

    "the user is authorised" should {

      "return 200 OK" in {

        given.user.isAuthorised

        DeregisterVatStub.successfulGetAnswer(vrn,WhyTurnoverBelowAnswerService.key)(whyTurnoverBelowJson)

        val response: WSResponse = getRequest()

        response should have(
          httpStatus(OK),
          pageTitle("Why does the business expect its turnover to be below the threshold?")
        )
      }
    }

    "the user is not authenticated" should {

      "return 303 SEE_OTHER" in {

        given.user.isNotAuthenticated

        val response: WSResponse = getRequest()

        response should have(
          httpStatus(SEE_OTHER),
          redirectURI(appConfig.signInUrl)
        )
      }
    }

    "the user is not authorised" should {

      "return 403 FORBIDDEN" in {

        given.user.isNotAuthorised

        val response: WSResponse = getRequest()

        response should have(
          httpStatus(FORBIDDEN),
          pageTitle("You can’t use this service yet")
        )
      }
    }
  }


  "Calling the POST Why Turnover Below endpoint" when {

    def postRequest(data: WhyTurnoverBelowModel): WSResponse =
      post("/reasons-for-low-turnover")(toFormData(WhyTurnoverBelowForm.whyTurnoverBelowForm, data))

    val validModel = WhyTurnoverBelowModel(true, true, true, true, true, true, true)
    val invalidModel = WhyTurnoverBelowModel(false, false, false, false, false, false, false)

    "the user is authorised" when {

      "the post request includes valid data" should {

        "return 303 SEE_OTHER" in {

          given.user.isAuthorised

          DeregisterVatStub.successfulPutAnswer(vrn,WhyTurnoverBelowAnswerService.key)

          val response: WSResponse = postRequest(validModel)

          response should have(
            httpStatus(SEE_OTHER),
            redirectURI(controllers.routes.VATAccountsController.show().url)
          )
        }
      }

      "the post request includes invalid data" should {

        "return 400 BAD_REQUEST" in {

          given.user.isAuthorised

          val response: WSResponse = postRequest(invalidModel)

          response should have(
            httpStatus(BAD_REQUEST),
            pageTitle("Why does the business expect its turnover to be below the threshold?"),
            elementText(".error-message")("Select at least one option")
          )
        }
      }
    }

    "the user is not authenticated" should {

      "return 303 SEE_OTHER" in {

        given.user.isNotAuthenticated

        val response: WSResponse = postRequest(validModel)

        response should have(
          httpStatus(SEE_OTHER),
          redirectURI(appConfig.signInUrl)
        )
      }
    }

    "the user is not authorised" should {

      "return 403 FORBIDDEN" in {

        given.user.isNotAuthorised

        val response: WSResponse = postRequest(validModel)

        response should have(
          httpStatus(FORBIDDEN),
          pageTitle("You can’t use this service yet")
        )
      }
    }
  }
}
