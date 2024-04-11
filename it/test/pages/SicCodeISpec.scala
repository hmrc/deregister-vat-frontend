/*
 * Copyright 2023 HM Revenue & Customs
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

package test.pages

import test.assets.IntegrationTestConstants._
import common.Constants
import models.Yes
import play.api.http.Status._
import play.api.libs.json.Json
import play.api.libs.ws.WSResponse
import services._
import test.helpers.IntegrationBaseSpec
import test.stubs.DeregisterVatStub

class SicCodeISpec extends IntegrationBaseSpec {

  "Calling the SicCode page" when {

    def getRequest: WSResponse = get("/what-is-the-sic-code", formatPendingDereg(Some(Constants.registered)) ++ isNotInsolvent)

    "the user is authorised" should {

      "return 200 OK" in {

        given.user.isAuthorised

        DeregisterVatStub.successfulGetAnswer(vrn, BusinessActivityAnswerService.key)(Json.toJson(Yes))
        DeregisterVatStub.successfulGetAnswer(vrn, SicCodeAnswerService.key)(Json.toJson(sicCodeValue))

        val response: WSResponse = getRequest

        response should have(
          httpStatus(OK),
          pageTitle("What is the business’s Standard Industrial Classification (SIC) Code?" + titleSuffix)
        )
      }
    }

    "the user is not authenticated" should {

      "return 303 SEE_OTHER" in {

        given.user.isNotAuthenticated

        val response: WSResponse = getRequest

        response should have(
          httpStatus(SEE_OTHER),
          redirectURI(appConfig.signInUrl)
        )
      }
    }

    "the user is not authorised" should {

      "return 403 FORBIDDEN" in {

        given.user.isNotAuthorised

        val response: WSResponse = getRequest

        response should have(
          httpStatus(FORBIDDEN),
          pageTitle("You can’t use this service yet" + titleSuffixOther)
        )
      }
    }
  }

  "Calling the GET Sic Code endpoint" when {

    def getRequest(pendingDereg: Option[String]): WSResponse = get("/what-is-the-sic-code", formatPendingDereg(pendingDereg) ++ isNotInsolvent)

    "user has a pending dereg request" should {

      "redirect the user" in {
        given.user.isAuthorised

        val response: WSResponse = getRequest(Some(Constants.pending))

        response should have(
          httpStatus(SEE_OTHER),
          redirectURI(appConfig.vatSummaryFrontendUrl)
        )
      }
    }

    "no pending dereg data in session and vat-subscription returns 'no pending dereg'" should {

      "redirect user to the start of the journey" in {
        given.user.isAuthorised
        given.user.noDeregPending

        val response: WSResponse = getRequest(None)

        response should have(
          httpStatus(SEE_OTHER),
          redirectURI(controllers.routes.DeregisterForVATController.show.url)
        )
      }
    }

    "no pending dereg data in session and vat-subscription returns 'pending dereg'" should {

      "redirect the user" in {
        given.user.isAuthorised
        given.user.deregPending

        val response: WSResponse = getRequest(None)

        response should have(
          httpStatus(SEE_OTHER),
          redirectURI(appConfig.vatSummaryFrontendUrl)
        )
      }
    }

    "no pending dereg data in session and vat-subscription returns 'None'" should {

      "redirect user to the start of the journey" in {
        given.user.isAuthorised
        given.user.noPendingData

        val response: WSResponse = getRequest(None)

        response should have(
          httpStatus(SEE_OTHER),
          redirectURI(controllers.routes.DeregisterForVATController.show.url)
        )
      }
    }
  }

  "Calling the POST SicCode" when {

    def postRequest(data: Map[String, Seq[String]]): WSResponse =
      post("/what-is-the-sic-code", isNotInsolvent)(data)

    "the user is authorised" when {

      "the post request includes valid data" should {

        "return 303 SEE_OTHER" in {

          given.user.isAuthorised

          DeregisterVatStub.successfulPutAnswer(vrn, SicCodeAnswerService.key)

          val response: WSResponse = postRequest(Map("value" -> Seq("12345")))

          response should have(
            httpStatus(SEE_OTHER),
            redirectURI(controllers.routes.NextTaxableTurnoverController.show.url)
          )
        }
      }

      "the post returns an error" should {

        "return 500 INTERNAL_SERVER_ERROR" in {

          given.user.isAuthorised

          DeregisterVatStub.putAnswerError(vrn, SicCodeAnswerService.key)

          val response: WSResponse = postRequest(Map("value" -> Seq("12345")))

          response should have(
            httpStatus(INTERNAL_SERVER_ERROR)
          )
        }
      }
    }

    "the user is not authenticated" should {

      "return 303 SEE_OTHER" in {

        given.user.isNotAuthenticated

        val response: WSResponse = postRequest(Map("value" -> Seq("12345")))

        response should have(
          httpStatus(SEE_OTHER),
          redirectURI(appConfig.signInUrl)
        )
      }
    }

    "the user is not authorised" should {

      "return 403 FORBIDDEN" in {

        given.user.isNotAuthorised

        val response: WSResponse = postRequest(Map("value" -> Seq("12345")))

        response should have(
          httpStatus(FORBIDDEN),
          pageTitle("You can’t use this service yet" + titleSuffixOther)
        )
      }
    }


    "the post request includes invalid data" should {

      "return 400 BAD_REQUEST" in {

        given.user.isAuthorised

        val response: WSResponse = postRequest(Map("value" -> Seq("")))

        response should have(
          httpStatus(BAD_REQUEST),
          pageTitle("Error: What is the business’s Standard Industrial Classification (SIC) Code?" + titleSuffix),
          elementText(".govuk-error-message")("Error: Enter the 5 digit code which best describes your business activity")
        )
      }
    }
  }
}
