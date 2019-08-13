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

import assets.messages.{CommonMessages, DeregisterForVATMessages}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class DeregisterForVATSpec extends ViewBaseSpec {

  object Selectors {
    val back = ".link-back"
    val pageHeading = "#content h1"
    val button = ".button"
    val para = "#content article > p"
    val bullets: Int => String = n => s"#content > article > ul > li:nth-child($n)"
  }

  "Rendering the Deregistration reason page" should {

    lazy val view = views.html.deregisterForVAT()(user,messages,mockConfig)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct document title" in {
      document.title shouldBe DeregisterForVATMessages.title
    }

    s"have the correct back text" in {
      elementText(Selectors.back) shouldBe CommonMessages.back
      element(Selectors.back).attr("href") shouldBe mockConfig.manageVatSubscriptionFrontendUrl
    }

    s"have the correct page heading" in {
      elementText(Selectors.pageHeading) shouldBe DeregisterForVATMessages.heading
    }

    s"have the correct continue button text and url" in {
      elementText(Selectors.button) shouldBe CommonMessages.continue
      element(Selectors.button).attr("href") shouldBe controllers.routes.DeregistrationReasonController.show().url
    }

    s"have the correct paragraph" in {
      elementText(Selectors.para) shouldBe DeregisterForVATMessages.p1
    }

    s"have the correct bullet points" in {
      elementText(Selectors.bullets(1)) shouldBe DeregisterForVATMessages.bullet1
      elementText(Selectors.bullets(2)) shouldBe DeregisterForVATMessages.bullet2
      elementText(Selectors.bullets(3)) shouldBe DeregisterForVATMessages.bullet3
    }

  }

}
