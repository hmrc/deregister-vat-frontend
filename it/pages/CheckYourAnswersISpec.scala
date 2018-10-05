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

import assets.IntegrationTestConstants.vrn
import helpers.IntegrationBaseSpec
import models._
import services._
import play.api.http.Status._
import play.api.libs.json.Json
import play.api.libs.ws.WSResponse
import stubs.DeregisterVatStub

class CheckYourAnswersISpec extends IntegrationBaseSpec {

  "Calling GET Check your answers" when {

    def getRequest(): WSResponse = get("/check-your-answers")

    val dateModel = DateModel(1,1,2018)
    val taxableTurnoverAbove = NextTaxableTurnoverModel(BigDecimal(90000))
    val taxableTurnoverBelow = NextTaxableTurnoverModel(BigDecimal(200))
    val whyTurnoverBelowAll = WhyTurnoverBelowModel(true,true,true,true,true,true,true)
    val yesNoAmountYes = YesNoAmountModel(Yes,Some(BigDecimal(1000)))
    val yesNoAmountNo = YesNoAmountModel(No,None)
    val deregistrationDate = DeregistrationDateModel(Yes,Some(DateModel(1,1,2018)))
    
    "the user is authorised" should {

      "return 200 OK" in {

        DeregisterVatStub.successfulGetAnswer(vrn, DeregReasonAnswerService.key)(Json.toJson(BelowThreshold))
        DeregisterVatStub.successfulGetAnswer(vrn, CeasedTradingDateAnswerService.key)(Json.toJson(dateModel))
        DeregisterVatStub.successfulGetAnswer(vrn, TaxableTurnoverAnswerService.key)(Json.toJson(Yes))
        DeregisterVatStub.successfulGetAnswer(vrn, NextTaxableTurnoverAnswerService.key)(Json.toJson(taxableTurnoverBelow))
        DeregisterVatStub.successfulGetAnswer(vrn, WhyTurnoverBelowAnswerService.key)(Json.toJson(whyTurnoverBelowAll))
        DeregisterVatStub.successfulGetAnswer(vrn, AccountingMethodAnswerService.key)(Json.toJson(StandardAccounting))
        DeregisterVatStub.successfulGetAnswer(vrn, StocksAnswerService.key)(Json.toJson(yesNoAmountYes))
        DeregisterVatStub.successfulGetAnswer(vrn, CapitalAssetsAnswerService.key)(Json.toJson(yesNoAmountYes))
        DeregisterVatStub.successfulGetAnswer(vrn, OptionTaxAnswerService.key)(Json.toJson(yesNoAmountNo))
        DeregisterVatStub.successfulGetAnswer(vrn, IssueNewInvoicesAnswerService.key)(Json.toJson(Yes))
        DeregisterVatStub.successfulGetAnswer(vrn, OutstandingInvoicesAnswerService.key)(Json.toJson(Yes))
        DeregisterVatStub.successfulGetAnswer(vrn, DeregDateAnswerService.key)(Json.toJson(deregistrationDate))

        given.user.isAuthorised

        val response: WSResponse = getRequest()

        response should have(
          httpStatus(OK),
          pageTitle("Check your answers")
        )
      }
    }

    "an error is returned from an answer" should {

      "return 500 INTERNAL SERVER ERROR" in {

        DeregisterVatStub.successfulGetAnswer(vrn, DeregReasonAnswerService.key)(Json.toJson(BelowThreshold))
        DeregisterVatStub.successfulGetAnswer(vrn, CeasedTradingDateAnswerService.key)(Json.toJson(dateModel))
        DeregisterVatStub.successfulGetAnswer(vrn, TaxableTurnoverAnswerService.key)(Json.toJson(Yes))
        DeregisterVatStub.successfulGetAnswer(vrn, NextTaxableTurnoverAnswerService.key)(Json.toJson(taxableTurnoverBelow))
        DeregisterVatStub.successfulGetAnswer(vrn, WhyTurnoverBelowAnswerService.key)(Json.toJson(whyTurnoverBelowAll))
        DeregisterVatStub.successfulGetAnswer(vrn, AccountingMethodAnswerService.key)(Json.toJson(StandardAccounting))
        DeregisterVatStub.successfulGetAnswer(vrn, StocksAnswerService.key)(Json.toJson(yesNoAmountYes))
        DeregisterVatStub.successfulGetAnswer(vrn, CapitalAssetsAnswerService.key)(Json.toJson(yesNoAmountYes))
        DeregisterVatStub.successfulGetAnswer(vrn, OptionTaxAnswerService.key)(Json.toJson(yesNoAmountNo))
        DeregisterVatStub.successfulGetAnswer(vrn, IssueNewInvoicesAnswerService.key)(Json.toJson(Yes))
        DeregisterVatStub.successfulGetAnswer(vrn, OutstandingInvoicesAnswerService.key)(Json.toJson(Yes))

        DeregisterVatStub.getAnswerError(vrn, DeregDateAnswerService.key)

        given.user.isAuthorised

        val response: WSResponse = getRequest()

        response should have(
          httpStatus(INTERNAL_SERVER_ERROR)
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
}
