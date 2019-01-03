/*
 * Copyright 2019 HM Revenue & Customs
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
    val link = "#content > article > p:nth-child(4) > a"
  }

  "Rendering the deregistration confirmation page for non-agent user" when {

    "the user is not an agent" should {
      lazy val view = views.html.deregistrationConfirmation(None)(user, messages, mockConfig)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have the correct document title" in {
        document.title shouldBe DeregistrationConfirmationMessages.title
      }

      "have the correct page heading" in {
        elementText(Selectors.pageHeading) shouldBe DeregistrationConfirmationMessages.title
      }

      "have the correct page subheading" in {
        elementText(Selectors.subheading) shouldBe DeregistrationConfirmationMessages.subheading
      }

      "have the correct first paragraph" in {
        elementText(Selectors.text) shouldBe DeregistrationConfirmationMessages.textNonAgentP1
      }

      "have the correct second paragraph" in {
        elementText(Selectors.text2) shouldBe DeregistrationConfirmationMessages.textNonAgentP2
      }

      "have a link to manage vat subscription" in {
        element(Selectors.link).attr("href") shouldBe mockConfig.manageVatSubscriptionFrontendUrl
      }

      "have the correct continue button text" in {
        elementText(Selectors.button) shouldBe CommonMessages.finish
      }

      "have the correct continue button url" in {
        element(Selectors.button).attr("href") shouldBe mockConfig.manageVatSubscriptionFrontendUrl
      }
    }

    "the user is an agent with verifiedAgentEmail (Yes pref inferred)" should {
      val businessName: Option[String] = Some("Fake Business Name Limited")
      lazy val view = views.html.deregistrationConfirmation(businessName)(agentUserPrefYes, messages, mockConfig)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have the correct document title" in {
        document.title shouldBe DeregistrationConfirmationMessages.title
      }

      "have the correct page heading" in {
        elementText(Selectors.pageHeading) shouldBe DeregistrationConfirmationMessages.title
      }

      "have the correct page subheading" in {
        elementText(Selectors.subheading) shouldBe DeregistrationConfirmationMessages.subheading
      }

      "have the correct first paragraph" in {
        elementText(Selectors.text) shouldBe DeregistrationConfirmationMessages.textAgentPrefYes
      }

      "have the correct text for the second paragraph (including business name)" in {
        elementText(Selectors.text2) shouldBe DeregistrationConfirmationMessages.text2AgentWithOrgName
      }

      "have the correct continue button text" in {
        elementText(Selectors.button) shouldBe CommonMessages.finish
      }

      "have the correct continue button url" in {
        element(Selectors.button).attr("href") shouldBe mockConfig.manageVatSubscriptionFrontendUrl
      }
    }

    "the user is an agent without a verifiedAgentEmail (No pref inferred)" should {
      lazy val noBusinessName: Option[String] = None
      lazy val view = views.html.deregistrationConfirmation(noBusinessName)(agentUserPrefNo, messages, mockConfig)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have the correct document title" in {
        document.title shouldBe DeregistrationConfirmationMessages.title
      }

      "have the correct page heading" in {
        elementText(Selectors.pageHeading) shouldBe DeregistrationConfirmationMessages.title
      }

      "have the correct page subheading" in {
        elementText(Selectors.subheading) shouldBe DeregistrationConfirmationMessages.subheading
      }

      "have the correct first paragraph" in {
        elementText(Selectors.text) shouldBe DeregistrationConfirmationMessages.textAgentPrefNo
      }

      "have the correct text for the second paragraph (no business name when one isn't found)" in {
        elementText(Selectors.text2) shouldBe DeregistrationConfirmationMessages.text2AgentPrefNo
      }

      "have the correct continue button text" in {
        elementText(Selectors.button) shouldBe CommonMessages.finish
      }

      "have the correct continue button url" in {
        element(Selectors.button).attr("href") shouldBe mockConfig.manageVatSubscriptionFrontendUrl
      }
    }
  }
}
