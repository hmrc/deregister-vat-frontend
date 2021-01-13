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

import java.time.LocalDate

import assets.IntegrationTestConstants._
import common.Constants
import forms.DeregistrationDateForm
import helpers.IntegrationBaseSpec
import models.DateModel
import play.api.http.Status._
import play.api.libs.json.Json
import play.api.libs.ws.WSResponse
import services.DeregDateAnswerService
import stubs.DeregisterVatStub

class DeregistrationDateISpec extends IntegrationBaseSpec {

  val testDay: Int = LocalDate.now().getDayOfMonth
  val testMonth: Int = LocalDate.now().getMonthValue
  val testYear: Int = LocalDate.now().getYear
  val testDateModel: DateModel = DateModel(testDay, testMonth, testYear)

  "Calling the GET Deregistration Date endpoint" when {

    def getRequest(pendingDereg: Option[String] = Some(Constants.registered)): WSResponse =
      get("/enter-cancel-vat-date", formatPendingDereg(pendingDereg) ++ isNotInsolvent)

    "the user is authorised" should {

      "return 200 OK" in {

        given.user.isAuthorised

        DeregisterVatStub.successfulGetAnswer(vrn, DeregDateAnswerService.key)(Json.toJson(testDateModel))

        val response: WSResponse = getRequest()

        response should have(
          httpStatus(OK),
          pageTitle("What is the cancellation date?" + titleSuffix)
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
          pageTitle("You can’t use this service yet" + titleSuffixOther)
        )
      }
    }

    "user has a pending deregistration request" should {


      "redirect the user" in {
        given.user.isAuthorised

        val response: WSResponse = getRequest(Some(Constants.pending))

        response should have(
          httpStatus(SEE_OTHER),
          redirectURI(appConfig.vatSummaryFrontendUrl)
        )
      }
    }

    "no pending deregistration data in session and vat-subscription returns 'no pending deregistration'" should {

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

    "no pending deregistration data in session and vat-subscription returns 'pending deregistration'" should {

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

    "no pending deregistration data in session and vat-subscription returns 'None'" should {

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

  "Calling the POST Deregistration Date endpoint" when {

    def postRequest(data: DateModel): WSResponse =
      post("/enter-cancel-vat-date", formatPendingDereg(Some(Constants.registered)) ++ isNotInsolvent)(
        toFormData(DeregistrationDateForm.form, data)
      )

    "the user is authorised" when {

      "the post request includes valid data" should {

        "return 303 SEE_OTHER" in {

          given.user.isAuthorised
          DeregisterVatStub.successfulPutAnswer(vrn, DeregDateAnswerService.key)

          val response: WSResponse = postRequest(testDateModel)

          response should have(
            httpStatus(SEE_OTHER),
            redirectURI(controllers.routes.CheckAnswersController.show().url)
          )
        }
      }

      "the post request includes invalid data" should {

        "return 400 BAD_REQUEST" in {

          given.user.isAuthorised

          val response: WSResponse = postRequest(DateModel(0, 0, 0))

          response should have(
            httpStatus(BAD_REQUEST),
            pageTitle("Error: What is the cancellation date?" + titleSuffix),
            elementText(".error-message")("Error: Enter a valid cancellation date")
          )
        }
      }
    }

    "the user is not authenticated" should {

      "return 303 SEE_OTHER" in {

        given.user.isNotAuthenticated

        val response: WSResponse = postRequest(testDateModel)

        response should have(
          httpStatus(SEE_OTHER),
          redirectURI(appConfig.signInUrl)
        )
      }
    }

    "the user is not authorised" should {

      "return 403 FORBIDDEN" in {

        given.user.isNotAuthorised

        val response: WSResponse = postRequest(testDateModel)

        response should have(
          httpStatus(FORBIDDEN),
          pageTitle("You can’t use this service yet" + titleSuffixOther)
        )
      }
    }
  }
}
