/*
 * Copyright 2020 HM Revenue & Customs
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
import utils.MoneyFormatter

case class CheckYourAnswersModel(deregistrationReason: Option[DeregistrationReason],
                                 ceasedTradingDate: Option[DateModel],
                                 turnover: Option[YesNo],
                                 nextTurnover: Option[NumberInputModel],
                                 whyTurnoverBelow: Option[WhyTurnoverBelowModel],
                                 accounting: Option[VATAccountsModel],
                                 optionTax: Option[YesNoAmountModel],
                                 capitalAssets: Option[YesNoAmountModel],
                                 stocks: Option[YesNoAmountModel],
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
    capitalAssetsAnswer,
    capitalAssetsValueAnswer,
    stocksAnswer,
    stocksValueAnswer,
    newInvoicesAnswer,
    outstandingInvoicesAnswer,
    deregDateAnswer
  ).flatten

  private val deregReasonAnswer = deregistrationReason.map(answer => CheckYourAnswersRowModel(
    messages("checkYourAnswers.question.reason"),
    Html(messages(s"checkYourAnswers.answer.reason.${answer.value}", appConfig.deregThreshold)),
    controllers.routes.DeregistrationReasonController.show().url,
    messages("checkYourAnswers.hidden.reason"),
    "reason"
  ))

  private val ceasedTradingDateAnswer = ceasedTradingDate.map(answer => CheckYourAnswersRowModel(
    messages("checkYourAnswers.question.ceasedTrading"),
    Html(answer.longDate),
    controllers.routes.CeasedTradingDateController.show().url,
    messages("checkYourAnswers.hidden.ceasedTrading"),
    "ceased-trading"
  ))

  private val turnoverAnswer = turnover.map(answer => CheckYourAnswersRowModel(
    messages("checkYourAnswers.question.taxableTurnover", appConfig.deregThreshold),
    Html(messages(s"common.${answer.toString}")),
    controllers.routes.TaxableTurnoverController.show().url,
    messages("checkYourAnswers.hidden.taxableTurnover"),
    "below-threshold"
  ))

  private val nextTurnoverAnswer = nextTurnover.map(answer => CheckYourAnswersRowModel(
    messages("checkYourAnswers.question.nextTaxableTurnover"),
    MoneyFormatter.formatHtmlAmount(answer.value),
    controllers.routes.NextTaxableTurnoverController.show().url,
    messages("checkYourAnswers.hidden.nextTaxableTurnover"),
    "expected-turnover"
  ))

  private val whyTurnoverBelowAnswer = whyTurnoverBelow.map(answer =>
    CheckYourAnswersRowModel(
      messages("checkYourAnswers.question.whyBelow"),
      Html(answer.asSequence.collect {
        case (true, message) => messages(s"whyTurnoverBelow.reason.$message")
      }.mkString(", ")),
      controllers.routes.WhyTurnoverBelowController.show().url,
      messages("checkYourAnswers.hidden.whyBelow"),
      "why-turnover-below"
    ))

  private val accountingAnswer = accounting.map(answer => CheckYourAnswersRowModel(
    messages("checkYourAnswers.question.vatAccounts"),
    Html(messages(s"checkYourAnswers.answer.${answer.value}")),
    controllers.routes.VATAccountsController.show().url,
    messages("checkYourAnswers.hidden.VatAccounts"),
    "accounting"
  ))

  private val optionTaxAnswer = optionTax.map(answer => CheckYourAnswersRowModel(
    messages("checkYourAnswers.question.optionTax"),
    Html(messages(s"common.${answer.yesNo.toString}")),
    controllers.routes.OptionTaxController.show().url,
    messages("checkYourAnswers.hidden.optionTax"),
    "option-to-tax"
  ))

  private val optionTaxValueAnswer = optionTax.flatMap(_.amount.map(answer => CheckYourAnswersRowModel(
    messages("checkYourAnswers.question.optionTaxValue"),
    MoneyFormatter.formatHtmlAmount(answer),
    controllers.routes.OptionTaxController.show().url,
    messages("checkYourAnswers.hidden.optionTaxValue"),
    "option-to-tax-value"
  )))

  private val capitalAssetsAnswer = capitalAssets.map(answer => CheckYourAnswersRowModel(
    messages("checkYourAnswers.question.capitalAssets"),
    Html(messages(s"common.${answer.yesNo.toString}")),
    controllers.routes.CapitalAssetsController.show().url,
    messages("checkYourAnswers.hidden.capitalAssets"),
    "capital"
  ))

  private val capitalAssetsValueAnswer = capitalAssets.flatMap(_.amount.map(answer => CheckYourAnswersRowModel(
    messages("checkYourAnswers.question.capitalAssetsValue"),
    MoneyFormatter.formatHtmlAmount(answer),
    controllers.routes.CapitalAssetsController.show().url,
    messages("checkYourAnswers.hidden.capitalAssetsValue"),
    "capital-value"
  )))

  private val stocksAnswer = stocks.map(answer => CheckYourAnswersRowModel(
    messages("checkYourAnswers.question.stocks"),
    Html(messages(s"common.${answer.yesNo.toString}")),
    controllers.routes.OptionStocksToSellController.show().url,
    messages("checkYourAnswers.hidden.stocks"),
    "stock"
  ))

  private val stocksValueAnswer = stocks.flatMap(_.amount.map(answer => CheckYourAnswersRowModel(
    messages("checkYourAnswers.question.stocksValue"),
    MoneyFormatter.formatHtmlAmount(answer),
    controllers.routes.OptionStocksToSellController.show().url,
    messages("checkYourAnswers.hidden.stocksValue"),
    "stock-value"
  )))

  private val outstandingInvoicesAnswer = outstandingInvoices.map(answer => CheckYourAnswersRowModel(
    messages("checkYourAnswers.question.outstandingInvoice"),
    Html(messages(s"common.${answer.toString}")),
    controllers.routes.OutstandingInvoicesController.show().url,
    messages("checkYourAnswers.hidden.outstandingInvoice"),
    "outstanding-invoices"
  ))

  private val newInvoicesAnswer = newInvoices.map(answer => CheckYourAnswersRowModel(
    messages("checkYourAnswers.question.newInvoices"),
    Html(messages(s"common.${answer.toString}")),
    controllers.routes.IssueNewInvoicesController.show().url,
    messages("checkYourAnswers.hidden.newInvoices"),
    "new-invoices"
  ))

  private val deregDateAnswer = deregDate.flatMap(_.date.map(answer => CheckYourAnswersRowModel(
    messages("checkYourAnswers.question.deregistrationDate"),
    Html(answer.longDate),
    controllers.routes.DeregistrationDateController.show().url,
    messages("checkYourAnswers.hidden.deregistrationDate"),
    "dereg-date"
  )))
}
