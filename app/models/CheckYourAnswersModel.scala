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

package models

import config.AppConfig
import play.api.i18n.Messages
import play.twirl.api.Html
import services.OutstandingInvoicesAnswerService
import utils.MoneyFormatter

case class CheckYourAnswersModel(deregistrationReason: Option[DeregistrationReason],
                                 ceasedTradingDate: Option[DateModel],
                                 turnover: Option[YesNo],
                                 nextTurnover: Option[NextTaxableTurnoverModel],
                                 whyTurnoverBelow: Option[WhyTurnoverBelowModel],
                                 accounting: Option[VATAccountsModel],
                                 optionTax: Option[YesNoAmountModel],
                                 stocks: Option[YesNoAmountModel],
                                 capitalAssets: Option[YesNoAmountModel],
                                 newInvoices: Option[YesNo],
                                 outstandingInvoices: Option[YesNo],
                                 deregDate: Option[DeregistrationDateModel])(implicit user: User[_], messages: Messages, appConfig: AppConfig) {


  def seqAnswers: Seq[CheckYourAnswersRowModel] = Seq(
    deregReasonAnswer,
    ceasedTradingDateAnswer,
    turnoverAnswer,
    nextTurnoverAnswer,
    whyTurnoverBelowAnswer,
    accountingAnswer,
    optionTaxAnswer,
    optionTaxValueAnswer,
    stocksAnswer,
    stocksValueAnswer,
    capitalAssetsAnswer,
    capitalAssetsValueAnswer,
    newInvoicesAnswer,
    outstandingInvoicesAnswer,
    deregDateAnswer
  ).flatten

  private val deregReasonAnswer = deregistrationReason.map(answer => CheckYourAnswersRowModel(
    messages("checkYourAnswers.question.reason"),
    Html(messages(s"deregistrationReason.reason.${answer.value}", appConfig.deregThreshold)),
    controllers.routes.DeregistrationReasonController.show(user.isAgent).url
  ))

  private val ceasedTradingDateAnswer = ceasedTradingDate.map(answer => CheckYourAnswersRowModel(
    messages("checkYourAnswers.question.ceasedTrading"),
    Html(answer.longDate),
    controllers.routes.CeasedTradingDateController.show().url
  ))

  private val turnoverAnswer = turnover.map(answer => CheckYourAnswersRowModel(
    messages("checkYourAnswers.question.taxableTurnover", appConfig.deregThreshold),
    Html(messages(s"common.${answer.toString}")),
    controllers.routes.TaxableTurnoverController.show().url
  ))

  private val nextTurnoverAnswer = nextTurnover.map(answer => CheckYourAnswersRowModel(
    messages("checkYourAnswers.question.nextTaxableTurnover"),
    MoneyFormatter.formatHtmlAmount(answer.turnover),
    controllers.routes.NextTaxableTurnoverController.show().url
  ))

  private val whyTurnoverBelowAnswer = whyTurnoverBelow.map(answer =>
    CheckYourAnswersRowModel(
      messages("checkYourAnswers.question.whyBelow"),
      Html(answer.asSequence.collect {
        case (true, message) => messages(s"whyTurnoverBelow.reason.$message")
      }.mkString(", ")),
      controllers.routes.WhyTurnoverBelowController.show().url
  ))

  private val accountingAnswer = accounting.map(answer => CheckYourAnswersRowModel(
    messages("checkYourAnswers.question.VatAccounts"),
    Html(messages(s"checkYourAnswers.answer.${answer.value}")),
    controllers.routes.VATAccountsController.show().url
  ))

  private val optionTaxAnswer = optionTax.map(answer => CheckYourAnswersRowModel(
    messages("checkYourAnswers.question.optionTax"),
    Html(messages(s"common.${answer.yesNo.toString}")),
    controllers.routes.OptionTaxController.show().url
  ))

  private val optionTaxValueAnswer = optionTax.flatMap(_.amount.map(answer => CheckYourAnswersRowModel(
    messages("checkYourAnswers.question.optionTaxValue"),
    MoneyFormatter.formatHtmlAmount(answer),
    controllers.routes.OptionTaxController.show().url
  )))

  private val stocksAnswer = stocks.map(answer => CheckYourAnswersRowModel(
    messages("checkYourAnswers.question.stocks"),
    Html(messages(s"common.${answer.yesNo.toString}")),
    controllers.routes.OptionStocksToSellController.show().url
  ))

  private val stocksValueAnswer = stocks.flatMap(_.amount.map(answer => CheckYourAnswersRowModel(
    messages("checkYourAnswers.question.stocksValue"),
    MoneyFormatter.formatHtmlAmount(answer),
    controllers.routes.OptionStocksToSellController.show().url
  )))

  private val capitalAssetsAnswer = capitalAssets.map(answer => CheckYourAnswersRowModel(
    messages("checkYourAnswers.question.capitalAssets"),
    Html(messages(s"common.${answer.yesNo.toString}")),
    controllers.routes.CapitalAssetsController.show().url
  ))

  private val capitalAssetsValueAnswer = capitalAssets.flatMap(_.amount.map(answer => CheckYourAnswersRowModel(
    messages("checkYourAnswers.question.capitalAssetsValue"),
    MoneyFormatter.formatHtmlAmount(answer),
    controllers.routes.CapitalAssetsController.show().url
  )))

  private val outstandingInvoicesAnswer = outstandingInvoices.map(answer => CheckYourAnswersRowModel(
    messages("checkYourAnswers.question.outstandingInvoice"),
    Html(messages(s"common.${answer.toString}")),
    controllers.routes.OutstandingInvoicesController.show().url
  ))

  private val newInvoicesAnswer = newInvoices.map(answer => CheckYourAnswersRowModel(
    messages("checkYourAnswers.question.newInvoices"),
    Html(messages(s"common.${answer.toString}")),
    controllers.routes.IssueNewInvoicesController.show().url
  ))

  private val deregDateAnswer = deregDate.flatMap(_.date.map(answer => CheckYourAnswersRowModel(
    messages("checkYourAnswers.question.deregistrationDate"),
    Html(answer.longDate),
    controllers.routes.DeregistrationDateController.show().url
  )))
}
