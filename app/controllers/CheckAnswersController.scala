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
    for {
      deregReason <- deregReasonAnswerService.getAnswer
      ceased <- ceasedTradingDateAnswerService.getAnswer
      turnover <- taxableTurnoverAnswerService.getAnswer
      nextTurnover <- nextTaxableTurnoverAnswerService.getAnswer
      whyBelow <- whyTurnoverBelowAnswerService.getAnswer
      accounting <- accountingMethodAnswerService.getAnswer
      optionTax <- optionTaxAnswerService.getAnswer
      stocks <- stocksAnswerService.getAnswer
      capital <- capitalAssetsAnswerService.getAnswer
      owesMoney <- owesMoneyAnswerService.getAnswer
      deregDate <- deregDateAnswerService.getAnswer
    } yield {
      Ok(views.html.checkYourAnswers(
        createAnswers(retrieveModel(deregReason),Seq("checkYourAnswers.question.reason"),"e") ++
        createAnswers(retrieveModel(ceased),Seq("checkYourAnswers.question.ceasedTrading"),"c") ++
        createAnswers(retrieveModel(turnover),Seq("checkYourAnswers.question.taxableTurnover"),"j") ++
        createAnswers(retrieveModel(nextTurnover),Seq("checkYourAnswers.question.owesMoney"),"f") ++
        createAnswers(retrieveModel(whyBelow),Seq("checkYourAnswers.question.whyBelow"),"k") ++
        createAnswers(retrieveModel(accounting),Seq("checkYourAnswers.question.VatAccounts"),"a") ++
        createAnswers(retrieveModel(optionTax),Seq("checkYourAnswers.question.optionTax","checkYourAnswers.question.optionTaxValue"),"g") ++
        createAnswers(retrieveModel(stocks),Seq("checkYourAnswers.question.stock","checkYourAnswers.question.stockValue"),"i") ++
        createAnswers(retrieveModel(capital),Seq("checkYourAnswers.question.capitalAssets","checkYourAnswers.question.capitalAssetsValue"),"url") ++
        createAnswers(retrieveModel(owesMoney),Seq("owesMoney"),"h") ++
        createAnswers(retrieveModel(deregDate),Seq("checkYourAnswers.question.deregistrationDate",""),"d")
      ))
    }
  }

  val submit: Action[AnyContent] = TODO

  def retrieveModel(answerModel: Either[ErrorModel,Option[BaseAnswerModel]]): Option[BaseAnswerModel] = {
    answerModel match {
      case Right(None) => None
      case Right(answer) => answer
      case _ => None
    }
  }

  def createAnswers(model: Option[BaseAnswerModel], question: Seq[String], url: String): Seq[DeregCheckYourAnswerModel] = {
    model match {
      case Some(answer) if answer.getAnswer.size > 1 => {Seq(
        DeregCheckYourAnswerModel(question.head, answer.getAnswer.head, url),
        DeregCheckYourAnswerModel(question(1), answer.getAnswer(1), url)
      )}
      case Some(answer) => Seq(DeregCheckYourAnswerModel(question.head, answer.getAnswer.head, url))
      case _ => Seq.empty[DeregCheckYourAnswerModel]
    }
  }
}
