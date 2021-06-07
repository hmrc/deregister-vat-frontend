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

package controllers

import models.{DeregisterVatSuccess, ErrorModel, WhyTurnoverBelowModel}
import org.jsoup.Jsoup
import play.api.http.Status
import play.api.mvc.AnyContentAsFormUrlEncoded
import play.api.test.FakeRequest
import play.api.test.Helpers.{contentType, _}
import services.mocks.MockWhyTurnoverBelowAnswerService
import views.html.WhyTurnoverBelow

class WhyTurnoverBelowControllerSpec extends ControllerBaseSpec with MockWhyTurnoverBelowAnswerService {

  lazy val whyTurnoverBelow: WhyTurnoverBelow = injector.instanceOf[WhyTurnoverBelow]

  object TestWhyTurnoverBelowController extends WhyTurnoverBelowController(
    whyTurnoverBelow,
    mcc,
    mockAuthPredicate,
    mockRegistrationStatusPredicate,
    mockWhyTurnoverBelowAnswerService,
    serviceErrorHandler,
    ec,
    mockConfig
  )

  "the user is authorised" when {

    "Calling the .show action" when {

      "the user does not have any checkboxes pre selected" should {

        lazy val result = TestWhyTurnoverBelowController.show()(request)

        "return 200 (OK)" in {
          setupMockGetWhyTurnoverBelow(Right(None))
          mockAuthResult(mockAuthorisedIndividual)
          status(result) shouldBe Status.OK
        }

        "return HTML" in {
          contentType(result) shouldBe Some("text/html")
          charset(result) shouldBe Some("utf-8")
        }
      }

      "the user is has checkboxes pre-selected" should {

        lazy val result = TestWhyTurnoverBelowController.show()(request)

        "return 200 (OK)" in {
          setupMockGetWhyTurnoverBelow(Right(Some(WhyTurnoverBelowModel(true,true,true,true,true,true,true))))
          mockAuthResult(mockAuthorisedIndividual)
          status(result) shouldBe Status.OK
        }

        "return HTML" in {
          contentType(result) shouldBe Some("text/html")
          charset(result) shouldBe Some("utf-8")
        }

        "have the boxes displayed as selected" in {
          Jsoup.parse(contentAsString(result)).select("#lostContract").hasAttr("checked") shouldBe true
          Jsoup.parse(contentAsString(result)).select("#semiRetiring").hasAttr("checked") shouldBe true
          Jsoup.parse(contentAsString(result)).select("#moreCompetitors").hasAttr("checked") shouldBe true
          Jsoup.parse(contentAsString(result)).select("#reducedTradingHours").hasAttr("checked") shouldBe true
          Jsoup.parse(contentAsString(result)).select("#seasonalBusiness").hasAttr("checked") shouldBe true
          Jsoup.parse(contentAsString(result)).select("#closedPlacesOfBusiness").hasAttr("checked") shouldBe true
          Jsoup.parse(contentAsString(result)).select("#turnoverLowerThanExpected").hasAttr("checked") shouldBe true
        }
      }

      authChecks(".show", TestWhyTurnoverBelowController.show(), request)

    }

    "Calling the .submit action" when {

      val model: WhyTurnoverBelowModel = WhyTurnoverBelowModel(
        lostContract = true,
        semiRetiring = false,
        moreCompetitors = false,
        reducedTradingHours = false,
        seasonalBusiness = false,
        closedPlacesOfBusiness = false,
        turnoverLowerThanExpected = false
      )

      "the user submits after selecting at least one checkbox" should {

        lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
          requestPost.withFormUrlEncodedBody((WhyTurnoverBelowModel.lostContract, "true"))
        lazy val result = TestWhyTurnoverBelowController.submit()(request)

        "return 303 (SEE OTHER)" in {
          setupMockStoreWhyTurnoverBelow(model)(Right(DeregisterVatSuccess))
          mockAuthResult(mockAuthorisedIndividual)
          status(result) shouldBe Status.SEE_OTHER
        }

        s"redirect to ${controllers.routes.VATAccountsController.show().url}" in {
          redirectLocation(result) shouldBe Some(controllers.routes.VATAccountsController.show().url)
        }
      }

      "the user submits without selecting an option" should {

        lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
          requestPost.withFormUrlEncodedBody(("", ""))
        lazy val result = TestWhyTurnoverBelowController.submit()(request)

        "return 400 (BAD REQUEST)" in {
          mockAuthResult(mockAuthorisedIndividual)
          status(result) shouldBe Status.BAD_REQUEST
        }

        "return HTML" in {
          contentType(result) shouldBe Some("text/html")
          charset(result) shouldBe Some("utf-8")
        }
      }

      "storing the answer fails" should {

        lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
          requestPost.withFormUrlEncodedBody((WhyTurnoverBelowModel.lostContract, "true"))
        lazy val result = TestWhyTurnoverBelowController.submit()(request)

        "return Internal Server Error" in {

          setupMockStoreWhyTurnoverBelow(model)(Left(ErrorModel(INTERNAL_SERVER_ERROR, "")))
          mockAuthResult(mockAuthorisedIndividual)
          status(result) shouldBe INTERNAL_SERVER_ERROR
        }
      }
    }

    authChecks(".submit", TestWhyTurnoverBelowController.submit(), requestPost
      .withFormUrlEncodedBody((WhyTurnoverBelowModel.lostContract, "true")))
  }
}
