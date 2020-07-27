/*
 * Copyright 2020 HM Revenue & Customs
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
import models._
import org.jsoup.Jsoup
import play.api.http.Status
import play.api.mvc.AnyContentAsFormUrlEncoded
import play.api.test.FakeRequest
import play.api.test.Helpers.{contentType, _}
import services.mocks.{MockAccountingMethodAnswerService, MockDeregReasonAnswerService, MockTaxableTurnoverAnswerService}
import views.html.VatAccounts

import scala.concurrent.Future

class VATAccountsControllerSpec extends ControllerBaseSpec with MockAccountingMethodAnswerService
                                with MockDeregReasonAnswerService with MockTaxableTurnoverAnswerService {

  lazy val vatAccounts: VatAccounts = injector.instanceOf[VatAccounts]

  object TestVATAccountsController extends VATAccountsController(
    vatAccounts,
    mcc,
    mockAuthPredicate,
    mockRegistrationStatusPredicate,
    mockAccountingMethodAnswerService,
    mockDeregReasonAnswerService,
    mockTaxableTurnoverAnswerService,
    serviceErrorHandler,
    ec,
    mockConfig
  )

  "the user is authorised" when {

    "Calling the .show action" when {

      "the user does not have a pre selected option" should {

        lazy val result = TestVATAccountsController.show()(request)

        "return 200 (OK)" in {
          setupMockGetDeregReason(Right(Some(Ceased)))
          setupMockGetAccountingMethod(Right(None))
          setupMockGetTaxableTurnover(Right(None))
          mockAuthResult(Future.successful(mockAuthorisedIndividual))
          status(result) shouldBe Status.OK
        }

        "return HTML" in {
          contentType(result) shouldBe Some("text/html")
          charset(result) shouldBe Some("utf-8")
        }
      }

      "the user is has previously entered values" should {

        lazy val result = TestVATAccountsController.show()(request)

        "return 200 (OK)" in {
          setupMockGetDeregReason(Right(Some(BelowThreshold)))
          setupMockGetAccountingMethod(Right(Some(StandardAccounting)))
          setupMockGetTaxableTurnover(Right(None))
          mockAuthResult(Future.successful(mockAuthorisedIndividual))
          status(result) shouldBe Status.OK
        }

        "return HTML" in {
          contentType(result) shouldBe Some("text/html")
          charset(result) shouldBe Some("utf-8")
        }

        s"have the correct value 'standard' for the accounting method" in {
          Jsoup.parse(bodyOf(result)).select("#accountingMethod-standard").hasAttr("checked") shouldBe true
        }
      }

      "an error is returned from the DeregReasonAnswerService" should {

        lazy val result = TestVATAccountsController.show()(request)

        "return 200 (OK)" in {
          setupMockGetDeregReason(Left(ErrorModel(INTERNAL_SERVER_ERROR, "error")))
          setupMockGetAccountingMethod(Right(Some(StandardAccounting)))
          setupMockGetTaxableTurnover(Right(None))
          mockAuthResult(Future.successful(mockAuthorisedIndividual))
          status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        }
      }

      "an error is returned from the AccountingMethodAnswerService" should {

        lazy val result = TestVATAccountsController.show()(request)

        "return 200 (OK)" in {
          setupMockGetDeregReason(Right(Some(Ceased)))
          setupMockGetAccountingMethod(Left(ErrorModel(INTERNAL_SERVER_ERROR, "error")))
          setupMockGetTaxableTurnover(Right(None))
          mockAuthResult(Future.successful(mockAuthorisedIndividual))
          status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        }
      }
    }

    "Calling the .submit action" when {

      "the user submits after selecting the 'Standard accounting' option" should {

        lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
          FakeRequest("POST", "/").withFormUrlEncodedBody(("accountingMethod", "standard"))
        lazy val result = TestVATAccountsController.submit()(request)

        "return 303 (SEE OTHER)" in {
          setupMockStoreAccountingMethod(StandardAccounting)(Right(DeregisterVatSuccess))
          mockAuthResult(Future.successful(mockAuthorisedIndividual))
          status(result) shouldBe Status.SEE_OTHER
        }

        s"Redirect to the '${controllers.routes.OptionTaxController.show().url}'" in {
          redirectLocation(result) shouldBe Some(controllers.routes.OptionTaxController.show().url)
        }
      }

      "the user submits after selecting the 'Cash accounting' option" should {

        lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
          FakeRequest("POST", "/").withFormUrlEncodedBody(("accountingMethod", "cash"))
        lazy val result = TestVATAccountsController.submit()(request)

        "return 303 (SEE OTHER)" in {
          setupMockStoreAccountingMethod(CashAccounting)(Right(DeregisterVatSuccess))
          mockAuthResult(Future.successful(mockAuthorisedIndividual))
          status(result) shouldBe Status.SEE_OTHER
        }

        s"Redirect to the '${controllers.routes.OptionTaxController.show().url}'" in {
          redirectLocation(result) shouldBe Some(controllers.routes.OptionTaxController.show().url)
        }
      }

      "the user submits without selecting an option" should {

        lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
          FakeRequest("POST", "/").withFormUrlEncodedBody(("accountingMethod", ""))
        lazy val result = TestVATAccountsController.submit()(request)

        "return 400 (BAD REQUEST)" in {
          setupMockGetDeregReason(Right(Some(BelowThreshold)))
          setupMockGetTaxableTurnover(Right(None))
          mockAuthResult(Future.successful(mockAuthorisedIndividual))
          status(result) shouldBe Status.BAD_REQUEST
        }

        "return HTML" in {
          contentType(result) shouldBe Some("text/html")
          charset(result) shouldBe Some("utf-8")
        }
      }

      "the user submits without selecting an option and an error is returned from DeregReasonAnswerService" should {

        lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
          FakeRequest("POST", "/").withFormUrlEncodedBody(("accountingMethod", ""))
        lazy val result = TestVATAccountsController.submit()(request)

        "return 400 (BAD REQUEST)" in {
          setupMockGetDeregReason(Left(ErrorModel(INTERNAL_SERVER_ERROR,"error")))
          setupMockGetTaxableTurnover(Right(None))
          mockAuthResult(Future.successful(mockAuthorisedIndividual))
          status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        }
      }
    }

    "if an error is returned when storing" should {

      lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] =
        FakeRequest("POST", "/").withFormUrlEncodedBody(("accountingMethod", "cash"))
      lazy val result = TestVATAccountsController.submit()(request)

      "return ISE (INTERNAL SERVER ERROR)" in {
        setupMockStoreAccountingMethod(CashAccounting)(Left(errorModel))
        mockAuthResult(Future.successful(mockAuthorisedIndividual))
        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
      }
    }
  }

  "Calling .backLink" when {

    "dereg reason is Ceased" should {

      val result = TestVATAccountsController.backLink(None, Ceased)

      s"return ${controllers.routes.CeasedTradingDateController.show().url}" in {
        result shouldBe controllers.routes.CeasedTradingDateController.show().url
      }
    }

    "dereg reason is ZeroRated" should {

      val result = TestVATAccountsController.backLink(None, ZeroRated)

      s"return ${controllers.zeroRated.routes.PurchasesExceedSuppliesController.show().url}" in {
        result shouldBe controllers.zeroRated.routes.PurchasesExceedSuppliesController.show().url
      }
    }

    "dereg reason is ExemptOnly" should {

      val result = TestVATAccountsController.backLink(None, ExemptOnly)

      s"return ${controllers.routes.DeregistrationReasonController.show().url}" in {
        result shouldBe controllers.routes.DeregistrationReasonController.show().url
      }
    }

    "dereg reason is not Ceased" when {

      "last turnover was below threshold" should {

        val result = TestVATAccountsController.backLink(Some(Yes), BelowThreshold)

        s"return ${controllers.routes.NextTaxableTurnoverController.show().url}" in {
          result shouldBe controllers.routes.NextTaxableTurnoverController.show().url
        }
      }

      "last turnover was above threshold" should {

        val result = TestVATAccountsController.backLink(Some(No), BelowThreshold)

        s"return ${controllers.routes.WhyTurnoverBelowController.show().url}" in {
          result shouldBe controllers.routes.WhyTurnoverBelowController.show().url
        }
      }
    }
  }
}
