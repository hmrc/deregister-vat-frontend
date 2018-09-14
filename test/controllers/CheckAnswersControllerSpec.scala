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

///*
// * Copyright 2018 HM Revenue & Customs
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package controllers
//
//import models.{Yes, YesNoAmountModel}
//import org.jsoup.Jsoup
//import play.api.http.Status
//import play.api.test.Helpers.{contentType, _}
//import services.mocks._
//
//class CheckAnswersControllerSpec extends ControllerBaseSpec with MockAllAnswerService {
//
//  object TestCapitalAssetsController extends CheckAnswersController(
//    messagesApi,
//    mockAuthPredicate,
//    mockAccountingMethodAnswerService,
//    mockCapitalAssetsAnswerService,
//    mockCeasedTradingDateAnswerService,
//    mockDeregDateAnswerService,
//    mockDeregReasonAnswerService,
//    mockNextTaxableTurnoverAnswerService,
//    mockOptionTaxAnswerService,
//    mockOwesMoneyAnswerService,
//    mockStocksAnswerService,
//    mockTaxableTurnoverAnswerService,
//    mockWhyTurnoverBelowAnswerService,
//    mockConfig)
//
//  val amount = 12345
//
//  "the user is authorised" when {
//
//    "Calling the .show action" when {
//
//      "the user does not have a pre selected option" should {
//
//        lazy val result = TestCapitalAssetsController.show()(request)
//
//        "return 200 (OK)" in {
//          setupMockGetAnswers(Right(None))
//          mockAuthResult(mockAuthorisedIndividual)
//          status(result) shouldBe Status.OK
//        }
//
//        "return HTML" in {
//          contentType(result) shouldBe Some("text/html")
//          charset(result) shouldBe Some("utf-8")
//        }
//      }
//
//      "the user is has pre selected option" should {
//
//        lazy val result = TestCapitalAssetsController.show()(request)
//
//        "return 200 (OK)" in {
//          setupMockGetAnswers(Right(Some(YesNoAmountModel(Yes, Some(amount)))))
//          mockAuthResult(mockAuthorisedIndividual)
//          status(result) shouldBe Status.OK
//        }
//
//        "return HTML" in {
//          contentType(result) shouldBe Some("text/html")
//          charset(result) shouldBe Some("utf-8")
//        }
//
//        "should have the 'Yes' option checked and an amount already entered" in {
//          Jsoup.parse(bodyOf(result)).select("#yes_no-yes").hasAttr("checked") shouldBe true
//          Jsoup.parse(bodyOf(result)).select("#amount").attr("value") shouldBe amount.toString
//        }
//      }
//
//      authChecks(".show", TestCapitalAssetsController.show(), request)
//
//    }
//  }
//}