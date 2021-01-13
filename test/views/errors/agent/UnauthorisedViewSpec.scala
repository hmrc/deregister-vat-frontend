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

package views.errors.agent

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

import assets.messages.{CommonMessages, AgentUnauthorisedPageMessages => Messages}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import views.ViewBaseSpec
import views.html.errors.agent.Unauthorised

class UnauthorisedViewSpec extends ViewBaseSpec {

  lazy val unauthorised: Unauthorised = injector.instanceOf[Unauthorised]

  "Rendering the unauthorised page" should {

    object Selectors {
      val serviceName = ".header__menu__proposition-name"
      val pageHeading = "#content h1"
      val instructions = "#content article > p"
      val instructionsLink = "#content article > p > a"
      val button = "#content .button"
    }

    lazy val view = unauthorised()(request, messages, mockConfig)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct document title" in {
      document.title shouldBe Messages.title
    }

    "have the correct service name" in {
      elementText(Selectors.serviceName) shouldBe CommonMessages.agentServiceName
    }

    "have a the correct page heading" in {
      elementText(Selectors.pageHeading) shouldBe Messages.pageHeading
    }

    "have the correct instructions on the page" in {
      elementText(Selectors.instructions) shouldBe Messages.instructions
    }

    "have a link to GOV.UK guidance" in {
      element(Selectors.instructionsLink).attr("href") shouldBe mockConfig.agentServicesGovUkGuidance
    }

    "have a Sign out button" in {
      elementText(Selectors.button) shouldBe CommonMessages.signOut
    }

    "have a link to sign out" in {
      element(Selectors.button).attr("href") shouldBe controllers.routes.SignOutController.signOut(authorised = false).url
    }
  }
}

