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
import helpers.IntegrationBaseSpec
import models.{BelowThreshold, Yes, YesNoAmountModel}
import play.api.http.Status._
import play.api.libs.json.Json
import play.api.libs.ws.WSResponse
import services._
import stubs.DeregisterVatStub

class BusinessActivityISpec extends IntegrationBaseSpec {

  val yesNoAmountYes: YesNoAmountModel = YesNoAmountModel(Yes, Some(BigDecimal(1000)))

  "Calling the GET BusinessActivity" when {

    def getRequest: WSResponse = get("/has-the-business-activity-changed", formatPendingDereg(Some(Constants.registered)) ++ isNotInsolvent)

    "the user is authorised" should {

      "return 200 OK" in {

        given.user.isAuthorised

        DeregisterVatStub.successfulGetAnswer(vrn, BusinessActivityAnswerService.key)(Json.toJson(Yes))

        val response: WSResponse = getRequest

        response should have(
          httpStatus(OK),
          pageTitle("Has the business activity changed?" + titleSuffix)
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


  "Calling the GET BusinessActivity" when {

    def getRequest(pendingDereg: Option[String]): WSResponse =
      get("/has-the-business-activity-changed", formatPendingDereg(pendingDereg) ++ isNotInsolvent)

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


  "Calling the POST BusinessActivity" when {

    def postRequest(data: Map[String, Seq[String]]): WSResponse =
      post("/has-the-business-activity-changed", isNotInsolvent)(data)

    val yes = Map("yes_no" -> Seq("yes"))
    val no = Map("yes_no" -> Seq("no"))
    val invalidModel = Map("yes_no" -> Seq(""))

    "the user is authorised" when {

      "posting 'Yes' data" should {

        "return 303 SEE_OTHER" in {

          given.user.isAuthorised

          DeregisterVatStub.successfulPutAnswer(vrn, BusinessActivityAnswerService.key)

          DeregisterVatStub.successfulGetAnswer(vrn, DeregReasonAnswerService.key)(Json.toJson(BelowThreshold))
          DeregisterVatStub.successfulGetAnswer(vrn, CapitalAssetsAnswerService.key)(Json.toJson(yesNoAmountYes))
          DeregisterVatStub.successfulGetAnswer(vrn, IssueNewInvoicesAnswerService.key)(Json.toJson(Yes))
          DeregisterVatStub.successfulGetAnswer(vrn, OutstandingInvoicesAnswerService.key)(Json.toJson(Yes))
          DeregisterVatStub.successfulDeleteAnswer(vrn, CeasedTradingDateAnswerService.key)
          DeregisterVatStub.successfulDeleteAnswer(vrn, BusinessActivityAnswerService.key)
          DeregisterVatStub.successfulDeleteAnswer(vrn, SicCodeAnswerService.key)
          DeregisterVatStub.successfulDeleteAnswer(vrn, ZeroRatedSuppliesValueService.key)
          DeregisterVatStub.successfulDeleteAnswer(vrn, PurchasesExceedSuppliesAnswerService.key)
          DeregisterVatStub.successfulDeleteAnswer(vrn, NextTaxableTurnoverAnswerService.key)
          DeregisterVatStub.successfulDeleteAnswer(vrn, OutstandingInvoicesAnswerService.key)

          val response: WSResponse = postRequest(yes)

          response should have(
            httpStatus(SEE_OTHER),
            redirectURI(controllers.zeroRated.routes.SicCodeController.show().url)
          )
        }
      }
    }

    "the user is authorised" when {

      "posting 'No' data" should {

        "return 303 SEE_OTHER" in {

          given.user.isAuthorised

          DeregisterVatStub.successfulPutAnswer(vrn, BusinessActivityAnswerService.key)

          DeregisterVatStub.successfulGetAnswer(vrn, DeregReasonAnswerService.key)(Json.toJson(BelowThreshold))
          DeregisterVatStub.successfulGetAnswer(vrn, CapitalAssetsAnswerService.key)(Json.toJson(yesNoAmountYes))
          DeregisterVatStub.successfulGetAnswer(vrn, IssueNewInvoicesAnswerService.key)(Json.toJson(Yes))
          DeregisterVatStub.successfulGetAnswer(vrn, OutstandingInvoicesAnswerService.key)(Json.toJson(Yes))
          DeregisterVatStub.successfulDeleteAnswer(vrn, CeasedTradingDateAnswerService.key)
          DeregisterVatStub.successfulDeleteAnswer(vrn, BusinessActivityAnswerService.key)
          DeregisterVatStub.successfulDeleteAnswer(vrn, SicCodeAnswerService.key)
          DeregisterVatStub.successfulDeleteAnswer(vrn, ZeroRatedSuppliesValueService.key)
          DeregisterVatStub.successfulDeleteAnswer(vrn, PurchasesExceedSuppliesAnswerService.key)
          DeregisterVatStub.successfulDeleteAnswer(vrn, NextTaxableTurnoverAnswerService.key)
          DeregisterVatStub.successfulDeleteAnswer(vrn, OutstandingInvoicesAnswerService.key)

          val response: WSResponse = postRequest(no)

          response should have(
            httpStatus(SEE_OTHER),
            redirectURI(controllers.routes.NextTaxableTurnoverController.show().url)
          )
        }
      }
    }

    "the user is authorised" when {

      "posting 'Yes' data and an error is returned when deleting redundant data" should {

        "return 303 SEE_OTHER" in {

          given.user.isAuthorised

          DeregisterVatStub.putAnswerError(vrn, BusinessActivityAnswerService.key)

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
          pageTitle("Error: Has the business activity changed?" + titleSuffix),
          elementText(".govuk-error-message")("Error: Select yes if your business activity has changed")
        )
      }
    }
  }
}
