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

package controllers

import javax.inject.{Inject, Singleton}
import config.AppConfig
import controllers.predicates.AuthPredicate
import models._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import services._
import uk.gov.hmrc.play.bootstrap.controller.FrontendController

import scala.concurrent.Future

@Singleton
class CheckAnswersController @Inject()(val messagesApi: MessagesApi,
                                       val authenticate: AuthPredicate,
                                       val accountingMethodAnswerService: AccountingMethodAnswerService,
                                       val capitalAssetsAnswerService: CapitalAssetsAnswerService,
                                       val ceasedTradingDateAnswerService: CeasedTradingDateAnswerService,
                                       val deregDateAnswerService: DeregDateAnswerService,
                                       val deregReasonAnswerService: DeregReasonAnswerService,
                                       val nextTaxableTurnoverAnswerService: NextTaxableTurnoverAnswerService,
                                       val optionTaxAnswerService: OptionTaxAnswerService,
                                       val owesMoneyAnswerService: OwesMoneyAnswerService,
                                       val stocksAnswerService: StocksAnswerService,
                                       val taxableTurnoverAnswerService: TaxableTurnoverAnswerService,
                                       val whyTurnoverBelowAnswerService: WhyTurnoverBelowAnswerService,
                                       implicit val appConfig: AppConfig) extends FrontendController with I18nSupport {



  val show: Action[AnyContent] = authenticate.async { implicit user =>
    val seq = for {
      accounting <- accountingMethodAnswerService.getAnswer
      capital <- capitalAssetsAnswerService.getAnswer
      ceased <- ceasedTradingDateAnswerService.getAnswer
      deregDate <- deregDateAnswerService.getAnswer
      deregReason <- deregReasonAnswerService.getAnswer
      nextTurnover <- nextTaxableTurnoverAnswerService.getAnswer
      optionTax <- optionTaxAnswerService.getAnswer
      owesMoney <- owesMoneyAnswerService.getAnswer
      stocks <- stocksAnswerService.getAnswer
      turnover <- taxableTurnoverAnswerService.getAnswer
      whyBelow <- whyTurnoverBelowAnswerService.getAnswer
    } yield {
      DeregCheckYourAnswersModel.customApply(
        method(accounting),
        method(capital),
        method(ceased),
        method(deregDate),
        method(deregReason),
        method(nextTurnover),
        method(optionTax),
        method(owesMoney),
        method(stocks),
        method(turnover),
        method(whyBelow)
      )
    }
    Future.successful(Ok(""))
  }

  val submit: Action[AnyContent] = TODO

  private[CheckAnswersController] def method[T](thingy: Either[ErrorModel, Option[T]]): Option[T] = {
    thingy match {
      case Right(answer) => answer
      case _ => None
    }
  }
}
