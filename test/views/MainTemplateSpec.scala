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

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.Html
import views.html.MainTemplate

class MainTemplateSpec extends ViewBaseSpec {

  val injectedView: MainTemplate = injector.instanceOf[MainTemplate]

  "MainTemplate" when {

    "the user is an Agent" should {

      lazy val view = injectedView("Title")(Html(""))(agentUserPrefYes, messages, mockConfig)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have the correct service name" in {
        elementText(".govuk-header__service-name") shouldBe "Your client’s VAT details"
      }

      "have the correct service URL" in {
        element(".govuk-header__service-name").attr("href") shouldBe mockConfig.agentClientLookupAgentHubPath
      }
    }

    "the user is not an Agent" should {

      lazy val view = injectedView("Title")(Html(""))(user, messages, mockConfig)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have the correct service name" in {
        elementText(".govuk-header__service-name") shouldBe "Manage your VAT account"
      }

      "have the correct service URL" in {
        element(".govuk-header__service-name").attr("href") shouldBe mockConfig.vatSummaryFrontendUrl
      }
    }

    "the user type cannot be determined" should {

      lazy val view = injectedView("Title")(Html(""))(request, messages, mockConfig)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have the correct service name" in {
        elementText(".govuk-header__service-name") shouldBe "VAT"
      }

      "have the correct service URL" in {
        element(".govuk-header__service-name").attr("href") shouldBe ""
      }
    }
  }
}
