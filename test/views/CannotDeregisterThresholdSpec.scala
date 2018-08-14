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

import assets.messages.{CannotDeregisterThresholdMessages, CommonMessages}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document


class CannotDeregisterThresholdSpec extends ViewBaseSpec {

  "Rendering the Ceased trading date page" should {

    object Selectors {
      val back = ".link-back"
      val pageHeading = "#content h1"
      val text = "#content > article > p:nth-child(3)"
      val linkText = "#content > article > p:nth-child(4)"
      val link = "#content > article > p:nth-child(4) > a"
    }

    lazy val view = views.html.cannotDeregisterThreshold()
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct document title" in {
      document.title shouldBe CannotDeregisterThresholdMessages.title
    }

    s"have the correct back text" in {
      elementText(Selectors.back) shouldBe CommonMessages.back
      element(Selectors.back).attr("href") shouldBe controllers.routes.NextTaxableTurnoverController.show().url
    }

    s"have the correct page heading" in {
      elementText(Selectors.pageHeading) shouldBe CannotDeregisterThresholdMessages.title
    }

    "have the correct text displayed in the first paragraph" in {
      elementText(Selectors.text) shouldBe CannotDeregisterThresholdMessages.text
    }

    "have the correct text and url for the link" in {
      elementText(Selectors.linkText) shouldBe CannotDeregisterThresholdMessages.linkText
      element(Selectors.link).attr("href") shouldBe mockConfig.manageVatSubscriptionFrontendUrl
    }
  }
}
