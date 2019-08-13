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
    val link = "a[href*=\"change-business-details\"]"
    val changeClientLink = "#content > article > p:nth-child(5) > a"
  }

  "Rendering the deregistration confirmation page for a non-agent user" when {

    "the 'useContactPreference' feature is disabled" should {
      lazy val view = views.html.deregistrationConfirmation()(user, messages, mockConfig, hc, ec)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have the correct document title" in {
        mockConfig.features.useContactPreferences(false)
        document.title shouldBe DeregistrationConfirmationMessages.title
      }

      "have the correct page heading" in {
        elementText(Selectors.pageHeading) shouldBe DeregistrationConfirmationMessages.heading
      }

      "have the correct page subheading" in {
        elementText(Selectors.subheading) shouldBe DeregistrationConfirmationMessages.subheading
      }

      "have the correct first paragraph" in {
        elementText(Selectors.text) shouldBe DeregistrationConfirmationMessages.p1contactPrefDisabled
      }

      "have the correct second paragraph" in {
        elementText(Selectors.text2) shouldBe DeregistrationConfirmationMessages.checkContactDetails
      }

      "have a link to manage vat subscription" in {
        element(Selectors.link).attr("href") shouldBe mockConfig.manageVatSubscriptionFrontendUrl
      }

      "have the correct continue button text and url" in {
        elementText(Selectors.button) shouldBe CommonMessages.finish
        element(Selectors.button).attr("href") shouldBe mockConfig.manageVatSubscriptionFrontendUrl
      }
    }

    "the 'useContactPreference' feature is enabled" when {

      "contactPreferences returns 'DIGITAL'" should {

        lazy val view = views.html.deregistrationConfirmation(preference = Some("DIGITAL"))(user, messages, mockConfig, hc, ec)
        lazy implicit val document: Document = Jsoup.parse(view.body)

        "have the correct document title" in {
          mockConfig.features.useContactPreferences(true)
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

        "have the correct continue button text and url" in {
          elementText(Selectors.button) shouldBe CommonMessages.finish
          element(Selectors.button).attr("href") shouldBe mockConfig.manageVatSubscriptionFrontendUrl
        }
      }

      "contactPreferences returns 'PAPER'" should {

        lazy val view = views.html.deregistrationConfirmation(preference = Some("PAPER"))(user, messages, mockConfig, hc, ec)
        lazy implicit val document: Document = Jsoup.parse(view.body)

        "have the correct document title" in {
          mockConfig.features.useContactPreferences(true)
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

        "have the correct continue button text and url" in {
          elementText(Selectors.button) shouldBe CommonMessages.finish
          element(Selectors.button).attr("href") shouldBe mockConfig.manageVatSubscriptionFrontendUrl
        }
      }

      "contactPreferences returns an error" should {

        lazy val view = views.html.deregistrationConfirmation()(user, messages, mockConfig, hc, ec)
        lazy implicit val document: Document = Jsoup.parse(view.body)

        "have the correct document title" in {
          mockConfig.features.useContactPreferences(true)
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

        "have the correct continue button text and url" in {
          elementText(Selectors.button) shouldBe CommonMessages.finish
          element(Selectors.button).attr("href") shouldBe mockConfig.manageVatSubscriptionFrontendUrl
        }
      }
    }
  }

  "Rendering the deregistration confirmation page for an agent" when {

    "the user has verifiedAgentEmail (Yes pref inferred)" should {
      val businessName: Option[String] = Some("Fake Business Name Limited")
      lazy val view = views.html.deregistrationConfirmation(businessName)(agentUserPrefYes, messages, mockConfig, hc, ec)
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
        elementText(Selectors.text2) shouldBe DeregistrationConfirmationMessages.text2AgentWithOrgName
      }

      "have the correct continue button text" in {
        elementText(Selectors.button) shouldBe CommonMessages.finish
      }

      "have the correct continue button url" in {
        element(Selectors.button).attr("href") shouldBe mockConfig.manageVatSubscriptionFrontendUrl
      }
    }

    "the user is without a verifiedAgentEmail (No pref inferred)" should {
      lazy val noBusinessName: Option[String] = None
      lazy val view = views.html.deregistrationConfirmation(noBusinessName)(agentUserPrefNo, messages, mockConfig, hc, ec)
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
        elementText(Selectors.text2) shouldBe DeregistrationConfirmationMessages.text2AgentPrefNo
      }

      "have the correct continue button text" in {
        elementText(Selectors.button) shouldBe CommonMessages.finish
      }

      "have the correct continue button url" in {
        element(Selectors.button).attr("href") shouldBe mockConfig.manageVatSubscriptionFrontendUrl
      }
    }

    "the changeClient feature switch is enabled" should {
      "display the change client link" in {
        mockConfig.features.changeClient(true)
        val businessName: Option[String] = Some("Fake Business Name Limited")
        lazy val view = views.html.deregistrationConfirmation(businessName)(agentUserPrefYes, messages, mockConfig, hc, ec)
        lazy implicit val document: Document = Jsoup.parse(view.body)

        elementText(Selectors.changeClientLink) shouldBe "Change client"
      }
    }

    "the changeClient feature switch is disabled" should {
      "not display the change client link" in {
        mockConfig.features.changeClient(false)
        val businessName: Option[String] = Some("Fake Business Name Limited")
        lazy val view = views.html.deregistrationConfirmation(businessName)(agentUserPrefYes, messages, mockConfig, hc, ec)
        lazy implicit val document: Document = Jsoup.parse(view.body)

        elementText(Selectors.changeClientLink) shouldNot be("Change client")
      }
    }
  }
}
