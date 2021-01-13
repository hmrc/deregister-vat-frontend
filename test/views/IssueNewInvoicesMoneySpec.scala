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

import assets.messages.{CommonMessages, IssueNewInvoicesMessages}
import forms.YesNoForm
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import views.html.IssueNewInvoices

class IssueNewInvoicesMoneySpec extends ViewBaseSpec {

  lazy val issueNewInvoices: IssueNewInvoices = injector.instanceOf[IssueNewInvoices]

  object Selectors {
    val back = ".link-back"
    val pageHeading = "#content h1"
    val yesOption = "div.multiple-choice:nth-child(1) > label:nth-child(2)"
    val noOption = "div.multiple-choice:nth-child(2) > label:nth-child(2)"
    val button = ".button"
    val errorHeading = "#error-summary-display"
    val error = ".error-message"
  }

  "Rendering the option to tax page with no errors" should {

    lazy val view = issueNewInvoices(YesNoForm.yesNoForm("issueNewInvoices.error.mandatoryRadioOption"))
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct document title" in {
      document.title shouldBe IssueNewInvoicesMessages.title
    }

    "have the correct back text" in {
      elementText(Selectors.back) shouldBe CommonMessages.back
      element(Selectors.back).attr("href") shouldBe controllers.routes.OptionStocksToSellController.show().url
    }

    "have the correct page heading" in {
      elementText(Selectors.pageHeading) shouldBe IssueNewInvoicesMessages.heading
    }

    "not display an error heading" in {
      document.select(Selectors.errorHeading).isEmpty shouldBe true
    }

    "have the correct a radio button form with yes/no answers" in {
      elementText(Selectors.yesOption) shouldBe CommonMessages.yes
      elementText(Selectors.noOption) shouldBe CommonMessages.no
    }

    "have the correct continue button text and url" in {
      elementText(Selectors.button) shouldBe CommonMessages.continue
    }

    "not display an error" in {
      document.select(Selectors.error).isEmpty shouldBe true
    }
  }

  "Rendering the option to tax page with errors" should {

    lazy val view = issueNewInvoices(YesNoForm.yesNoForm("issueNewInvoices.error.mandatoryRadioOption").bind(Map("yes_no" -> "")))
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct document title" in {
      document.title shouldBe s"${CommonMessages.errorTitlePrefix} ${IssueNewInvoicesMessages.title}"
    }

    "have the correct back text" in {
      elementText(Selectors.back) shouldBe CommonMessages.back
      element(Selectors.back).attr("href") shouldBe controllers.routes.OptionStocksToSellController.show().url
    }

    "have the correct page heading" in {
      elementText(Selectors.pageHeading) shouldBe IssueNewInvoicesMessages.heading
    }

    "display the correct error heading" in {
      elementText(Selectors.errorHeading) shouldBe s"${CommonMessages.errorHeading} ${IssueNewInvoicesMessages.yesNoError}"
    }

    "have the correct a radio button form with yes/no answers" in {
      elementText(Selectors.yesOption) shouldBe CommonMessages.yes
      elementText(Selectors.noOption) shouldBe CommonMessages.no
    }

    "have the correct continue button text and url" in {
      elementText(Selectors.button) shouldBe CommonMessages.continue
    }

    "display the correct error messages" in {
      elementText(Selectors.error) shouldBe s"${CommonMessages.errorPrefix} ${IssueNewInvoicesMessages.yesNoError}"
    }
  }

}
