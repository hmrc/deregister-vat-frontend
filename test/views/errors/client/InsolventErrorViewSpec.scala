/*
 * Copyright 2023 HM Revenue & Customs
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

package views.errors.client

import assets.messages.{CommonMessages, InsolventErrorPageMessages=> Messages}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import views.ViewBaseSpec
import views.html.errors.client.InsolventError

class InsolventErrorViewSpec extends ViewBaseSpec {

  lazy val insolventError: InsolventError = injector.instanceOf[InsolventError]

  "Rendering the InsolventError page" should {

    object Selectors {
      val serviceName = ".hmrc-header__service-name"
      val pageHeading = "#content h1"
      val insolventMessage = "#insolvent-message"
      val signoutLink = ".govuk-link"
      val btaButton = ".govuk-button"
    }

    lazy val view = insolventError()(user, messages, mockConfig)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct document title" in {
      document.title shouldBe Messages.title
    }

    "have the correct service name" in {
      elementText(Selectors.serviceName) shouldBe CommonMessages.clientServiceName
    }

    "have a the correct page heading" in {
      elementText(Selectors.pageHeading) shouldBe Messages.pageHeading
    }

    "have the correct message on the page" in {
      elementText(Selectors.insolventMessage) shouldBe Messages.insolventMessage
    }

    "have the correct text for sign out link" in {
      elementText(Selectors.signoutLink) shouldBe Messages.signOut
    }

    "have a link to sign out" in {
      element(Selectors.signoutLink).attr("href") shouldBe controllers.routes.SignOutController.signOut(authorised = false).url
    }

    "have the correct text for BTA button" in {
      elementText(Selectors.btaButton) shouldBe Messages.btaButton
    }

    "have a link to BTA" in {
      element(Selectors.btaButton).attr("href") shouldBe mockConfig.btaHomeUrl
    }
  }
}

