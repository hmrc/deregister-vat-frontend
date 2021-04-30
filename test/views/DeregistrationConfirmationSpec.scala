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

package views

import assets.messages.{CommonMessages, DeregistrationConfirmationMessages}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.Html
import views.html.DeregistrationConfirmation

class DeregistrationConfirmationSpec extends ViewBaseSpec {

  lazy val deregistrationConfirmation: DeregistrationConfirmation = injector.instanceOf[DeregistrationConfirmation]

  object Selectors {
    val pageHeading = "#content h1"
    val subheading = "#content h2"
    val text = "#content .govuk-body:nth-child(3)"
    val text2 = "#content .govuk-body:nth-child(4)"
    val button = ".govuk-button"
    val link = "#content .govuk-link[href*=\"change-business-details\"]"
    val changeClientLink = "#content .govuk-body:nth-child(5) > a"
  }

  "Rendering the deregistration confirmation page for a non-agent user" when {

    "contactPreference is 'DIGITAL'" when {

      "verifiedEmail is true" should {

      lazy val view = deregistrationConfirmation(preference = Some("DIGITAL"), verifiedEmail = Some(true))(user, messages, mockConfig)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have the correct document title" in {
        document.title shouldBe DeregistrationConfirmationMessages.title
      }

      "have the correct page heading" in {
        elementText(Selectors.pageHeading) shouldBe DeregistrationConfirmationMessages.heading
      }

      "have the correct page subheading" in {
        elementText(Selectors.subheading) shouldBe DeregistrationConfirmationMessages.subheading
      }

      "have the correct first paragraph" in {
        elementText(Selectors.text) shouldBe DeregistrationConfirmationMessages.emailPreference
      }

      "have the correct second paragraph" in {
        elementText(Selectors.text2) shouldBe DeregistrationConfirmationMessages.contactDetails
      }

      "have the correct finish button text and url" in {
        elementText(Selectors.button) shouldBe CommonMessages.finish
        element(Selectors.button).attr("href") shouldBe mockConfig.vatSummaryFrontendUrl
      }

    }

      "verifiedEmail is false" should {

        lazy val view = deregistrationConfirmation(preference = Some("DIGITAL"), verifiedEmail = Some(false))(user, messages, mockConfig)
        lazy implicit val document: Document = Jsoup.parse(view.body)

        "have the correct document title" in {
          document.title shouldBe DeregistrationConfirmationMessages.title
        }

        "have the correct page heading" in {
          elementText(Selectors.pageHeading) shouldBe DeregistrationConfirmationMessages.heading
        }

        "have the correct page subheading" in {
          elementText(Selectors.subheading) shouldBe DeregistrationConfirmationMessages.subheading
        }

        "have the correct first paragraph" in {
          elementText(Selectors.text) shouldBe DeregistrationConfirmationMessages.digitalPreference
        }

        "have the correct second paragraph" in {
          elementText(Selectors.text2) shouldBe DeregistrationConfirmationMessages.contactDetails
        }

        "have the correct finish button text and url" in {
          elementText(Selectors.button) shouldBe CommonMessages.finish
          element(Selectors.button).attr("href") shouldBe mockConfig.vatSummaryFrontendUrl
        }

      }

    }

    "contactPreference is 'PAPER'" should {

      lazy val view = deregistrationConfirmation(preference = Some("PAPER"))(user, messages, mockConfig)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have the correct document title" in {
        document.title shouldBe DeregistrationConfirmationMessages.title
      }

      "have the correct page heading" in {
        elementText(Selectors.pageHeading) shouldBe DeregistrationConfirmationMessages.heading
      }

      "have the correct page subheading" in {
        elementText(Selectors.subheading) shouldBe DeregistrationConfirmationMessages.subheading
      }

      "have the correct first paragraph" in {
        elementText(Selectors.text) shouldBe DeregistrationConfirmationMessages.paperPreference
      }

      "have the correct second paragraph" in {
        elementText(Selectors.text2) shouldBe DeregistrationConfirmationMessages.contactDetails
      }

      "have the correct finish button text and url" in {
        elementText(Selectors.button) shouldBe CommonMessages.finish
        element(Selectors.button).attr("href") shouldBe mockConfig.vatSummaryFrontendUrl
      }
    }

    "a contact preference is not present" should {

      lazy val view = deregistrationConfirmation()(user, messages, mockConfig)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have the correct document title" in {
        document.title shouldBe DeregistrationConfirmationMessages.title
      }

      "have the correct page heading" in {
        elementText(Selectors.pageHeading) shouldBe DeregistrationConfirmationMessages.heading
      }

      "have the correct page subheading" in {
        elementText(Selectors.subheading) shouldBe DeregistrationConfirmationMessages.subheading
      }

      "have the correct first paragraph" in {
        elementText(Selectors.text) shouldBe DeregistrationConfirmationMessages.contactPrefError
      }

      "have the correct second paragraph" in {
        elementText(Selectors.text2) shouldBe DeregistrationConfirmationMessages.contactDetails
      }

      "have the correct finish button text and url" in {
        elementText(Selectors.button) shouldBe CommonMessages.finish
        element(Selectors.button).attr("href") shouldBe mockConfig.vatSummaryFrontendUrl
      }
    }
  }

  "Rendering the deregistration confirmation page for an agent" when {

    "the user has verifiedAgentEmail (Yes pref inferred) and a business name" should {
      val businessName: Option[String] = Some("Fake Business Name Limited")
      lazy val view = {
        deregistrationConfirmation(businessName)(agentUserPrefYes, messages, mockConfig)
      }
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have the correct document title" in {
        document.title shouldBe DeregistrationConfirmationMessages.agentTitle
      }

      "have the correct page heading" in {
        elementText(Selectors.pageHeading) shouldBe DeregistrationConfirmationMessages.heading
      }


      "have the correct first paragraph" in {
        elementText(Selectors.text) shouldBe DeregistrationConfirmationMessages.bpOffAgentYesPref
      }

      "have the correct text for the second paragraph (including business name)" in {
        elementText(Selectors.text2) shouldBe DeregistrationConfirmationMessages.agentWithBName
      }
    }

    "the user has verifiedAgentEmail (Yes pref inferred) and no business name" should {
      lazy val view = {
        deregistrationConfirmation()(agentUserPrefYes, messages, mockConfig)
      }
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have the correct document title" in {
        document.title shouldBe DeregistrationConfirmationMessages.agentTitle
      }

      "have the correct page heading" in {
        elementText(Selectors.pageHeading) shouldBe DeregistrationConfirmationMessages.heading
      }


      "have the correct first paragraph" in {
        elementText(Selectors.text) shouldBe DeregistrationConfirmationMessages.bpOffAgentYesPref
      }

      "have the correct text for the second paragraph (including business name)" in {
        elementText(Selectors.text2) shouldBe DeregistrationConfirmationMessages.agentNoBName
      }
    }

    "the user is without a verifiedAgentEmail (No pref inferred) and no business name" should {
      lazy val view = {
        deregistrationConfirmation()(agentUserPrefNo, messages, mockConfig)
      }
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have the correct document title" in {
        document.title shouldBe DeregistrationConfirmationMessages.agentTitle
      }

      "have the correct page heading" in {
        elementText(Selectors.pageHeading) shouldBe DeregistrationConfirmationMessages.heading
      }

      "have the correct paragraph" in {
        elementText(Selectors.text) shouldBe DeregistrationConfirmationMessages.agentNoBName
      }
    }

    "the user is without a verifiedAgentEmail (No pref inferred) and has a business name" should {
      val businessName: Option[String] = Some("Fake Business Name Limited")
      lazy val view: Html = {
        deregistrationConfirmation(businessName)(agentUserPrefNo, messages, mockConfig)
      }
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have the correct document title" in {
        document.title shouldBe DeregistrationConfirmationMessages.agentTitle
      }

      "have the correct page heading" in {
        elementText(Selectors.pageHeading) shouldBe DeregistrationConfirmationMessages.heading
      }

      "have the correct paragraph" in {
        elementText(Selectors.text) shouldBe DeregistrationConfirmationMessages.agentWithBName
      }
    }
  }
}
