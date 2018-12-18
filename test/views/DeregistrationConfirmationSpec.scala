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

package views

import assets.messages.{CommonMessages, DeregistrationConfirmationMessages}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class DeregistrationConfirmationSpec extends ViewBaseSpec {

  object Selectors {
    val pageHeading = "#content > article > div > h1"
    val subheading = "#content > article > h2"
    val text = "#content > article > p:nth-child(3)"
    val text2 = "#content > article > p:nth-child(4)"
    val button = ".button"
  }

  "Rendering the Why is the deregistration confirmation page for non-agent user" when {

    "the user is not an agent" should {
      lazy val view = views.html.deregistrationConfirmation()(user, messages, mockConfig)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      s"have the correct document title" in {
        document.title shouldBe DeregistrationConfirmationMessages.title
      }

      s"have the correct page heading" in {
        elementText(Selectors.pageHeading) shouldBe DeregistrationConfirmationMessages.title
      }

      s"have the correct page subheading" in {
        elementText(Selectors.subheading) shouldBe DeregistrationConfirmationMessages.subheading
      }

      "have the correct first paragraph" in {
        elementText(Selectors.text) shouldBe DeregistrationConfirmationMessages.textNonAgent
      }

      "have the correct text for the second paragraph" in {
        elementText(Selectors.text2) shouldBe DeregistrationConfirmationMessages.text2NonAgent
      }

      s"have the correct continue button text and url" in {
        element(Selectors.button).attr("href") shouldBe mockConfig.manageVatSubscriptionFrontendUrl
        elementText(Selectors.button) shouldBe CommonMessages.finish
      }
    }

    "the user is an agent with verifiedAgentEmail (Yes pref inferred)" should {
      lazy val view = views.html.deregistrationConfirmation()(agentUserPrefYes, messages, mockConfig)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      s"have the correct document title" in {
        document.title shouldBe DeregistrationConfirmationMessages.title
      }

      s"have the correct page heading" in {
        elementText(Selectors.pageHeading) shouldBe DeregistrationConfirmationMessages.title
      }

      s"have the correct page subheading" in {
        elementText(Selectors.subheading) shouldBe DeregistrationConfirmationMessages.subheading
      }

      "have the correct first paragraph" in {
        elementText(Selectors.text) shouldBe DeregistrationConfirmationMessages.textAgentPrefYes
      }

      "have the correct text for the second paragraph" in {
        elementText(Selectors.text2) shouldBe DeregistrationConfirmationMessages.text2AgentPrefYes
      }

      s"have the correct continue button text and url" in {
        element(Selectors.button).attr("href") shouldBe mockConfig.manageVatSubscriptionFrontendUrl
        elementText(Selectors.button) shouldBe CommonMessages.finish
      }
    }

    "the user is an agent without a verifiedAgentEmail (No pref inferred)" should {
      lazy val view = views.html.deregistrationConfirmation()(agentUserPrefNo, messages, mockConfig)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      s"have the correct document title" in {
        document.title shouldBe DeregistrationConfirmationMessages.title
      }

      s"have the correct page heading" in {
        elementText(Selectors.pageHeading) shouldBe DeregistrationConfirmationMessages.title
      }

      s"have the correct page subheading" in {
        elementText(Selectors.subheading) shouldBe DeregistrationConfirmationMessages.subheading
      }

      "have the correct first paragraph" in {
        elementText(Selectors.text) shouldBe DeregistrationConfirmationMessages.textAgentPrefNo
      }

      "have the correct text for the second paragraph" in {
        elementText(Selectors.text2) shouldBe DeregistrationConfirmationMessages.text2AgentPrefNo
      }

      s"have the correct continue button text and url" in {
        element(Selectors.button).attr("href") shouldBe mockConfig.manageVatSubscriptionFrontendUrl
        elementText(Selectors.button) shouldBe CommonMessages.finish
      }
    }
  }
}
