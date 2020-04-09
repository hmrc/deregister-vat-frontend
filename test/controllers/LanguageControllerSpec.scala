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

import play.api.Play
import play.api.http.Status.SEE_OTHER
import play.api.mvc.{AnyContentAsEmpty, Call, Cookie}
import play.api.test.FakeRequest
import play.api.test.Helpers._

class LanguageControllerSpec extends ControllerBaseSpec {
  lazy val controller = new LanguageController(mockConfig, messagesApi)

  lazy val emptyFakeRequest = FakeRequest()
  lazy val fRequest: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest("get", "aurl").withHeaders(REFERER -> "thisIsMyNextLocation")

  "switchLanguage" should {
    "correctly change the language session property" when {
      "English is passed in" in {
        lazy val result = controller.switchLanguage("english")(fRequest)

        status(result) shouldBe SEE_OTHER
        cookies(result).get(Play.langCookieName(messagesApi)) shouldBe Some(Cookie("PLAY_LANG", "en", None, "/", None, secure = false, httpOnly = true))
        redirectLocation(result) shouldBe Some("thisIsMyNextLocation")
      }
      "Welsh is passed in" in {
        lazy val result = controller.switchLanguage("cymraeg")(fRequest)

        status(result) shouldBe SEE_OTHER
        cookies(result).get(Play.langCookieName(messagesApi)) shouldBe Some(Cookie("PLAY_LANG", "cy", None, "/", None, secure = false, httpOnly = true))
        redirectLocation(result) shouldBe Some("thisIsMyNextLocation")
      }
    }
    "remain on the same language" when {
      "an invalid language is requested" in {
        lazy val result = controller.switchLanguage("dovahtongue")(fRequest)

        status(result) shouldBe SEE_OTHER
        cookies(result).get(Play.langCookieName(messagesApi)) shouldBe Some(Cookie("PLAY_LANG", "en", None, "/", None, secure = false, httpOnly = true))
        redirectLocation(result) shouldBe Some("thisIsMyNextLocation")
      }
    }
    "redirect to the fallback url" when {
      "one is not provided" in {
        lazy val result = controller.switchLanguage("english")(emptyFakeRequest)

        val expectedResponse = mockConfig.languageFallbackUrl

        status(result) shouldBe SEE_OTHER
        cookies(result).get(Play.langCookieName(messagesApi)) shouldBe Some(Cookie("PLAY_LANG", "en", None, "/", None, secure = false, httpOnly = true))
        redirectLocation(result) shouldBe Some(expectedResponse)
      }
    }
  }

  "langToCall" should {
    "return the correct call" in {
      lazy val result: Call = controller.langToCall("english")

      result.url shouldBe "/vat-through-software/account/cancel-vat/language/english"
    }
  }
}
