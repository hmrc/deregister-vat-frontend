/*
 * Copyright 2024 HM Revenue & Customs
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

import audit.mocks.MockAuditConnector
import common.{Constants, SessionKeys}
import controllers.predicates.DeniedAccessPredicate
import play.api.http.Status
import play.api.test.Helpers._
import services.CustomerDetailsService
import services.mocks.MockAuditService
import views.html.DeregisterForVAT

class DeregisterForVATControllerSpec extends ControllerBaseSpec with MockAuditConnector with MockAuditService {


  lazy val deregisterForVAT: DeregisterForVAT = injector.instanceOf[DeregisterForVAT]

  val mockPendingDereg = new DeniedAccessPredicate(
    new CustomerDetailsService(mockVatSubscriptionConnector),
    serviceErrorHandler,
    mcc,
    messagesApi,
    mockConfig
  )

  object TestDeregisterForVATController extends DeregisterForVATController(
    deregisterForVAT,
    mcc,
    mockAuthPredicate,
    mockPendingDereg,
    mockAuditService,
    ec,
    mockConfig
  )

  "the user is authorised" when {

    "Calling the .show action with agent" should {

      lazy val result = TestDeregisterForVATController.show(requestWithVRN.withSession(
        SessionKeys.registrationStatus -> Constants.registered
      ))

      "return 200 (OK)" in {
        mockAuthResult(mockAuthorisedAgent, true)
        mockAudit()
        status(result) shouldBe Status.OK
      }

      "return HTML" in {
        contentType(result) shouldBe Some("text/html")
        charset(result) shouldBe Some("utf-8")
      }

    }

    "Calling the .show action with non-agent" should {

      lazy val result = TestDeregisterForVATController.show(request.withSession(
        SessionKeys.registrationStatus -> Constants.registered
      ))

      "return 200 (OK)" in {
        mockAuthResult(mockAuthorisedIndividual)
        mockAudit()
        status(result) shouldBe Status.OK
      }

      "return HTML" in {
        contentType(result) shouldBe Some("text/html")
        charset(result) shouldBe Some("utf-8")
      }

    }
  }

}
