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

import assets.constants.BaseTestConstants.vrn
import assets.constants.CustomerDetailsTestConstants.customerDetailsMax
import assets.messages.{DeregistrationConfirmationMessages => Messages}
import common.SessionKeys
import models.{DeregisterVatSuccess, ErrorModel}
import org.jsoup.Jsoup
import play.api.http.Status
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.mocks.{MockAuditService, MockCustomerDetailsService, MockDeleteAllStoredAnswersService}
import views.html.DeregistrationConfirmation


class DeregistrationConfirmationControllerSpec extends ControllerBaseSpec with MockDeleteAllStoredAnswersService
  with MockCustomerDetailsService with MockAuditService {

  lazy val deregistrationConfirmation: DeregistrationConfirmation = injector.instanceOf[DeregistrationConfirmation]

  object TestDeregistrationConfirmationController
    extends DeregistrationConfirmationController(
      deregistrationConfirmation,
      mcc,
      mockAuthPredicate,
      mockDeleteAllStoredAnswersService,
      serviceErrorHandler,
      mockCustomerDetailsService
    )

 lazy val requestWithSession: FakeRequest[AnyContentAsEmpty.type] = request.withSession(SessionKeys.deregSuccessful -> "true")

  "the user is authorised" when {

    "Calling the .show action" when {

      "User has successful dereg session key" when {

        lazy val result = {
          setupMockDeleteAllStoredAnswers(Right(DeregisterVatSuccess))
          mockAuthResult(mockAuthorisedIndividual)
          setupMockCustomerDetails(vrn)(Right(customerDetailsMax))
          TestDeregistrationConfirmationController.show()(requestWithSession)
        }
        lazy val document = Jsoup.parse(contentAsString(result))

        "return 200 (OK)" in {
          status(result) shouldBe Status.OK
        }

        "return HTML" in {
          contentType(result) shouldBe Some("text/html")
          charset(result) shouldBe Some("utf-8")
        }

        "return the correct first paragraph" in {
          messages(document.getElementsByClass("govuk-body").first().text()) shouldBe Messages.emailPreference
        }
      }

      "user does not have session key" should {
        lazy val result = {
          mockAuthResult(mockAuthorisedIndividual)
          TestDeregistrationConfirmationController.show()(request)
        }

        "return 303" in {
          status(result) shouldBe Status.SEE_OTHER
        }

        "redirect to Deregister for VAT controller" in {
          redirectLocation(result) shouldBe Some(controllers.routes.DeregisterForVATController.show().url)
        }
      }
    }

    "answers are deleted successfully and an error is received for CustomerDetails call" should {

      lazy val result = TestDeregistrationConfirmationController.show()(requestWithSession)
      lazy val document = Jsoup.parse(contentAsString(result))

      "return 200 (OK)" in {
        setupMockDeleteAllStoredAnswers(Right(DeregisterVatSuccess))
        mockAuthResult(mockAuthorisedIndividual)
        setupMockCustomerDetails(vrn)(Left(ErrorModel(INTERNAL_SERVER_ERROR, "bad things")))
        status(result) shouldBe Status.OK
      }

      "return HTML" in {
        contentType(result) shouldBe Some("text/html")
        charset(result) shouldBe Some("utf-8")
      }

      "return the correct first paragraph" in {
        messages(document.getElementsByClass("govuk-body").first().text()) shouldBe Messages.contactPrefError
      }
    }

    "answers are not deleted successfully should return internal server error" in {
      lazy val result = TestDeregistrationConfirmationController.show()(requestWithSession)
      setupMockDeleteAllStoredAnswers(Left(ErrorModel(INTERNAL_SERVER_ERROR, "bad things")))
      mockAuthResult(mockAuthorisedIndividual)
      status(result) shouldBe Status.INTERNAL_SERVER_ERROR
    }
  }
}
