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

package controllers

import models.{DeregisterVatSuccess, ErrorModel}
import play.api.http.Status
import play.api.test.Helpers._
import services.mocks.{MockAuditService, MockContactPreferencesService, MockCustomerDetailsService, MockDeleteAllStoredAnswersService}
import assets.constants.CustomerDetailsTestConstants.customerDetailsMax
import assets.constants.ContactPreferencesTestConstants.{contactPreferencesDigital, contactPreferencesPaper}
import assets.constants.BaseTestConstants.vrn
import org.jsoup.Jsoup
import assets.messages.{DeregistrationConfirmationMessages => Messages}
import scala.concurrent.Future

class DeregistrationConfirmationControllerSpec extends ControllerBaseSpec with MockDeleteAllStoredAnswersService
  with MockCustomerDetailsService with MockContactPreferencesService with MockAuditService {

  object TestDeregistrationConfirmationController
    extends DeregistrationConfirmationController(
      messagesApi,
      mockAuthPredicate,
      mockDeleteAllStoredAnswersService,
      serviceErrorHandler,
      mockCustomerDetailsService,
      mockAuditService,
      mockContactPreferencesService,
      mockConfig)

  "the user is authorised" when {

    "Calling the .show action" when {

      "answers are deleted successfully and a customerDetails is received" when {

        "the useContactPreferences feature is disabled" should {
          lazy val result = {
            mockConfig.features.useContactPreferences(false)
            TestDeregistrationConfirmationController.show()(request)
          }
          lazy val document = Jsoup.parse(bodyOf(result))

          "return 200 (OK)" in {
            setupMockDeleteAllStoredAnswers(Right(DeregisterVatSuccess))
            mockAuthResult(Future.successful(mockAuthorisedIndividual))
            setupMockCustomerDetails(vrn)(Right(customerDetailsMax))
            status(result) shouldBe Status.OK
          }

          "return HTML" in {
            contentType(result) shouldBe Some("text/html")
            charset(result) shouldBe Some("utf-8")
          }

          "return the correct first paragraph" in {
            document.getElementById("content").getElementsByTag("p").first().text() shouldBe Messages.p1contactPrefDisabled
          }
        }

        "the useContactPreferences feature is enabled and set to 'DIGITAL'" should {
          lazy val result = {
            mockConfig.features.useContactPreferences(true)
            TestDeregistrationConfirmationController.show()(request)
          }
          lazy val document = Jsoup.parse(bodyOf(result))

          "return 200 (OK)" in {
            setupMockDeleteAllStoredAnswers(Right(DeregisterVatSuccess))
            mockAuthResult(Future.successful(mockAuthorisedIndividual))
            setupMockContactPreferences(vrn)(Right(contactPreferencesDigital))
            setupMockCustomerDetails(vrn)(Right(customerDetailsMax))
            setupAuditExtendedEvent

            status(result) shouldBe Status.OK
          }

          "return HTML" in {
            contentType(result) shouldBe Some("text/html")
            charset(result) shouldBe Some("utf-8")
          }

          "return the correct first paragraph" in {
            document.getElementById("content").getElementsByTag("p").first().text() shouldBe Messages.digitalPreference
          }
        }

      }

      "answers are deleted successfully and an error is received for CustomerDetails call" when {

        "'useContactPreference' is disabled" should {

          lazy val result = {
            mockConfig.features.useContactPreferences(false)
            TestDeregistrationConfirmationController.show()(request)
          }
          lazy val document = Jsoup.parse(bodyOf(result))

          "return 200 (OK)" in {
            setupMockDeleteAllStoredAnswers(Right(DeregisterVatSuccess))
            mockAuthResult(Future.successful(mockAuthorisedIndividual))
            setupMockCustomerDetails(vrn)(Left(ErrorModel(INTERNAL_SERVER_ERROR, "bad things")))
            status(result) shouldBe Status.OK
          }

          "return HTML" in {
            contentType(result) shouldBe Some("text/html")
            charset(result) shouldBe Some("utf-8")
          }

          "return the correct first paragraph" in {
            document.getElementById("content").getElementsByTag("p").first().text() shouldBe Messages.p1contactPrefDisabled
          }
        }

        "'useContactPreference' is enabled and set to 'PAPER'" should {

          lazy val result = {
            mockConfig.features.useContactPreferences(true)
            TestDeregistrationConfirmationController.show()(request)
          }
          lazy val document = Jsoup.parse(bodyOf(result))

          "return 200 (OK)" in {
            setupMockDeleteAllStoredAnswers(Right(DeregisterVatSuccess))
            mockAuthResult(Future.successful(mockAuthorisedIndividual))
            setupMockContactPreferences(vrn)(Right(contactPreferencesPaper))
            setupMockCustomerDetails(vrn)(Left(ErrorModel(INTERNAL_SERVER_ERROR, "bad things")))
            status(result) shouldBe Status.OK
          }

          "return HTML" in {
            contentType(result) shouldBe Some("text/html")
            charset(result) shouldBe Some("utf-8")
          }

          "return the correct first paragraph" in {
            document.getElementById("content").getElementsByTag("p").first().text() shouldBe Messages.paperPreference
          }
        }

        "'useContactPreference' and returns an error" should {

          lazy val result = {
            mockConfig.features.useContactPreferences(true)
            TestDeregistrationConfirmationController.show()(request)
          }
          lazy val document = Jsoup.parse(bodyOf(result))

          "return 200 (OK)" in {
            setupMockDeleteAllStoredAnswers(Right(DeregisterVatSuccess))
            mockAuthResult(Future.successful(mockAuthorisedIndividual))
            setupMockContactPreferences(vrn)(Left(ErrorModel(Status.INTERNAL_SERVER_ERROR, "I got nothing")))
            setupMockCustomerDetails(vrn)(Left(ErrorModel(INTERNAL_SERVER_ERROR, "bad things")))
            status(result) shouldBe Status.OK
          }

          "return HTML" in {
            contentType(result) shouldBe Some("text/html")
            charset(result) shouldBe Some("utf-8")
          }

          "return the correct first paragraph" in {
            document.getElementById("content").getElementsByTag("p").first().text() shouldBe Messages.contactPrefError
          }
        }
      }
    }

    lazy val result3 = TestDeregistrationConfirmationController.show()(request)

    "throw an ISE if there's an error deleting the stored answers" in {
      setupMockDeleteAllStoredAnswers(Left(ErrorModel(INTERNAL_SERVER_ERROR, "bad things")))
      mockAuthResult(Future.successful(mockAuthorisedIndividual))
      setupMockContactPreferences(vrn)(Right(contactPreferencesDigital))
      setupMockCustomerDetails(vrn)(Right(customerDetailsMax))
      status(result3) shouldBe Status.INTERNAL_SERVER_ERROR
    }
  }

}
