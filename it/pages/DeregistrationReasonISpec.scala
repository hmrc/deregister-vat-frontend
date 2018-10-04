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
import forms.DeregistrationReasonForm
import helpers.IntegrationBaseSpec
import models._
import play.api.http.Status._
import play.api.libs.json.Json
import play.api.libs.ws.WSResponse
import services._
import stubs.DeregisterVatStub


class DeregistrationReasonISpec extends IntegrationBaseSpec {

  "Calling the GET Why Turnover Below endpoint" when {

    def getRequest(): WSResponse = get("/deregistration-reason?isAgent=false")

    "the user is authorised" should {

      "return 200 OK" in {

        given.user.isAuthorised

        DeregisterVatStub.successfulGetAnswer(vrn,DeregReasonAnswerService.key)(Json.toJson(Ceased))

        val response: WSResponse = getRequest()

        response should have(
          httpStatus(OK),
          pageTitle("Why is the business deregistering from VAT?")
        )
      }
    }

    "the user is not authenticated" should {

      "return 401 UNAUTHORIZED" in {

        given.user.isNotAuthenticated

        val response: WSResponse = getRequest()

        response should have(
          httpStatus(UNAUTHORIZED),
          pageTitle("Your session has timed out")
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

    def postRequest(data: DeregistrationReason): WSResponse =
      post("/deregistration-reason")(toFormData(DeregistrationReasonForm.deregistrationReasonForm, data))


    "the user is authorised" when {

      "the post request includes valid ceased journey data" should {

        "return 303 SEE_OTHER" in {

          given.user.isAuthorised

          DeregisterVatStub.successfulGetNoDataAnswer(vrn, TaxableTurnoverAnswerService.key)
          DeregisterVatStub.successfulGetAnswer(vrn, CapitalAssetsAnswerService.key)(capitalAssetsYesJson)
          DeregisterVatStub.successfulGetAnswer(vrn, IssueNewInvoicesAnswerService.key)(Json.toJson(No))
          DeregisterVatStub.successfulGetAnswer(vrn, OutstandingInvoicesAnswerService.key)(Json.toJson(Yes))

          DeregisterVatStub.successfulPutAnswer(vrn,DeregReasonAnswerService.key)

          DeregisterVatStub.successfulDeleteAnswer(vrn,TaxableTurnoverAnswerService.key)
          DeregisterVatStub.successfulDeleteAnswer(vrn,NextTaxableTurnoverAnswerService.key)
          DeregisterVatStub.successfulDeleteAnswer(vrn,WhyTurnoverBelowAnswerService.key)


          val response: WSResponse = postRequest(Ceased)

          response should have(
            httpStatus(SEE_OTHER),
            redirectURI(controllers.routes.CeasedTradingDateController.show().url)
          )
        }
      }

      "the post request includes valid below threshold journey data" should {

        "return 303 SEE_OTHER" in {

          given.user.isAuthorised

          DeregisterVatStub.successfulGetNoDataAnswer(vrn, TaxableTurnoverAnswerService.key)
          DeregisterVatStub.successfulGetAnswer(vrn, CapitalAssetsAnswerService.key)(capitalAssetsYesJson)
          DeregisterVatStub.successfulGetAnswer(vrn, IssueNewInvoicesAnswerService.key)(Json.toJson(No))
          DeregisterVatStub.successfulGetAnswer(vrn, OutstandingInvoicesAnswerService.key)(Json.toJson(Yes))

          DeregisterVatStub.successfulPutAnswer(vrn,DeregReasonAnswerService.key)
          DeregisterVatStub.successfulDeleteAnswer(vrn,CeasedTradingDateAnswerService.key)


          val response: WSResponse = postRequest(BelowThreshold)

          response should have(
            httpStatus(SEE_OTHER),
            redirectURI(controllers.routes.TaxableTurnoverController.show().url)
          )
        }
      }

      "the post request fails to delete data" should {

        "return 303 SEE_OTHER" in {

          given.user.isAuthorised

          DeregisterVatStub.successfulGetNoDataAnswer(vrn, TaxableTurnoverAnswerService.key)
          DeregisterVatStub.successfulGetAnswer(vrn, CapitalAssetsAnswerService.key)(capitalAssetsYesJson)
          DeregisterVatStub.successfulGetAnswer(vrn, IssueNewInvoicesAnswerService.key)(Json.toJson(No))
          DeregisterVatStub.successfulGetAnswer(vrn, OutstandingInvoicesAnswerService.key)(Json.toJson(Yes))

          DeregisterVatStub.successfulPutAnswer(vrn,DeregReasonAnswerService.key)
          DeregisterVatStub.successfulDeleteAnswer(vrn,TaxableTurnoverAnswerService.key)
          DeregisterVatStub.successfulDeleteAnswer(vrn,NextTaxableTurnoverAnswerService.key)
          DeregisterVatStub.deleteAnswerError(vrn,WhyTurnoverBelowAnswerService.key)


          val response: WSResponse = postRequest(Ceased)

          response should have(
            httpStatus(INTERNAL_SERVER_ERROR)
          )
        }
      }
    }

    "the user is not authenticated" should {

      "return 401 UNAUTHORIZED" in {

        given.user.isNotAuthenticated

        val response: WSResponse = postRequest(Ceased)

        response should have(
          httpStatus(UNAUTHORIZED),
          pageTitle("Your session has timed out")
        )
      }
    }

    "the user is not authorised" should {

      "return 403 FORBIDDEN" in {

        given.user.isNotAuthorised

        val response: WSResponse = postRequest(Ceased)

        response should have(
          httpStatus(FORBIDDEN),
          pageTitle("You can’t use this service yet")
        )
      }
    }

  }
}
