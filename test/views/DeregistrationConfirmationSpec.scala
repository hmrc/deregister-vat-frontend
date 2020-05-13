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
    val link = "a[href*=\"change-business-details\"]"
    val changeClientLink = "#content > article > p:nth-child(5) > a"
  }

  "Rendering the deregistration confirmation page for a non-agent user" when {

    "contactPreferences returns 'DIGITAL'" when {

      "verifiedEmail is true" should {

      lazy val view = views.html.deregistrationConfirmation(preference = Some("DIGITAL"), verifiedEmail = Some(true))(user, messages, mockConfig, hc, ec)
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

        lazy val view = views.html.deregistrationConfirmation(preference = Some("DIGITAL"), verifiedEmail = Some(false))(user, messages, mockConfig, hc, ec)
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

    "contactPreferences returns 'PAPER'" should {

      lazy val view = views.html.deregistrationConfirmation(preference = Some("PAPER"))(user, messages, mockConfig, hc, ec)
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

    "contactPreferences returns an error" should {

      lazy val view = views.html.deregistrationConfirmation()(user, messages, mockConfig, hc, ec)
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

    "the bulkPaperOff feature switch is disabled" when {

      "the user has verifiedAgentEmail (Yes pref inferred)" should {
        val businessName: Option[String] = Some("Fake Business Name Limited")
        lazy val view = {
          mockConfig.features.bulkPaperOffFeature(false)
          views.html.deregistrationConfirmation(businessName)(agentUserPrefYes, messages, mockConfig, hc, ec)
        }
        lazy implicit val document: Document = Jsoup.parse(view.body)


        "have the correct document title" in {
          document.title shouldBe DeregistrationConfirmationMessages.agentTitle
        }

        "have the correct page heading" in {
          elementText(Selectors.pageHeading) shouldBe DeregistrationConfirmationMessages.heading
        }

        "have the correct page subheading" in {
          elementText(Selectors.subheading) shouldBe DeregistrationConfirmationMessages.subheading
        }

        "have the correct first paragraph" in {
          elementText(Selectors.text) shouldBe DeregistrationConfirmationMessages.textAgentPrefYes
        }

        "have the correct text for the second paragraph (including business name)" in {
          elementText(Selectors.text2) shouldBe DeregistrationConfirmationMessages.agentWithBName
        }

        "have the correct finish button text" in {
          elementText(Selectors.button) shouldBe CommonMessages.finish
        }

        "have the correct finish button url" in {
          element(Selectors.button).attr("href") shouldBe mockConfig.agentClientLookupAgentHubPath
        }
      }

      "the user is without a verifiedAgentEmail (No pref inferred)" should {
        lazy val noBusinessName: Option[String] = None
        lazy val view = {
          mockConfig.features.bulkPaperOffFeature(false)
          views.html.deregistrationConfirmation(noBusinessName)(agentUserPrefNo, messages, mockConfig, hc, ec)
        }
        lazy implicit val document: Document = Jsoup.parse(view.body)


        "have the correct document title" in {
          document.title shouldBe DeregistrationConfirmationMessages.agentTitle
        }

        "have the correct page heading" in {
          elementText(Selectors.pageHeading) shouldBe DeregistrationConfirmationMessages.heading
        }

        "have the correct page subheading" in {
          elementText(Selectors.subheading) shouldBe DeregistrationConfirmationMessages.subheading
        }

        "have the correct first paragraph" in {
          elementText(Selectors.text) shouldBe DeregistrationConfirmationMessages.textAgentPrefNo
        }

        "have the correct text for the second paragraph (no business name when one isn't found)" in {
          elementText(Selectors.text2) shouldBe DeregistrationConfirmationMessages.agentNoBName
        }

        "have the correct finish button text" in {
          elementText(Selectors.button) shouldBe CommonMessages.finish
        }

        "have the correct finish button url" in {
          element(Selectors.button).attr("href") shouldBe mockConfig.agentClientLookupAgentHubPath
        }
      }
    }

    "the bulkPaperOff feature switch is enabled" when {

      "the user has verifiedAgentEmail (Yes pref inferred) and a business name" should {
        val businessName: Option[String] = Some("Fake Business Name Limited")
        lazy val view = {
          mockConfig.features.bulkPaperOffFeature(true)
          views.html.deregistrationConfirmation(businessName)(agentUserPrefYes, messages, mockConfig, hc, ec)
        }
        lazy implicit val document: Document = Jsoup.parse(view.body)

        "have the correct document title" in {
          document.title shouldBe DeregistrationConfirmationMessages.bpOffAgentTitle
        }

        "have the correct page heading" in {
          elementText(Selectors.pageHeading) shouldBe DeregistrationConfirmationMessages.bpOffHeading
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
          mockConfig.features.bulkPaperOffFeature(true)
          views.html.deregistrationConfirmation()(agentUserPrefYes, messages, mockConfig, hc, ec)
        }
        lazy implicit val document: Document = Jsoup.parse(view.body)

        "have the correct document title" in {
          document.title shouldBe DeregistrationConfirmationMessages.bpOffAgentTitle
        }

        "have the correct page heading" in {
          elementText(Selectors.pageHeading) shouldBe DeregistrationConfirmationMessages.bpOffHeading
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
          mockConfig.features.bulkPaperOffFeature(true)
          views.html.deregistrationConfirmation()(agentUserPrefNo, messages, mockConfig, hc, ec)
        }
        lazy implicit val document: Document = Jsoup.parse(view.body)

        "have the correct document title" in {
          document.title shouldBe DeregistrationConfirmationMessages.bpOffAgentTitle
        }

        "have the correct page heading" in {
          elementText(Selectors.pageHeading) shouldBe DeregistrationConfirmationMessages.bpOffHeading
        }

        "have the correct paragraph" in {
          elementText(Selectors.text) shouldBe DeregistrationConfirmationMessages.agentNoBName
        }
      }

      "the user is without a verifiedAgentEmail (No pref inferred) and has a business name" should {
        val businessName: Option[String] = Some("Fake Business Name Limited")
        lazy val view = {
          mockConfig.features.bulkPaperOffFeature(true)
          views.html.deregistrationConfirmation(businessName)(agentUserPrefNo, messages, mockConfig, hc, ec)
        }
        lazy implicit val document: Document = Jsoup.parse(view.body)

        "have the correct document title" in {
          document.title shouldBe DeregistrationConfirmationMessages.bpOffAgentTitle
        }

        "have the correct page heading" in {
          elementText(Selectors.pageHeading) shouldBe DeregistrationConfirmationMessages.bpOffHeading
        }

        "have the correct paragraph" in {
          elementText(Selectors.text) shouldBe DeregistrationConfirmationMessages.agentWithBName
        }
      }
    }

    "isAgent is true" should {
      "display the change client link" in {
        val businessName: Option[String] = Some("Fake Business Name Limited")
        lazy val view = views.html.deregistrationConfirmation(businessName)(agentUserPrefYes, messages, mockConfig, hc, ec)
        lazy implicit val document: Document = Jsoup.parse(view.body)

        elementText(Selectors.changeClientLink) shouldBe "Change client"
      }
    }

    "isAgent is false" should {
      "not display the change client link" in {
        val businessName: Option[String] = Some("Fake Business Name Limited")
        lazy val view = views.html.deregistrationConfirmation(businessName)(user, messages, mockConfig, hc, ec)
        lazy implicit val document: Document = Jsoup.parse(view.body)

        elementText(Selectors.changeClientLink) shouldNot be("Change client")
      }
    }
  }
}
