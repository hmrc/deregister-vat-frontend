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

import messages.DeregistrationOTTConfirmationMessages
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import views.html.DeregistrationOTTConfirmation

class DeregistrationOTTConfirmationSpec extends ViewBaseSpec {

  lazy val deregistrationOTTConfirmation: DeregistrationOTTConfirmation = injector.instanceOf[DeregistrationOTTConfirmation]

  object Selectors {
    val pageHeading = "#content h1"
    val emailPreference = "#content .govuk-body:nth-child(2)"
    val subheading = "#content h2"

    val toDoNext_p1 = "#content .govuk-body:nth-child(4)"
    val toDoNext_p2 = "#content .govuk-body:nth-child(5)"
    val button = ".govuk-button"
    val link = "#content .govuk-link[href*=\"opting-to-tax-land-and-buildings-notice\"]"
    val warningText = "#content .govuk-warning-text__text"
  }

  "Rendering the deregistration OTT confirmation page" when {

    "User is Agent" when {

      "SessionKeys verifiedEmail is true" should {

        lazy val view = deregistrationOTTConfirmation()(agentUserPrefYes, messages, mockConfig)
        lazy implicit val document: Document = Jsoup.parse(view.body)

        "have the correct document title" in {
          document.title shouldBe DeregistrationOTTConfirmationMessages.agentTitle
        }

        "have the correct page heading" in {
          elementText(Selectors.pageHeading) shouldBe DeregistrationOTTConfirmationMessages.heading
        }

        "have the correct page subheading" in {
          elementText(Selectors.subheading) shouldBe DeregistrationOTTConfirmationMessages.subheading
        }

        "have the correct contact paragraph" in {
          elementText(Selectors.emailPreference) shouldBe DeregistrationOTTConfirmationMessages.updateConfirmationAgent
        }

        "have the correct first paragraph" in {
          elementText(Selectors.toDoNext_p1) shouldBe DeregistrationOTTConfirmationMessages.toDoNext_p1
        }

        "have the correct second paragraph" in {
          elementText(Selectors.toDoNext_p2) shouldBe DeregistrationOTTConfirmationMessages.toDoNext_p2
        }

        "have the correct warning message" in {
          elementText(Selectors.warningText) shouldBe DeregistrationOTTConfirmationMessages.warningMsg
        }

        "have the correct govuk link" in {
          element(Selectors.link).attr("href") shouldBe DeregistrationOTTConfirmationMessages.govUkLink
        }

        "have the correct finish button text and url" in {
          elementText(Selectors.button) shouldBe DeregistrationOTTConfirmationMessages.finishButtonText
          // element(Selectors.button).attr("href") shouldBe mockConfig.completeLandAndBuildingsInfoFormUrl //to be fixed thru this ticket DL-18688
        }
      }
    }

    "User is a Client" when {

      "SessionKeys verifiedEmail is false" should {

        lazy val view = deregistrationOTTConfirmation()(user, messages, mockConfig)
        lazy implicit val document: Document = Jsoup.parse(view.body)

        "have the correct document title" in {
          document.title shouldBe DeregistrationOTTConfirmationMessages.title
        }

        "have the correct page heading" in {
          elementText(Selectors.pageHeading) shouldBe DeregistrationOTTConfirmationMessages.heading
        }

        "have the correct page subheading" in {
          elementText(Selectors.subheading) shouldBe DeregistrationOTTConfirmationMessages.subheading
        }

        "have the correct contact paragraph" in {
          elementText(Selectors.emailPreference) shouldBe DeregistrationOTTConfirmationMessages.updateConfirmation
        }

        "have the correct first paragraph" in {
          elementText(Selectors.toDoNext_p1) shouldBe DeregistrationOTTConfirmationMessages.toDoNext_p1
        }

        "have the correct second paragraph" in {
          elementText(Selectors.toDoNext_p2) shouldBe DeregistrationOTTConfirmationMessages.toDoNext_p2
        }

        "have the correct govuk link" in {
          element(Selectors.link).attr("href") shouldBe DeregistrationOTTConfirmationMessages.govUkLink
        }

        "have the correct warning message" in {
          elementText(Selectors.warningText) shouldBe DeregistrationOTTConfirmationMessages.warningMsg
        }

        "have the correct finish button text and url" in {
          elementText(Selectors.button) shouldBe DeregistrationOTTConfirmationMessages.finishButtonText
          // element(Selectors.button).attr("href") shouldBe mockConfig.completeLandAndBuildingsInfoFormUrl //to be fixed thru this ticket DL-18688
        }

      }

    }

  }
}
