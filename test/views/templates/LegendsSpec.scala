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

package views.templates

import play.twirl.api.Html

class LegendsSpec extends TemplateBaseSpec {

  val legend = "I am a legend, for real"

  "Legends" when {

    "Called with display legend as header set to true" should {

      s"render the correct header legend markup" in {
        val expected = Html(generateExpectedLegendMarkup(legend))
        val actual = views.html.templates.legends(legend, asHeader = true)

        formatHtml(actual) shouldBe formatHtml(expected)
      }
    }

    "Called with display legend as header set to false" should {

      s"render the correct header legend markup" in {
        val expected = Html(generateExpectedLegendMarkup(legend, asHeader = false))
        val actual = views.html.templates.legends(legend, asHeader = false)

        formatHtml(actual) shouldBe formatHtml(expected)
      }
    }
  }
}