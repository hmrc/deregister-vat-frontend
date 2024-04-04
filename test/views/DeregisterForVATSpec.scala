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

package views

import assets.messages.{CommonMessages, DeregisterForVATMessages}
import config.ConfigKeys
import config.features.{Feature, Features}
import mocks.MockAppConfig
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.Configuration
import views.html.DeregisterForVAT
import play.twirl.api.Html

class DeregisterForVATSpec extends ViewBaseSpec {

  lazy val deregisterForVAT: DeregisterForVAT = injector.instanceOf[DeregisterForVAT]

  object Selectors {
    val back = ".govuk-back-link"
    val pageHeading = "#content h1"
    val button = ".govuk-button"
    val para:  Int => String = n => s"#content .govuk-body:nth-child($n)"
    val bullets: Int => String = n => s"#content ul > li:nth-child($n)"
    val webchatLink = "#webchatLink-id"
  }

  "Rendering the cancel registration reason page for client" should {

    lazy val view = deregisterForVAT()(user,messages,mockConfig)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct document title" in {
      document.title shouldBe DeregisterForVATMessages.title
    }

    "have the correct back text" in {
      elementText(Selectors.back) shouldBe CommonMessages.back
      element(Selectors.back).attr("href") shouldBe mockConfig.vatSummaryFrontendUrl
    }

    "have the correct page heading" in {
      elementText(Selectors.pageHeading) shouldBe DeregisterForVATMessages.heading
    }

    "have the correct continue button text and url" in {
      elementText(Selectors.button) shouldBe CommonMessages.continue
      element(Selectors.button).attr("href") shouldBe controllers.routes.DeregistrationReasonController.show.url
    }

    "have the correct paragraph" in {
      elementText(Selectors.para(2)) shouldBe DeregisterForVATMessages.p1
    }

    "have the correct bullet points" in {
      elementText(Selectors.bullets(1)) shouldBe DeregisterForVATMessages.bullet1
      elementText(Selectors.bullets(2)) shouldBe DeregisterForVATMessages.bullet2
    }

    "have the correct second paragraph" in {
      elementText(Selectors.para(4)) shouldBe DeregisterForVATMessages.p2
    }

  }

  "Rendering the cancel registration reason page for an agent" should {

    lazy val view = deregisterForVAT()(agentUserPrefYes,messages,mockConfig)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have a back link with the correct link location" in {
      element(Selectors.back).attr("href") shouldBe mockConfig.agentClientLookupAgentHubPath
    }
  }

  "The webchat link is displayed" when {
    "the webchatEnabled feature switch is switched on for principal user" in {
      lazy implicit val mockConf = mockAppConfigForWebChat("true")
      lazy val view = deregisterForVAT()(user,messages,mockConf)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      document.select("#webchatLink-id").text() shouldBe "Ask HMRC (opens in a new tab)"
      document.select("#webchatLink-id").attr("href") shouldBe "/ask-hmrc/chat/vat-online?ds"
    }

    "the webchatEnabled feature switch is switched on for an agent" in {
      lazy implicit val mockConf = mockAppConfigForWebChat("true")
      lazy val view = deregisterForVAT()(user,messages,mockConf)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      document.select("#webchatLink-id").text() shouldBe "Ask HMRC (opens in a new tab)"
      document.select("#webchatLink-id").attr("href") shouldBe "/ask-hmrc/chat/vat-online?ds"
    }
  }

  "The webchat link is not displayed" when {
    "the webchatEnabled feature switch is switched off for principal user" in {
      lazy implicit val mockConf = mockAppConfigForWebChat("false")
      lazy val view = deregisterForVAT()(user,messages,mockConf)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      document.select("#webchatLink-id").size shouldBe 0
    }

    "the webchatEnabled feature switch is switched off for an agent" in {
      lazy implicit val mockConf = mockAppConfigForWebChat("false")
      lazy val view = deregisterForVAT()(user,messages,mockConf)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      document.select("#webchatLink-id").size shouldBe 0
    }
  }

  private def mockAppConfigForWebChat(webchatLinkEnabled: String): MockAppConfig = {
    new MockAppConfig() {
      lazy implicit val config: Configuration = app.configuration
      override val features: Features = new Features() {
        override val webchatEnabled = new Feature("") {
          override def apply(): Boolean = webchatLinkEnabled.toBoolean
        }
      }
    }
  }
}
