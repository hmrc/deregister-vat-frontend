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
      Ok(views.html.checkYourAnswers(
          createAnswers(retrieveModel(accounting),Seq("accounting"),"a") ++
          createAnswers(retrieveModel(capital),Seq("capital","b"),"url") ++
          createAnswers(retrieveModel(ceased),Seq("ceased"),"c") ++
          createAnswers(retrieveModel(deregDate),Seq("deregDate",""),"d") ++
          createAnswers(retrieveModel(deregReason),Seq("deregReason"),"e") ++
          createAnswers(retrieveModel(nextTurnover),Seq("nextTurnover"),"f") ++
          createAnswers(retrieveModel(optionTax),Seq("optionTax",""),"g") ++
          createAnswers(retrieveModel(owesMoney),Seq("owesMoney"),"h") ++
          createAnswers(retrieveModel(stocks),Seq("stocks",""),"i") ++
          createAnswers(retrieveModel(turnover),Seq("turnover"),"j") ++
          createAnswers(retrieveModel(whyBelow),Seq("whyBelow"),"k")
        )
      )
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
