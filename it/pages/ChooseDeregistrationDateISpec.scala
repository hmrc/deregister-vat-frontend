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

package pages

import assets.IntegrationTestConstants._
import common.Constants
import forms.YesNoForm
import helpers.IntegrationBaseSpec
import models._
import play.api.http.Status._
import play.api.libs.json.Json
import play.api.libs.ws.WSResponse
import services.{ChooseDeregDateAnswerService, OutstandingInvoicesAnswerService}
import stubs.DeregisterVatStub

class ChooseDeregistrationDateISpec extends IntegrationBaseSpec {

  "Calling the GET Choose Deregistration Date endpoint" when {

    def getRequest: WSResponse = get("/choose-cancel-vat-date", formatPendingDereg(Some(Constants.registered)))

    "the user is authorised" should {

      "return 200 OK" in {

        given.user.isAuthorised

        DeregisterVatStub.successfulGetAnswer(vrn, ChooseDeregDateAnswerService.key)(Json.toJson(Yes))
        DeregisterVatStub.successfulGetAnswer(vrn, OutstandingInvoicesAnswerService.key)(Json.toJson(Yes))

        val response: WSResponse = getRequest

        response should have(
          httpStatus(OK),
          pageTitle("Do you want to choose the cancellation date?" + titleSuffix)
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

  "Calling the GET Deregistration date endpoint" when {

    def getRequest(pendingDereg: Option[String]): WSResponse = get("/choose-cancel-vat-date", formatPendingDereg(pendingDereg))

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
          redirectURI(controllers.routes.DeregisterForVATController.redirect().url)
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
          redirectURI(controllers.routes.DeregisterForVATController.redirect().url)
        )
      }
    }
  }

  "Calling the POST Choose Deregistration Date endpoint" when {

    def postRequest(data: YesNo): WSResponse =
      post("/choose-cancel-vat-date")(toFormData(YesNoForm.yesNoForm("yesNoError"), data))

    "the user is authorised" when {

      "the post request includes valid Yes data" should {

        "return 303 SEE_OTHER" in {

          given.user.isAuthorised
          DeregisterVatStub.successfulPutAnswer(vrn, ChooseDeregDateAnswerService.key)

          val response: WSResponse = postRequest(Yes)

          response should have(
            httpStatus(SEE_OTHER),
            redirectURI(controllers.routes.DeregistrationDateController.show().url)
          )
        }
      }

      "the post request includes valid No data" should {

        "return 303 SEE_OTHER" in {

          given.user.isAuthorised

          val response: WSResponse = postRequest(No)

          response should have(
            httpStatus(SEE_OTHER),
            redirectURI(controllers.routes.CheckAnswersController.show().url)
          )
        }
      }

      "the post request includes invalid Yes data" should {

        "return 400 BAD_REQUEST" in {

          DeregisterVatStub.successfulGetAnswer(vrn, OutstandingInvoicesAnswerService.key)(Json.toJson(Yes))
          given.user.isAuthorised

          post("/choose-cancel-vat-date")(Map("yes_no" -> Seq(""))) should have(
            httpStatus(BAD_REQUEST),
            pageTitle("Error: Do you want to choose the cancellation date?" + titleSuffix),
            elementText(".error-message")("Error: Select yes if the business wants to choose the cancellation date")
          )
        }
      }
    }

    "the user is not authenticated" should {

      "return 303 SEE_OTHER" in {

        given.user.isNotAuthenticated

        val response: WSResponse = postRequest(Yes)

        response should have(
          httpStatus(SEE_OTHER),
          redirectURI(appConfig.signInUrl)
        )
      }
    }

    "the user is not authorised" should {

      "return 403 FORBIDDEN" in {

        given.user.isNotAuthorised

        val response: WSResponse = postRequest(Yes)

        response should have(
          httpStatus(FORBIDDEN),
          pageTitle("You can’t use this service yet" + titleSuffixOther)
        )
      }
    }
  }
}
