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
import models._
import play.api.http.Status._
import play.api.libs.json.Json
import play.api.libs.ws.WSResponse
import services._
import stubs.{DeregisterVatStub, VatSubscriptionStub}

import java.time.LocalDate

class CheckYourAnswersISpec extends IntegrationBaseSpec {

  val dateModel: DateModel = DateModel(1,1,2018)
  val taxableTurnoverAbove: NumberInputModel = NumberInputModel(BigDecimal(90000))
  val taxableTurnoverBelow: NumberInputModel = NumberInputModel(BigDecimal(200))
  val yesNoAmountYes: YesNoAmountModel = YesNoAmountModel(Yes,Some(BigDecimal(1000)))
  val yesNoAmountNo: YesNoAmountModel = YesNoAmountModel(No,None)
  val zeroRatedSuppliesValue: NumberInputModel = NumberInputModel(1000)
  val dateModelNow: DateModel = DateModel(LocalDate.now().getDayOfMonth,
    LocalDate.now().getMonthValue,
    LocalDate.now().getYear
  )

  override def beforeEach(): Unit = {
    super.beforeEach()
    resetWireMock()
  }

  "Calling GET Check your answers" when {

    def getRequest: WSResponse = get("/check-your-answers", formatPendingDereg(Some(Constants.registered)) ++ isNotInsolvent)

    "the user is authorised" should {

      "return 200 OK" in {

        DeregisterVatStub.successfulGetAnswer(vrn, DeregReasonAnswerService.key)(Json.toJson(BelowThreshold))
        DeregisterVatStub.successfulGetAnswer(vrn, CeasedTradingDateAnswerService.key)(Json.toJson(dateModel))
        DeregisterVatStub.successfulGetAnswer(vrn, TaxableTurnoverAnswerService.key)(Json.toJson(Yes))
        DeregisterVatStub.successfulGetAnswer(vrn, NextTaxableTurnoverAnswerService.key)(Json.toJson(taxableTurnoverBelow))
        DeregisterVatStub.successfulGetAnswer(vrn, WhyTurnoverBelowAnswerService.key)(Json.toJson(whyTurnoverBelowModel))
        DeregisterVatStub.successfulGetAnswer(vrn, AccountingMethodAnswerService.key)(Json.toJson(StandardAccounting))
        DeregisterVatStub.successfulGetAnswer(vrn, StocksAnswerService.key)(Json.toJson(yesNoAmountYes))
        DeregisterVatStub.successfulGetAnswer(vrn, CapitalAssetsAnswerService.key)(Json.toJson(yesNoAmountYes))
        DeregisterVatStub.successfulGetAnswer(vrn, OptionTaxAnswerService.key)(Json.toJson(yesNoAmountNo))
        DeregisterVatStub.successfulGetAnswer(vrn, IssueNewInvoicesAnswerService.key)(Json.toJson(Yes))
        DeregisterVatStub.successfulGetAnswer(vrn, OutstandingInvoicesAnswerService.key)(Json.toJson(Yes))
        DeregisterVatStub.successfulGetAnswer(vrn, ChooseDeregDateAnswerService.key)(Json.toJson(Yes))
        DeregisterVatStub.successfulGetAnswer(vrn, DeregDateAnswerService.key)(Json.toJson(dateModel))
        DeregisterVatStub.successfulGetAnswer(vrn, BusinessActivityAnswerService.key)(Json.toJson(Yes))
        DeregisterVatStub.successfulGetAnswer(vrn, SicCodeAnswerService.key)(Json.toJson(sicCodeValue))
        DeregisterVatStub.successfulGetAnswer(vrn, ZeroRatedSuppliesValueService.key)(Json.toJson(zeroRatedSuppliesValue))
        DeregisterVatStub.successfulGetAnswer(vrn, PurchasesExceedSuppliesAnswerService.key)(Json.toJson(Yes))

        given.user.isAuthorised

        val response: WSResponse = getRequest

        response should have(
          httpStatus(OK),
          pageTitle("Check your answers" + titleSuffix)
        )
      }
    }

    "an error is returned from an answer" should {

      "return 500 INTERNAL SERVER ERROR" in {

        DeregisterVatStub.successfulGetAnswer(vrn, DeregReasonAnswerService.key)(Json.toJson(BelowThreshold))
        DeregisterVatStub.successfulGetAnswer(vrn, CeasedTradingDateAnswerService.key)(Json.toJson(dateModel))
        DeregisterVatStub.successfulGetAnswer(vrn, TaxableTurnoverAnswerService.key)(Json.toJson(Yes))
        DeregisterVatStub.successfulGetAnswer(vrn, NextTaxableTurnoverAnswerService.key)(Json.toJson(taxableTurnoverBelow))
        DeregisterVatStub.successfulGetAnswer(vrn, WhyTurnoverBelowAnswerService.key)(Json.toJson(whyTurnoverBelowModel))
        DeregisterVatStub.successfulGetAnswer(vrn, AccountingMethodAnswerService.key)(Json.toJson(StandardAccounting))
        DeregisterVatStub.successfulGetAnswer(vrn, StocksAnswerService.key)(Json.toJson(yesNoAmountYes))
        DeregisterVatStub.successfulGetAnswer(vrn, CapitalAssetsAnswerService.key)(Json.toJson(yesNoAmountYes))
        DeregisterVatStub.successfulGetAnswer(vrn, OptionTaxAnswerService.key)(Json.toJson(yesNoAmountNo))
        DeregisterVatStub.successfulGetAnswer(vrn, IssueNewInvoicesAnswerService.key)(Json.toJson(Yes))
        DeregisterVatStub.successfulGetAnswer(vrn, OutstandingInvoicesAnswerService.key)(Json.toJson(Yes))

        DeregisterVatStub.getAnswerError(vrn, ChooseDeregDateAnswerService.key)

        given.user.isAuthorised

        val response: WSResponse = getRequest

        response should have(
          httpStatus(INTERNAL_SERVER_ERROR)
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

  "Calling the GET Check Your Answers" when {

    def getRequest(pendingDereg: Option[String]): WSResponse = get("/check-your-answers", formatPendingDereg(pendingDereg) ++ isNotInsolvent)

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
          redirectURI(controllers.routes.DeregisterForVATController.show().url)
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
          redirectURI(controllers.routes.DeregisterForVATController.show().url)
        )
      }
    }
  }

  "Calling POST Check your answers" when {

    def postRequest: WSResponse = post("/check-your-answers", isNotInsolvent)(Map.empty)

    "user is authorised" when {

      "deregistration is successful" when {

        "all possible Zero Rated deregistration reason answers are completed" should {

          "redirect user to confirmation page" in {

            DeregisterVatStub.successfulGetAnswer(vrn, DeregReasonAnswerService.key)(Json.toJson(ZeroRated))
            DeregisterVatStub.successfulGetAnswer(vrn, PurchasesExceedSuppliesAnswerService.key)(Json.toJson(Yes))
            DeregisterVatStub.successfulGetAnswer(vrn, SicCodeAnswerService.key)(Json.toJson(sicCodeValue))
            DeregisterVatStub.successfulGetAnswer(vrn, ZeroRatedSuppliesValueService.key)(Json.toJson(zeroRatedSuppliesValue))
            DeregisterVatStub.successfulGetAnswer(vrn, NextTaxableTurnoverAnswerService.key)(Json.toJson(taxableTurnoverBelow))
            DeregisterVatStub.successfulGetAnswer(vrn, AccountingMethodAnswerService.key)(Json.toJson(CashAccounting))
            DeregisterVatStub.successfulGetAnswer(vrn, StocksAnswerService.key)(Json.toJson(yesNoAmountYes))
            DeregisterVatStub.successfulGetAnswer(vrn, CapitalAssetsAnswerService.key)(Json.toJson(yesNoAmountYes))
            DeregisterVatStub.successfulGetAnswer(vrn, OptionTaxAnswerService.key)(Json.toJson(yesNoAmountYes))
            DeregisterVatStub.successfulGetAnswer(vrn, IssueNewInvoicesAnswerService.key)(Json.toJson(Yes))
            DeregisterVatStub.successfulGetAnswer(vrn, OutstandingInvoicesAnswerService.key)(Json.toJson(Yes))
            DeregisterVatStub.successfulGetAnswer(vrn, ChooseDeregDateAnswerService.key)(Json.toJson(Yes))
            DeregisterVatStub.successfulGetAnswer(vrn, DeregDateAnswerService.key)(Json.toJson(dateModel))
            DeregisterVatStub.successfulGetNoDataAnswer(vrn, CeasedTradingDateAnswerService.key)
            DeregisterVatStub.successfulGetNoDataAnswer(vrn, TaxableTurnoverAnswerService.key)
            DeregisterVatStub.successfulGetNoDataAnswer(vrn, WhyTurnoverBelowAnswerService.key)

            VatSubscriptionStub.deregisterForVatSuccess()

            given.user.isAuthorised

            val response: WSResponse = postRequest

            VatSubscriptionStub.verifyDeregistration(zeroRatedFullPayloadJson)

            response should have(
              httpStatus(SEE_OTHER),
              redirectURI("/vat-through-software/account/cancel-vat/cancel-vat-request-received")
            )
          }
        }

        "all possible Below Threshold deregistration reason answers are completed" should {

          "redirect user to confirmation page" in {

            DeregisterVatStub.successfulGetAnswer(vrn, DeregReasonAnswerService.key)(Json.toJson(BelowThreshold))
            DeregisterVatStub.successfulGetAnswer(vrn, TaxableTurnoverAnswerService.key)(Json.toJson(Yes))
            DeregisterVatStub.successfulGetAnswer(vrn, NextTaxableTurnoverAnswerService.key)(Json.toJson(taxableTurnoverBelow))
            DeregisterVatStub.successfulGetAnswer(vrn, WhyTurnoverBelowAnswerService.key)(Json.toJson(whyTurnoverBelowModel))
            DeregisterVatStub.successfulGetAnswer(vrn, AccountingMethodAnswerService.key)(Json.toJson(CashAccounting))
            DeregisterVatStub.successfulGetAnswer(vrn, StocksAnswerService.key)(Json.toJson(yesNoAmountYes))
            DeregisterVatStub.successfulGetAnswer(vrn, CapitalAssetsAnswerService.key)(Json.toJson(yesNoAmountYes))
            DeregisterVatStub.successfulGetAnswer(vrn, OptionTaxAnswerService.key)(Json.toJson(yesNoAmountYes))
            DeregisterVatStub.successfulGetAnswer(vrn, IssueNewInvoicesAnswerService.key)(Json.toJson(Yes))
            DeregisterVatStub.successfulGetAnswer(vrn, OutstandingInvoicesAnswerService.key)(Json.toJson(Yes))
            DeregisterVatStub.successfulGetAnswer(vrn, ChooseDeregDateAnswerService.key)(Json.toJson(Yes))
            DeregisterVatStub.successfulGetAnswer(vrn, DeregDateAnswerService.key)(Json.toJson(dateModel))
            DeregisterVatStub.successfulGetNoDataAnswer(vrn, CeasedTradingDateAnswerService.key)
            DeregisterVatStub.successfulGetAnswer(vrn, PurchasesExceedSuppliesAnswerService.key)(Json.toJson(Yes))
            DeregisterVatStub.successfulGetNoDataAnswer(vrn, SicCodeAnswerService.key)
            DeregisterVatStub.successfulGetNoDataAnswer(vrn, ZeroRatedSuppliesValueService.key)

            VatSubscriptionStub.deregisterForVatSuccess()

            given.user.isAuthorised

            val response: WSResponse = postRequest

            VatSubscriptionStub.verifyDeregistration(belowThresholdFullPayloadJson)

            response should have(
              httpStatus(SEE_OTHER),
              redirectURI("/vat-through-software/account/cancel-vat/cancel-vat-request-received")
            )
          }
        }

        "all possible Ceased Trading deregistration reason answers are completed" should {

          "redirect user to confirmation page" in {

            DeregisterVatStub.successfulGetAnswer(vrn, DeregReasonAnswerService.key)(Json.toJson(Ceased))
            DeregisterVatStub.successfulGetAnswer(vrn, AccountingMethodAnswerService.key)(Json.toJson(CashAccounting))
            DeregisterVatStub.successfulGetAnswer(vrn, StocksAnswerService.key)(Json.toJson(yesNoAmountYes))
            DeregisterVatStub.successfulGetAnswer(vrn, CapitalAssetsAnswerService.key)(Json.toJson(yesNoAmountYes))
            DeregisterVatStub.successfulGetAnswer(vrn, OptionTaxAnswerService.key)(Json.toJson(yesNoAmountYes))
            DeregisterVatStub.successfulGetAnswer(vrn, IssueNewInvoicesAnswerService.key)(Json.toJson(Yes))
            DeregisterVatStub.successfulGetAnswer(vrn, OutstandingInvoicesAnswerService.key)(Json.toJson(Yes))
            DeregisterVatStub.successfulGetAnswer(vrn, ChooseDeregDateAnswerService.key)(Json.toJson(Yes))
            DeregisterVatStub.successfulGetAnswer(vrn, DeregDateAnswerService.key)(Json.toJson(dateModel))
            DeregisterVatStub.successfulGetAnswer(vrn, CeasedTradingDateAnswerService.key)(Json.toJson(dateModelNow))
            DeregisterVatStub.successfulGetAnswer(vrn, TaxableTurnoverAnswerService.key)(Json.toJson(Yes))
            DeregisterVatStub.successfulGetAnswer(vrn, NextTaxableTurnoverAnswerService.key)(Json.toJson(taxableTurnoverBelow))
            DeregisterVatStub.successfulGetNoDataAnswer(vrn, WhyTurnoverBelowAnswerService.key)
            DeregisterVatStub.successfulGetNoDataAnswer(vrn, PurchasesExceedSuppliesAnswerService.key)
            DeregisterVatStub.successfulGetNoDataAnswer(vrn, SicCodeAnswerService.key)
            DeregisterVatStub.successfulGetNoDataAnswer(vrn, ZeroRatedSuppliesValueService.key)

            VatSubscriptionStub.deregisterForVatSuccess()

            given.user.isAuthorised

            val response: WSResponse = postRequest

            VatSubscriptionStub.verifyDeregistration(ceasedTradingFullPayloadJson)

            response should have(
              httpStatus(SEE_OTHER),
              redirectURI("/vat-through-software/account/cancel-vat/cancel-vat-request-received")
            )
          }
        }

        "all possible Exempt Only Supplies deregistration reason answers are completed" should {

          "redirect user to confirmation page" in {

            DeregisterVatStub.successfulGetAnswer(vrn, DeregReasonAnswerService.key)(Json.toJson(ExemptOnly))
            DeregisterVatStub.successfulGetAnswer(vrn, AccountingMethodAnswerService.key)(Json.toJson(CashAccounting))
            DeregisterVatStub.successfulGetAnswer(vrn, StocksAnswerService.key)(Json.toJson(yesNoAmountYes))
            DeregisterVatStub.successfulGetAnswer(vrn, CapitalAssetsAnswerService.key)(Json.toJson(yesNoAmountYes))
            DeregisterVatStub.successfulGetAnswer(vrn, OptionTaxAnswerService.key)(Json.toJson(yesNoAmountYes))
            DeregisterVatStub.successfulGetAnswer(vrn, IssueNewInvoicesAnswerService.key)(Json.toJson(Yes))
            DeregisterVatStub.successfulGetAnswer(vrn, OutstandingInvoicesAnswerService.key)(Json.toJson(Yes))
            DeregisterVatStub.successfulGetAnswer(vrn, ChooseDeregDateAnswerService.key)(Json.toJson(Yes))
            DeregisterVatStub.successfulGetAnswer(vrn, DeregDateAnswerService.key)(Json.toJson(dateModel))
            DeregisterVatStub.successfulGetNoDataAnswer(vrn, CeasedTradingDateAnswerService.key)
            DeregisterVatStub.successfulGetAnswer(vrn, TaxableTurnoverAnswerService.key)(Json.toJson(Yes))
            DeregisterVatStub.successfulGetAnswer(vrn, NextTaxableTurnoverAnswerService.key)(Json.toJson(taxableTurnoverBelow))
            DeregisterVatStub.successfulGetNoDataAnswer(vrn, WhyTurnoverBelowAnswerService.key)
            DeregisterVatStub.successfulGetNoDataAnswer(vrn, PurchasesExceedSuppliesAnswerService.key)
            DeregisterVatStub.successfulGetNoDataAnswer(vrn, SicCodeAnswerService.key)
            DeregisterVatStub.successfulGetNoDataAnswer(vrn, ZeroRatedSuppliesValueService.key)

            VatSubscriptionStub.deregisterForVatSuccess()

            given.user.isAuthorised

            val response: WSResponse = postRequest

            VatSubscriptionStub.verifyDeregistration(exemptOnlyFullPayloadJson)

            response should have(
              httpStatus(SEE_OTHER),
              redirectURI("/vat-through-software/account/cancel-vat/cancel-vat-request-received")
            )
          }
        }
      }

      "deregistration is unsuccessful" should {

        "return ISE" in {

          DeregisterVatStub.successfulGetAnswer(vrn, DeregReasonAnswerService.key)(Json.toJson(BelowThreshold))
          DeregisterVatStub.successfulGetAnswer(vrn, TaxableTurnoverAnswerService.key)(Json.toJson(Yes))
          DeregisterVatStub.successfulGetAnswer(vrn, NextTaxableTurnoverAnswerService.key)(Json.toJson(taxableTurnoverBelow))
          DeregisterVatStub.successfulGetAnswer(vrn, WhyTurnoverBelowAnswerService.key)(Json.toJson(whyTurnoverBelowModel))
          DeregisterVatStub.successfulGetAnswer(vrn, AccountingMethodAnswerService.key)(Json.toJson(CashAccounting))
          DeregisterVatStub.successfulGetAnswer(vrn, StocksAnswerService.key)(Json.toJson(yesNoAmountYes))
          DeregisterVatStub.successfulGetAnswer(vrn, CapitalAssetsAnswerService.key)(Json.toJson(yesNoAmountYes))
          DeregisterVatStub.successfulGetAnswer(vrn, OptionTaxAnswerService.key)(Json.toJson(yesNoAmountYes))
          DeregisterVatStub.successfulGetAnswer(vrn, IssueNewInvoicesAnswerService.key)(Json.toJson(Yes))
          DeregisterVatStub.successfulGetAnswer(vrn, OutstandingInvoicesAnswerService.key)(Json.toJson(Yes))
          DeregisterVatStub.successfulGetAnswer(vrn, ChooseDeregDateAnswerService.key)(Json.toJson(Yes))
          DeregisterVatStub.successfulGetAnswer(vrn, DeregDateAnswerService.key)(Json.toJson(dateModel))
          DeregisterVatStub.successfulGetNoDataAnswer(vrn, CeasedTradingDateAnswerService.key)
          DeregisterVatStub.successfulGetNoDataAnswer(vrn, PurchasesExceedSuppliesAnswerService.key)
          DeregisterVatStub.successfulGetNoDataAnswer(vrn, SicCodeAnswerService.key)
          DeregisterVatStub.successfulGetNoDataAnswer(vrn, ZeroRatedSuppliesValueService.key)

          VatSubscriptionStub.deregisterForVatFailure()

          given.user.isAuthorised

          val response: WSResponse = postRequest

          response should have(
            httpStatus(INTERNAL_SERVER_ERROR)
          )
        }
      }
    }

    "user is unauthorised" should {

      "return 303 SEE_OTHER" in {

        given.user.isNotAuthenticated

        val response: WSResponse = postRequest

        response should have(
          httpStatus(SEE_OTHER),
          redirectURI(appConfig.signInUrl)
        )
      }
    }
  }
}
