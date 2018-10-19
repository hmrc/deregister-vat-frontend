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

import assets.messages.{CheckYourAnswersMessages, CommonMessages}
import models.CheckYourAnswersRowModel
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.Html

class CheckYourAnswersSpec extends ViewBaseSpec {

  object Selectors {
    val back = ".link-back"
    val pageHeading = "#content h1"
    val questionColumn: Int => String = row => s"#content > article > div > dl > div:nth-child($row) > dt"
    val answerColumn: Int => String = row => s"#content > article > div > dl > div:nth-child($row) > dd.cya-answer"
    val changeColumn: Int => String = row => s"#content > article > div > dl > div:nth-child($row) > dd.cya-change > a"
    val text = "#content > article > p"
    val button = ".button"
  }

  "Rendering the Check your answers page" should {

    val checkYourAnswersModel = Seq(
      CheckYourAnswersRowModel("test question 1", Html("test answer 1"), "test/url/1", "test hidden text 1"),
      CheckYourAnswersRowModel("test question 2", Html("test answer 2"), "test/url/2", "test hidden text 2")
    )
    lazy val view = views.html.checkYourAnswers("testUrl",checkYourAnswersModel)(user,messages,mockConfig)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct document title" in {
      document.title shouldBe CheckYourAnswersMessages.title
    }

    s"have the correct back text" in {
      elementText(Selectors.back) shouldBe CommonMessages.back
      element(Selectors.back).attr("href") shouldBe "testUrl"
    }

    s"have the correct page heading" in {
      elementText(Selectors.pageHeading) shouldBe CheckYourAnswersMessages.title
    }

    "have the correct questions being displayed being displayed" in {
      elementText(Selectors.questionColumn(1)) shouldBe "test question 1"
      elementText(Selectors.questionColumn(2)) shouldBe "test question 2"
    }

    "have the correct answers being displayed being displayed" in {
      elementText(Selectors.answerColumn(1)) shouldBe "test answer 1"
      elementText(Selectors.answerColumn(2)) shouldBe "test answer 2"
    }

    "have the correct links being displayed being displayed" in {
      elementText(Selectors.changeColumn(1)) shouldBe CheckYourAnswersMessages.change
      element(Selectors.changeColumn(1)).attr("href") shouldBe "test/url/1"
      elementText(Selectors.changeColumn(2)) shouldBe CheckYourAnswersMessages.change
      element(Selectors.changeColumn(2)).attr("href") shouldBe "test/url/2"
    }

    "have the correct hidden text" in {
      element(Selectors.changeColumn(1)).attr("aria-label") shouldBe "test hidden text 1"
      element(Selectors.changeColumn(2)).attr("aria-label") shouldBe "test hidden text 2"
    }

    s"have the correct confirmation text displayed" in {
      elementText(Selectors.text) shouldBe CheckYourAnswersMessages.text
    }

    s"have the correct continue button text and url" in {
      elementText(Selectors.button) shouldBe CheckYourAnswersMessages.confirm
    }
  }

  "Rendering the Check your answers page with no questions" should {

    val checkYourAnswersModel = Seq()

    lazy val view = views.html.checkYourAnswers("testUrl",checkYourAnswersModel)(user,messages,mockConfig)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct document title" in {
      document.title shouldBe CheckYourAnswersMessages.title
    }

    s"have the correct page heading" in {
      elementText(Selectors.pageHeading) shouldBe CheckYourAnswersMessages.title
    }

    "have no answers" in {
      document.select(Selectors.questionColumn(1)).isEmpty shouldBe true
      document.select(Selectors.answerColumn(1)).isEmpty shouldBe true
      document.select(Selectors.changeColumn(1)).isEmpty shouldBe true
      document.select(Selectors.questionColumn(2)).isEmpty shouldBe true
      document.select(Selectors.answerColumn(2)).isEmpty shouldBe true
      document.select(Selectors.changeColumn(2)).isEmpty shouldBe true
    }

    s"have the correct confirmation text displayed" in {
      elementText(Selectors.text) shouldBe CheckYourAnswersMessages.text
    }

    s"have the correct continue button text and url" in {
      elementText(Selectors.button) shouldBe CheckYourAnswersMessages.confirm
    }
  }

}