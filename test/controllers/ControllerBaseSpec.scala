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

import mocks.{MockAuth, MockDeniedAccessPredicate}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.http.Status
import play.api.mvc.{Action, AnyContent, Request, Result}
import play.api.test.FakeRequest
import play.filters.csrf.CSRF.Token
import play.filters.csrf.{CSRFConfigProvider, CSRFFilter}
import uk.gov.hmrc.auth.core.{InsufficientEnrolments, MissingBearerToken}
import utils.TestUtil

import scala.concurrent.Future

trait ControllerBaseSpec extends TestUtil with MockAuth with MockDeniedAccessPredicate{

  implicit class CSRFTokenAdder[T](req: FakeRequest[T]) {
    def addToken: FakeRequest[T] = {
      val csrfConfig = app.injector.instanceOf[CSRFConfigProvider].get
      val csrfFilter = app.injector.instanceOf[CSRFFilter]
      val token = csrfFilter.tokenProvider.generateToken

      req.copyFakeRequest(tags = req.tags ++ Map(
        Token.NameRequestTag -> csrfConfig.tokenName,
        Token.RequestTag -> token
      )).withHeaders(csrfConfig.headerName -> token)
    }
  }

  def authChecks(name: String, action: Action[AnyContent], request: Request[AnyContent]): Unit = {

    s"when the user is unauthenticated for '$name'" should {
      "return 303 (REDIRECT)" in {
        mockAuthResult(Future.failed(MissingBearerToken()))
        val result = action(request)
        status(result) shouldBe Status.SEE_OTHER
      }
    }

    s"when the user is unauthorised for '$name'" should {
      "return 403 (Forbidden)" in {
        mockAuthResult(Future.failed(InsufficientEnrolments()))
        val result = action(request)
        status(result) shouldBe Status.FORBIDDEN
      }
    }
  }

  def document(result: Future[Result]): Document = Jsoup.parse(bodyOf(result))
}
