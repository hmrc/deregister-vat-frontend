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

package pages

import assets.IntegrationTestConstants._
import common.Constants
import helpers.IntegrationBaseSpec
import models.{Ceased, No, Yes}
import play.api.http.Status._
import play.api.libs.json.Json
import play.api.libs.ws.WSResponse
import services._
import stubs.DeregisterVatStub

class IssueNewInvoicesISpec extends IntegrationBaseSpec {

  "Calling the GET IssueNewInvoices" when {

    def getRequest: WSResponse = get("/new-invoices", formatPendingDereg(Some(Constants.registered)) ++ isNotInsolvent)

    "the user is authorised" should {

      "return 200 OK" in {

        given.user.isAuthorised

        DeregisterVatStub.successfulGetAnswer(vrn,IssueNewInvoicesAnswerService.key)(Json.toJson(Yes))

        val response: WSResponse = getRequest

        response should have(
          httpStatus(OK),
          pageTitle("Is the business going to issue any new invoices after you cancel the registration?" + titleSuffix)
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


  "Calling the GET IssueNewInvoices" when {

    def getRequest(pendingDereg: Option[String]): WSResponse = get("/new-invoices", formatPendingDereg(pendingDereg) ++ isNotInsolvent)

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


  "Calling the POST IssueNewInvoices" when {

    def postRequest(data: Map[String, Seq[String]]): WSResponse = post("/new-invoices", isNotInsolvent)(data)

    val yes = Map("yes_no" -> Seq("yes"))
    val no = Map("yes_no" -> Seq("no"))
    val invalidModel = Map("yes_no" -> Seq(""))

    "the user is authorised" when {

      "posting 'Yes' data" should {

        "return 303 SEE_OTHER" in {

          given.user.isAuthorised

          DeregisterVatStub.successfulGetAnswer(vrn, DeregReasonAnswerService.key)(Json.toJson(Ceased))
          DeregisterVatStub.successfulGetAnswer(vrn, CapitalAssetsAnswerService.key)(capitalAssetsYesJson)
          DeregisterVatStub.successfulGetAnswer(vrn, IssueNewInvoicesAnswerService.key)(Json.toJson(No))
          DeregisterVatStub.successfulGetAnswer(vrn, OutstandingInvoicesAnswerService.key)(Json.toJson(Yes))
          DeregisterVatStub.successfulGetAnswer(vrn, BusinessActivityAnswerService.key)(Json.toJson(Yes))

          DeregisterVatStub.successfulPutAnswer(vrn,IssueNewInvoicesAnswerService.key)
          DeregisterVatStub.successfulDeleteAnswer(vrn,TaxableTurnoverAnswerService.key)
          DeregisterVatStub.successfulDeleteAnswer(vrn,WhyTurnoverBelowAnswerService.key)
          DeregisterVatStub.successfulDeleteAnswer(vrn,NextTaxableTurnoverAnswerService.key)
          DeregisterVatStub.successfulDeleteAnswer(vrn,BusinessActivityAnswerService.key)
          DeregisterVatStub.successfulDeleteAnswer(vrn,SicCodeAnswerService.key)
          DeregisterVatStub.successfulDeleteAnswer(vrn,ZeroRatedSuppliesValueService.key)
          DeregisterVatStub.successfulDeleteAnswer(vrn,PurchasesExceedSuppliesAnswerService.key)

          val response: WSResponse = postRequest(yes)

          response should have(
            httpStatus(SEE_OTHER),
            redirectURI(controllers.routes.ChooseDeregistrationDateController.show.url)
          )
        }
      }
    }

    "the user is authorised" when {

      "posting 'No' data" should {

        "return 303 SEE_OTHER" in {

          given.user.isAuthorised

          DeregisterVatStub.successfulGetAnswer(vrn, DeregReasonAnswerService.key)(Json.toJson(Ceased))
          DeregisterVatStub.successfulGetAnswer(vrn, CapitalAssetsAnswerService.key)(capitalAssetsYesJson)
          DeregisterVatStub.successfulGetAnswer(vrn, IssueNewInvoicesAnswerService.key)(Json.toJson(No))
          DeregisterVatStub.successfulGetAnswer(vrn, OutstandingInvoicesAnswerService.key)(Json.toJson(Yes))
          DeregisterVatStub.successfulGetAnswer(vrn, BusinessActivityAnswerService.key)(Json.toJson(Yes))

          DeregisterVatStub.successfulPutAnswer(vrn,IssueNewInvoicesAnswerService.key)
          DeregisterVatStub.successfulDeleteAnswer(vrn,TaxableTurnoverAnswerService.key)
          DeregisterVatStub.successfulDeleteAnswer(vrn,WhyTurnoverBelowAnswerService.key)
          DeregisterVatStub.successfulDeleteAnswer(vrn,NextTaxableTurnoverAnswerService.key)
          DeregisterVatStub.successfulDeleteAnswer(vrn,BusinessActivityAnswerService.key)
          DeregisterVatStub.successfulDeleteAnswer(vrn,SicCodeAnswerService.key)
          DeregisterVatStub.successfulDeleteAnswer(vrn,ZeroRatedSuppliesValueService.key)
          DeregisterVatStub.successfulDeleteAnswer(vrn,PurchasesExceedSuppliesAnswerService.key)

          val response: WSResponse = postRequest(no)

          response should have(
            httpStatus(SEE_OTHER),
            redirectURI(controllers.routes.OutstandingInvoicesController.show.url)
          )
        }
      }
    }

    "the user is authorised" when {

      "posting 'Yes' data and an error is returned when deleting redundant data" should {

        "return 303 SEE_OTHER" in {

          given.user.isAuthorised

          DeregisterVatStub.successfulGetAnswer(vrn, DeregReasonAnswerService.key)(Json.toJson(Ceased))
          DeregisterVatStub.successfulGetNoDataAnswer(vrn, TaxableTurnoverAnswerService.key)
          DeregisterVatStub.successfulGetAnswer(vrn, CapitalAssetsAnswerService.key)(capitalAssetsYesJson)
          DeregisterVatStub.successfulGetAnswer(vrn, IssueNewInvoicesAnswerService.key)(Json.toJson(Yes))
          DeregisterVatStub.successfulGetAnswer(vrn, OutstandingInvoicesAnswerService.key)(Json.toJson(Yes))
          DeregisterVatStub.successfulGetAnswer(vrn, BusinessActivityAnswerService.key)(Json.toJson(Yes))

          DeregisterVatStub.successfulPutAnswer(vrn,IssueNewInvoicesAnswerService.key)
          DeregisterVatStub.successfulDeleteAnswer(vrn,TaxableTurnoverAnswerService.key)
          DeregisterVatStub.successfulDeleteAnswer(vrn,WhyTurnoverBelowAnswerService.key)
          DeregisterVatStub.successfulDeleteAnswer(vrn,NextTaxableTurnoverAnswerService.key)
          DeregisterVatStub.successfulDeleteAnswer(vrn,BusinessActivityAnswerService.key)
          DeregisterVatStub.successfulDeleteAnswer(vrn,SicCodeAnswerService.key)
          DeregisterVatStub.successfulDeleteAnswer(vrn,ZeroRatedSuppliesValueService.key)
          DeregisterVatStub.successfulDeleteAnswer(vrn,PurchasesExceedSuppliesAnswerService.key)
          DeregisterVatStub.deleteAnswerError(vrn,OutstandingInvoicesAnswerService.key)

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
          pageTitle("Error: Is the business going to issue any new invoices after you cancel the registration?" + titleSuffix),
          elementText(".govuk-error-message")("Error: Select yes if the business is expecting to issue any new invoices after you cancel the registration")
        )
      }
    }
  }
}
