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

import assets.messages.{CommonMessages,DeregReasonMessages}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import forms.DeregistrationReasonForm


class DeregistrationReasonSpec extends ViewBaseSpec {

  "Rendering the Deregistration reason page" should {

    object Selectors {
      val back = "#link-back"
      val pageHeading = "#content h1"
      val reasonOption = (number: Int) => s"fieldset > div:nth-of-type($number) > label"
      val button = ".button"
    }

    lazy val view = views.html.deregistrationReason(DeregistrationReasonForm.deregistrationReasonForm)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct document title" in {
      document.title shouldBe DeregReasonMessages.title
    }

    s"have the correct back text" in {
      elementText(Selectors.back) shouldBe CommonMessages.back
      element(Selectors.back).attr("href") shouldBe "#"
    }

    s"have the correct page heading" in {
      elementText(Selectors.pageHeading) shouldBe DeregReasonMessages.title
    }

    s"have the correct a radio button form with the correct 3 options" in {
      elementText(Selectors.reasonOption(1)) shouldBe DeregReasonMessages.reason1
      elementText(Selectors.reasonOption(2)) shouldBe DeregReasonMessages.reason2
      elementText(Selectors.reasonOption(3)) shouldBe DeregReasonMessages.reason3
    }

    s"have the correct continue button text and url" in {
      elementText(Selectors.button) shouldBe CommonMessages.continue
    }
  }
}
