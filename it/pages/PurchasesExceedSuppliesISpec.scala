/*
 * Copyright 2019 HM Revenue & Customs
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
import helpers.IntegrationBaseSpec
import models.Yes
import play.api.http.Status._
import play.api.libs.json.Json
import play.api.libs.ws.WSResponse
import services.PurchasesExceedSuppliesAnswerService
import stubs.DeregisterVatStub

class PurchasesExceedSuppliesISpec extends IntegrationBaseSpec {

  "Calling the GET PurchasesExceedSupplies" when {

    def getRequest: WSResponse = get("/expected-value-vat-purchases", formatPendingDereg(Some(Constants.registered)))

    "the user is authorised" should {

      "return 200 OK" in {

        given.user.isAuthorised

        DeregisterVatStub.successfulGetAnswer(vrn,PurchasesExceedSuppliesAnswerService.key)(Json.toJson(Yes))

        val response: WSResponse = getRequest

        response should have(
          httpStatus(OK),
          pageTitle("Do you expect the VAT on purchases to regularly exceed the VAT on supplies?" + titleSuffix)
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


  "Calling the GET PurchasesExceedSupplies" when {

    def getRequest(pendingDereg: Option[String]): WSResponse = get("/expected-value-vat-purchases", formatPendingDereg(pendingDereg))

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


  "Calling the POST PurchasesExceedSupplies" when {

    def postRequest(data: Map[String, Seq[String]]): WSResponse =
      post("/expected-value-vat-purchases")(data)

    val yes = Map("yes_no" -> Seq("yes"))
    val no = Map("yes_no" -> Seq("no"))
    val invalidModel = Map("yes_no" -> Seq(""))

    "the user is authorised" when {

      "posting 'Yes' data" should {

        "return 303 SEE_OTHER" in {

          given.user.isAuthorised

          DeregisterVatStub.successfulPutAnswer(vrn, PurchasesExceedSuppliesAnswerService.key)

          val response: WSResponse = postRequest(yes)

          response should have(
            httpStatus(SEE_OTHER),
            redirectURI(controllers.routes.VATAccountsController.show().url)
          )
        }
      }
    }

    "the user is authorised" when {

      "posting 'No' data" should {

        "return 303 SEE_OTHER" in {

          given.user.isAuthorised

          DeregisterVatStub.successfulPutAnswer(vrn, PurchasesExceedSuppliesAnswerService.key)

          val response: WSResponse = postRequest(no)

          response should have(
            httpStatus(SEE_OTHER),
            redirectURI(controllers.routes.VATAccountsController.show().url)
          )
        }
      }
    }

    "the user is authorised" when {

      "posting 'Yes' data and an error is returned when deleting redundant data" should {

        "return 500 INTERNAL_SERVER_ERROR" in {

          given.user.isAuthorised

          DeregisterVatStub.putAnswerError(vrn ,PurchasesExceedSuppliesAnswerService.key)

          val response: WSResponse = postRequest(yes)

          response should have(
            httpStatus(INTERNAL_SERVER_ERROR)
          )
        }
      }
    }

    "the user is not authenticated" should {

      "return 303 Redirect" in {

        given.user.isNotAuthenticated

        val response: WSResponse = postRequest(yes)

        response should have(
          httpStatus(SEE_OTHER),
          redirectURI(appConfig.signInUrl)
        )
      }
    }

    "the user is not authorised" should {

      "return 403 FORBIDDEN" in {

        given.user.isNotAuthorised

        val response: WSResponse = postRequest(no)

        response should have(
          httpStatus(FORBIDDEN),
          pageTitle("You can’t use this service yet" + titleSuffixOther)
        )
      }
    }

    "the post request includes invalid data" should {

      "return 400 BAD_REQUEST" in {

        given.user.isAuthorised

        val response: WSResponse = postRequest(invalidModel)

        response should have(
          httpStatus(BAD_REQUEST),
          pageTitle("Error: Do you expect the VAT on purchases to regularly exceed the VAT on supplies?" + titleSuffix),
          elementText(".error-message")("Select yes if you expect VAT on purchases to be more than VAT on supplies")
        )
      }
    }
  }
}
