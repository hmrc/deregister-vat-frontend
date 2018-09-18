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

package controllers

import play.api.http.Status
import play.api.test.Helpers._

import scala.concurrent.Future

class DeregistrationConfirmationControllerSpec extends ControllerBaseSpec {

  object TestDeregistrationConfirmationController extends DeregistrationConfirmationController(messagesApi, mockAuthPredicate, mockConfig)

  "the user is authorised" when {

    "Calling the .show action" should {

      lazy val result = TestDeregistrationConfirmationController.show()(request)

      "return 200 (OK)" in {
        mockAuthResult(Future.successful(mockAuthorisedIndividual))
        status(result) shouldBe Status.OK
      }

      "return HTML" in {
        contentType(result) shouldBe Some("text/html")
        charset(result) shouldBe Some("utf-8")
      }

    }
  }

}