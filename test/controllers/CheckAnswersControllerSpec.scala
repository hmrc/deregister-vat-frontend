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

import assets.constants.CheckYourAnswersTestConstants._
import assets.constants.NextTaxableTurnoverTestConstants._
import assets.constants.WhyTurnoverBelowTestConstants._
import assets.constants.YesNoAmountTestConstants._
import common.{Constants, SessionKeys}
import models._
import play.api.http.Status
import play.api.test.Helpers.{contentType, _}
import services.mocks.{MockCheckAnswersService, MockChooseDeregDateAnswerService, MockUpdateDeregistrationService}
import views.html.CheckYourAnswers

import scala.concurrent.Future

class CheckAnswersControllerSpec
  extends ControllerBaseSpec
    with MockCheckAnswersService
    with MockChooseDeregDateAnswerService
    with MockUpdateDeregistrationService {

  lazy val checkYourAnswers: CheckYourAnswers = injector.instanceOf[CheckYourAnswers]

  object TestCheckAnswersController extends CheckAnswersController(
    checkYourAnswers,
    mcc,
    mockAuthPredicate,
    mockRegistrationStatusPredicate,
    mockCheckAnswersService,
    mockUpdateDeregistrationService,
    serviceErrorHandler,
    mockConfig,
    ec
  )

  "the user is authorised" when {

    "Calling the .show action" when {

      "a Right(CheckYourAnswersModel) is returned" should {

        lazy val result = TestCheckAnswersController.show()(request)

        "return 200 (OK)" in {
          setupMockCheckYourAnswersModel(Right(
            CheckYourAnswersModel(
              Some(Ceased),
              Some(dateModel),
              Some(Yes),
              Some(nextTaxableTurnoverBelow),
              Some(whyTurnoverBelowAll),
              Some(StandardAccounting),
              Some(yesNoAmountNo),
              Some(yesNoAmountNo),
              Some(yesNoAmountNo),
              Some(Yes),
              Some(Yes),
              Some(Yes),
              Some(dateModel),
              Some(Yes),
              Some(sicCodeValue),
              Some(zeroRatedSuppliesValue),
              Some(Yes)
            )
          ))
          mockAuthResult(Future.successful(mockAuthorisedIndividual))
          status(result) shouldBe Status.OK
        }

        "return HTML" in {
          contentType(result) shouldBe Some("text/html")
          charset(result) shouldBe Some("utf-8")
        }
      }

      "No deregistration date for DeregDateAnswerService is returned" should {

        lazy val result = TestCheckAnswersController.show()(request)

        "return 200 (OK)" in {
          setupMockCheckYourAnswersModel(Right(
            CheckYourAnswersModel(
              Some(Ceased),
              Some(dateModel),
              Some(Yes),
              Some(nextTaxableTurnoverBelow),
              Some(whyTurnoverBelowAll),
              Some(StandardAccounting),
              Some(yesNoAmountNo),
              Some(yesNoAmountNo),
              Some(yesNoAmountNo),
              Some(Yes),
              Some(Yes),
              Some(No),
              None,
              Some(Yes),
              Some(sicCodeValue),
              Some(zeroRatedSuppliesValue),
              Some(Yes)
            )
          ))
          mockAuthResult(Future.successful(mockAuthorisedIndividual))
          status(result) shouldBe Status.OK
        }

        "return HTML" in {
          contentType(result) shouldBe Some("text/html")
          charset(result) shouldBe Some("utf-8")
        }
      }

      "No option for ChooseDeregDateAnswerService is returned" should {

        lazy val result = TestCheckAnswersController.show()(request)

        "return 200 (OK)" in {
          setupMockCheckYourAnswersModel(Right(
            CheckYourAnswersModel(
              Some(Ceased),
              Some(dateModel),
              Some(Yes),
              Some(nextTaxableTurnoverBelow),
              Some(whyTurnoverBelowAll),
              Some(StandardAccounting),
              Some(yesNoAmountNo),
              Some(yesNoAmountNo),
              Some(yesNoAmountNo),
              Some(Yes),
              Some(Yes),
              None,
              None,
              Some(Yes),
              Some(sicCodeValue),
              Some(zeroRatedSuppliesValue),
              Some(Yes)
            )
          ))
          mockAuthResult(Future.successful(mockAuthorisedIndividual))
          status(result) shouldBe Status.OK
        }

        "return HTML" in {
          contentType(result) shouldBe Some("text/html")
          charset(result) shouldBe Some("utf-8")
        }
      }

      "a Left(ErrorModel) is returned" should {

        lazy val result = TestCheckAnswersController.show()(request)

        "return 500 (INTERNAL SERVER ERROR)" in {
          setupMockCheckYourAnswersModel(Left(ErrorModel(INTERNAL_SERVER_ERROR,"Error")))
          mockAuthResult(Future.successful(mockAuthorisedIndividual))
          status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        }
      }

      authChecks(".show", TestCheckAnswersController.show(), request)
    }

    "calling the submit action" when {

      "a Right(VatSubscriptionSuccess) is returned from updateDeregistrationService" should {

        lazy val result = TestCheckAnswersController.submit()(request)

        "return 303 (see other)" in {
          setupUpdateDeregistration(Right(VatSubscriptionSuccess))
          mockAuthResult(Future.successful(mockAuthorisedIndividual))
          status(result) shouldBe Status.SEE_OTHER
        }

        "redirect to the correct page" in {
          redirectLocation(result) shouldBe Some(controllers.routes.DeregistrationConfirmationController.show().url)
        }
        "add the pending dereg value to the session" in {
          session(result).get(SessionKeys.registrationStatusKey) shouldBe Some(Constants.pending)
        }

        "add the successful dereg value to the session" in {
          session(result).get(SessionKeys.deregSuccessful) shouldBe Some("true")
        }
      }

      "a Left(ErrorModel) is returned from updateDeregistrationService" should {

        lazy val result = TestCheckAnswersController.submit()(request)

        "return 200 (OK)" in {
          setupUpdateDeregistration(Left(ErrorModel(Status.INTERNAL_SERVER_ERROR,"error message")))
          mockAuthResult(Future.successful(mockAuthorisedIndividual))
          status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        }
      }
    }
  }
}
