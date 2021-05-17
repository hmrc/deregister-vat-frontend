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

import assets.constants.BaseTestConstants.errorModel
import forms.YesNoForm.yesNo
import models._
import play.api.http.Status
import play.api.mvc.AnyContentAsFormUrlEncoded
import play.api.test.FakeRequest
import play.api.test.Helpers.{contentType, _}
import services.mocks._
import views.html.OutstandingInvoices

import scala.concurrent.Future

class OutstandingInvoicesControllerSpec extends ControllerBaseSpec
  with MockWipeRedundantDataService with MockOutstandingInvoicesService with MockDeregReasonAnswerService with MockCapitalAssetsAnswerService {

  lazy val outstandingInvoices: OutstandingInvoices = injector.instanceOf[OutstandingInvoices]

  object TestOutstandingInvoicesController extends OutstandingInvoicesController(
    outstandingInvoices,
    mcc,
    mockAuthPredicate,
    mockRegistrationStatusPredicate,
    mockOutstandingInvoicesService,
    mockDeregReasonAnswerService,
    mockCapitalAssetsAnswerService,
    mockWipeRedundantDataService,
    serviceErrorHandler,
    ec,
    mockConfig
  )

  "Calling .show" when {

    "user is authorised" when {

      "the user does not have a pre selected option" should {

        lazy val result = TestOutstandingInvoicesController.show()(request)

        "return 200 (OK)" in {
          mockAuthResult(mockAuthorisedIndividual)
          setupMockGetOutstandingInvoices(Future.successful(Right(None)))
          status(result) shouldBe Status.OK
        }

        "return HTML" in {
          contentType(result) shouldBe Some("text/html")
          charset(result) shouldBe Some("utf-8")
        }
      }

      "the user is has pre selected option" should {

        lazy val result = TestOutstandingInvoicesController.show()(request)

        "return 200 (OK)" in {
          mockAuthResult(mockAuthorisedIndividual)
          setupMockGetOutstandingInvoices(Future.successful(Right(Some(Yes))))
          status(result) shouldBe Status.OK
        }

        "return HTML" in {
          contentType(result) shouldBe Some("text/html")
          charset(result) shouldBe Some("utf-8")
        }

        "has the Yes radio option checked" in {
          document(result).select(s"#$yesNo").hasAttr("checked") shouldBe true
        }
      }

      authChecks(".show", TestOutstandingInvoicesController.show(), request)
    }
  }

  "Calling .submit" when {

    "a success response is returned from the Wipe Redundant Data service and" when {

      "user selects 'Yes'" should {

        lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] = requestPost.withFormUrlEncodedBody((yesNo, "yes"))
        lazy val result = TestOutstandingInvoicesController.submit()(request)

        "return 303 (SEE OTHER)" in {
          setupMockStoreOutstandingInvoices(Yes)(Future.successful(Right(DeregisterVatSuccess)))
          setupMockGetCapitalAssets(Future.successful(Right(None)))
          setupMockGetDeregReason(Future.successful(Right(Some(BelowThreshold))))
          setupMockWipeRedundantData(Future.successful(Right(DeregisterVatSuccess)))

          mockAuthResult(mockAuthorisedIndividual)
          status(result) shouldBe Status.SEE_OTHER
        }

        s"redirect to ${controllers.routes.ChooseDeregistrationDateController.show()}" in {
          redirectLocation(result) shouldBe Some(controllers.routes.ChooseDeregistrationDateController.show().url)
        }
      }

      "user selects 'No'" when {

        "user is on 'below threshold' journey" should {

          lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] = requestPost.withFormUrlEncodedBody((yesNo, "no"))
          lazy val result = TestOutstandingInvoicesController.submit()(request)

          "return 303 (SEE OTHER)" in {
            setupMockGetDeregReason(Future.successful(Right(Some(BelowThreshold))))
            setupMockStoreOutstandingInvoices(No)(Future.successful(Right(DeregisterVatSuccess)))
            setupMockGetCapitalAssets(Future.successful(Right(None)))
            setupMockWipeRedundantData(Future.successful(Right(DeregisterVatSuccess)))

            mockAuthResult(mockAuthorisedIndividual)
            status(result) shouldBe Status.SEE_OTHER
          }

          s"redirect to ${controllers.routes.ChooseDeregistrationDateController.show()}" in {
            redirectLocation(result) shouldBe Some(controllers.routes.ChooseDeregistrationDateController.show().url)
          }
        }

        "user is on 'zero rated' journey" should {

          lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] = requestPost.withFormUrlEncodedBody((yesNo, "no"))
          lazy val result = TestOutstandingInvoicesController.submit()(request)

          "return 303 (SEE OTHER)" in {
            setupMockGetDeregReason(Future.successful(Right(Some(ZeroRated))))
            setupMockStoreOutstandingInvoices(No)(Future.successful(Right(DeregisterVatSuccess)))
            setupMockGetCapitalAssets(Future.successful(Right(None)))
            setupMockWipeRedundantData(Future.successful(Right(DeregisterVatSuccess)))

            mockAuthResult(mockAuthorisedIndividual)
            status(result) shouldBe Status.SEE_OTHER
          }

          s"redirect to ${controllers.routes.ChooseDeregistrationDateController.show()}" in {
            redirectLocation(result) shouldBe Some(controllers.routes.ChooseDeregistrationDateController.show().url)
          }
        }

        "user is on 'exemptOnly' journey" should {

          lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] = requestPost.withFormUrlEncodedBody((yesNo, "no"))
          lazy val result = TestOutstandingInvoicesController.submit()(request)

          "return 303 (SEE OTHER)" in {
            setupMockGetDeregReason(Future.successful(Right(Some(ExemptOnly))))
            setupMockStoreOutstandingInvoices(No)(Future.successful(Right(DeregisterVatSuccess)))
            setupMockGetCapitalAssets(Future.successful(Right(None)))
            setupMockWipeRedundantData(Future.successful(Right(DeregisterVatSuccess)))

            mockAuthResult(mockAuthorisedIndividual)
            status(result) shouldBe Status.SEE_OTHER
          }

          s"redirect to ${controllers.routes.ChooseDeregistrationDateController.show()}" in {
            redirectLocation(result) shouldBe Some(controllers.routes.ChooseDeregistrationDateController.show().url)
          }
        }

        "user is on 'ceased trading' journey" when {

          "user answered 'Yes' to having capital assets" should {

            lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] = requestPost.withFormUrlEncodedBody((yesNo, "no"))
            lazy val result = TestOutstandingInvoicesController.submit()(request)

            val capitalAssetsAmount: Int = 1000

            "return 303 (SEE OTHER)" in {
              setupMockGetDeregReason(Future.successful(Right(Some(Ceased))))
              setupMockGetCapitalAssets(Future.successful(Right(Some(YesNoAmountModel(Yes, Some(capitalAssetsAmount))))))
              setupMockStoreOutstandingInvoices(No)(Future.successful(Right(DeregisterVatSuccess)))
              setupMockWipeRedundantData(Future.successful(Right(DeregisterVatSuccess)))

              mockAuthResult(mockAuthorisedIndividual)
              status(result) shouldBe Status.SEE_OTHER
            }

            s"redirect to ${controllers.routes.ChooseDeregistrationDateController.show()}" in {
              redirectLocation(result) shouldBe Some(controllers.routes.ChooseDeregistrationDateController.show().url)
            }
          }

          "user answered 'No' to having capital assets" should {

            lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
              requestPost.withFormUrlEncodedBody((yesNo, "no"))
            lazy val result = TestOutstandingInvoicesController.submit()(request)

            "return 303 (SEE OTHER)" in {
              setupMockStoreOutstandingInvoices(No)(Future.successful(Right(DeregisterVatSuccess)))
              setupMockGetDeregReason(Future.successful(Right(Some(Ceased))))
              setupMockGetCapitalAssets(Future.successful(Right(Some(YesNoAmountModel(No, None)))))
              setupMockWipeRedundantData(Future.successful(Right(DeregisterVatSuccess)))

              mockAuthResult(mockAuthorisedIndividual)
              status(result) shouldBe Status.SEE_OTHER
            }

            s"redirect to ${controllers.routes.CheckAnswersController.show()}" in {
              redirectLocation(result) shouldBe Some(controllers.routes.CheckAnswersController.show().url)
            }
          }

          "no answer is returned for capital assets" should {

            lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
              requestPost.withFormUrlEncodedBody((yesNo, "no"))
            lazy val result = TestOutstandingInvoicesController.submit()(request)

            "return 500 (ISE)" in {
              setupMockStoreOutstandingInvoices(No)(Future.successful(Right(DeregisterVatSuccess)))
              setupMockGetDeregReason(Future.successful(Right(Some(Ceased))))
              setupMockGetCapitalAssets(Future.successful(Right(None)))
              setupMockWipeRedundantData(Future.successful(Right(DeregisterVatSuccess)))

              mockAuthResult(mockAuthorisedIndividual)
              status(result) shouldBe Status.INTERNAL_SERVER_ERROR
            }
          }

          "error is returned for capital assets" should {

            lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
              requestPost.withFormUrlEncodedBody((yesNo, "no"))
            lazy val result = TestOutstandingInvoicesController.submit()(request)

            "return 500 (ISE)" in {
              setupMockStoreOutstandingInvoices(No)(Future.successful(Right(DeregisterVatSuccess)))
              setupMockGetCapitalAssets(Future.successful(Left(errorModel)))
              setupMockWipeRedundantData(Future.successful(Right(DeregisterVatSuccess)))

              mockAuthResult(mockAuthorisedIndividual)
              status(result) shouldBe Status.INTERNAL_SERVER_ERROR
            }
          }
        }

        "no answer is returned for 'deregistration reason'" should {

          lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] = requestPost.withFormUrlEncodedBody((yesNo, "no"))
          lazy val result = TestOutstandingInvoicesController.submit()(request)


          "return 500 (ISE)" in {

            setupMockStoreOutstandingInvoices(No)(Future.successful(Right(DeregisterVatSuccess)))
            setupMockGetDeregReason(Future.successful(Right(None)))
            setupMockGetCapitalAssets(Future.successful(Right(Some(YesNoAmountModel(No, None)))))
            setupMockWipeRedundantData(Future.successful(Right(DeregisterVatSuccess)))

            mockAuthResult(mockAuthorisedIndividual)
            status(result) shouldBe Status.INTERNAL_SERVER_ERROR
          }
        }

        "an error is returned for 'deregistration reason'" should {

          lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] = requestPost.withFormUrlEncodedBody((yesNo, "no"))
          lazy val result = TestOutstandingInvoicesController.submit()(request)


          "return 500 (ISE)" in {
            setupMockStoreOutstandingInvoices(No)(Future.successful(Right(DeregisterVatSuccess)))
            setupMockGetDeregReason(Future.successful(Left(errorModel)))
            setupMockGetCapitalAssets(Future.successful(Right(Some(YesNoAmountModel(No, None)))))
            setupMockWipeRedundantData(Future.successful(Right(DeregisterVatSuccess)))

            mockAuthResult(mockAuthorisedIndividual)
            status(result) shouldBe Status.INTERNAL_SERVER_ERROR
          }
        }
      }
    }

    "an error response is returned from the Wipe Redundant Data service" should {

      lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] = requestPost.withFormUrlEncodedBody((yesNo, "no"))
      lazy val result = TestOutstandingInvoicesController.submit()(request)

      "return 500 (ISE)" in {
        setupMockStoreOutstandingInvoices(No)(Future.successful(Right(DeregisterVatSuccess)))
        setupMockWipeRedundantData(Future.successful(Left(errorModel)))

        mockAuthResult(mockAuthorisedIndividual)
        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
      }

    }

    "an error is returned when storing the answer" should {

      lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] = requestPost.withFormUrlEncodedBody((yesNo, "no"))
      lazy val result = TestOutstandingInvoicesController.submit()(request)

      "return 500 (ISE)" in {
        setupMockStoreOutstandingInvoices(No)(Future.successful(Left(errorModel)))
        mockAuthResult(mockAuthorisedIndividual)
        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
      }
    }

    "the user submits without selecting an option" should {

      lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] = requestPost.withFormUrlEncodedBody(("yes_no", ""))
      lazy val result = TestOutstandingInvoicesController.submit()(request)

      "return 400 (BAD REQUEST)" in {
        mockAuthResult(mockAuthorisedIndividual)
        status(result) shouldBe Status.BAD_REQUEST
      }

      "return HTML" in {
        contentType(result) shouldBe Some("text/html")
        charset(result) shouldBe Some("utf-8")
      }
    }

    authChecks(".submit", TestOutstandingInvoicesController.submit(), requestPost.withFormUrlEncodedBody(("yes_no", "no")))
  }
}
