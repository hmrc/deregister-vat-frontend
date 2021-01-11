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
                                 chooseDeregDate: Option[YesNo],
                                 deregDate: Option[DateModel],
                                 businessActivityChanged: Option[YesNo],
                                 sicCode: Option[String],
                                 zeroRatedSupplies: Option[NumberInputModel],
                                 purchasesExceedSupplies: Option[YesNo])
                                (implicit messages: Messages, appConfig: AppConfig) {

  def seqAnswers: Seq[CheckYourAnswersRowModel] = deregistrationReason match {
    case Some(ZeroRated) => zeroRatedAnswerSequence.flatten
    case _ => standardAnswerSequence.flatten
  }

  private val deregReasonAnswer = deregistrationReason.map(answer => CheckYourAnswersRowModel(
    messages("checkYourAnswers.question.reason"),
    Html(messages(s"checkYourAnswers.answer.reason.${answer.value}", appConfig.deregThreshold)),
    controllers.routes.DeregistrationReasonController.show().url,
    messages("checkYourAnswers.hidden.reason")
  ))

  private val ceasedTradingDateAnswer = ceasedTradingDate.map(answer => CheckYourAnswersRowModel(
    messages("checkYourAnswers.question.ceasedTrading"),
    Html(answer.longDate),
    controllers.routes.CeasedTradingDateController.show().url,
    messages("checkYourAnswers.hidden.ceasedTrading")
  ))

  private val turnoverAnswer = turnover.map(answer => CheckYourAnswersRowModel(
    messages("checkYourAnswers.question.taxableTurnover", appConfig.deregThreshold),
    Html(messages(s"common.${answer.toString}")),
    controllers.routes.TaxableTurnoverController.show().url,
    messages("checkYourAnswers.hidden.taxableTurnover")
  ))

  private val nextTurnoverAnswer = nextTurnover.map(answer => CheckYourAnswersRowModel(
    messages("checkYourAnswers.question.nextTaxableTurnover"),
    MoneyFormatter.formatHtmlAmount(answer.value),
    controllers.routes.NextTaxableTurnoverController.show().url,
    messages("checkYourAnswers.hidden.nextTaxableTurnover")
  ))

  private val whyTurnoverBelowAnswer = whyTurnoverBelow.map(answer =>
    CheckYourAnswersRowModel(
      messages("checkYourAnswers.question.whyBelow"),
      Html(answer.asSequence.collect {
        case (true, message) => messages(s"whyTurnoverBelow.reason.$message")
      }.mkString(", ")),
      controllers.routes.WhyTurnoverBelowController.show().url,
      messages("checkYourAnswers.hidden.whyBelow")
    ))

  private val accountingAnswer = accounting.map(answer => CheckYourAnswersRowModel(
    messages("checkYourAnswers.question.vatAccounts"),
    Html(messages(s"checkYourAnswers.answer.${answer.value}")),
    controllers.routes.VATAccountsController.show().url,
    messages("checkYourAnswers.hidden.VatAccounts")
  ))

  private val optionTaxAnswer = optionTax.map(answer => CheckYourAnswersRowModel(
    messages("checkYourAnswers.question.optionTax"),
    Html(messages(s"common.${answer.yesNo.toString}")),
    controllers.routes.OptionTaxController.show().url,
    messages("checkYourAnswers.hidden.optionTax")
  ))

  private val optionTaxValueAnswer = optionTax.flatMap(_.amount.map(answer => CheckYourAnswersRowModel(
    messages("checkYourAnswers.question.optionTaxValue"),
    MoneyFormatter.formatHtmlAmount(answer),
    controllers.routes.OptionTaxController.show().url,
    messages("checkYourAnswers.hidden.optionTaxValue")
  )))

  private val capitalAssetsAnswer = capitalAssets.map(answer => CheckYourAnswersRowModel(
    messages("checkYourAnswers.question.capitalAssets"),
    Html(messages(s"common.${answer.yesNo.toString}")),
    controllers.routes.CapitalAssetsController.show().url,
    messages("checkYourAnswers.hidden.capitalAssets")
  ))

  private val capitalAssetsValueAnswer = capitalAssets.flatMap(_.amount.map(answer => CheckYourAnswersRowModel(
    messages("checkYourAnswers.question.capitalAssetsValue"),
    MoneyFormatter.formatHtmlAmount(answer),
    controllers.routes.CapitalAssetsController.show().url,
    messages("checkYourAnswers.hidden.capitalAssetsValue")
  )))

  private val stocksAnswer = stocks.map(answer => CheckYourAnswersRowModel(
    messages("checkYourAnswers.question.stocks"),
    Html(messages(s"common.${answer.yesNo.toString}")),
    controllers.routes.OptionStocksToSellController.show().url,
    messages("checkYourAnswers.hidden.stocks")
  ))

  private val stocksValueAnswer = stocks.flatMap(_.amount.map(answer => CheckYourAnswersRowModel(
    messages("checkYourAnswers.question.stocksValue"),
    MoneyFormatter.formatHtmlAmount(answer),
    controllers.routes.OptionStocksToSellController.show().url,
    messages("checkYourAnswers.hidden.stocksValue")
  )))

  private val outstandingInvoicesAnswer = outstandingInvoices.map(answer => CheckYourAnswersRowModel(
    messages("checkYourAnswers.question.outstandingInvoice"),
    Html(messages(s"common.${answer.toString}")),
    controllers.routes.OutstandingInvoicesController.show().url,
    messages("checkYourAnswers.hidden.outstandingInvoice")
  ))

  private val newInvoicesAnswer = newInvoices.map(answer => CheckYourAnswersRowModel(
    messages("checkYourAnswers.question.newInvoices"),
    Html(messages(s"common.${answer.toString}")),
    controllers.routes.IssueNewInvoicesController.show().url,
    messages("checkYourAnswers.hidden.newInvoices")
  ))

  private val chooseDeregDateAnswer = chooseDeregDate.map(answer => CheckYourAnswersRowModel(
    messages("checkYourAnswers.question.chooseDeregistrationDate"),
    if(answer.value)
      {Html(messages("common.yes"))}
    else
      {Html(messages("common.no"))},
    controllers.routes.ChooseDeregistrationDateController.show().url,
    messages("checkYourAnswers.hidden.chooseDeregistrationDate")
  ))

  private val deregDateAnswer = deregDate.map(answer => CheckYourAnswersRowModel(
    messages("checkYourAnswers.question.deregistrationDate"),
    Html(answer.longDate),
    controllers.routes.DeregistrationDateController.show().url,
    messages("checkYourAnswers.hidden.deregistrationDate")
  ))

  private val businessActivityChangedAnswer = businessActivityChanged.map(answer => CheckYourAnswersRowModel(
    messages("checkYourAnswers.question.businessActivityChanged"),
    Html(messages(s"common.${answer.toString}")),
    controllers.zeroRated.routes.BusinessActivityController.show().url,
    messages("checkYourAnswers.hidden.businessActivityChanged")
  ))

  private val sicCodeAnswer = sicCode.map(answer => CheckYourAnswersRowModel(
    messages("checkYourAnswers.question.sicCode"),
    Html(answer),
    controllers.zeroRated.routes.SicCodeController.show().url,
    messages("checkYourAnswers.hidden.sicCode")
  ))

  private val zeroRatedSuppliesAnswer = zeroRatedSupplies.map(answer => CheckYourAnswersRowModel(
    messages("checkYourAnswers.question.zeroRatedSupplies"),
    MoneyFormatter.formatHtmlAmount(answer.value),
    controllers.zeroRated.routes.ZeroRatedSuppliesController.show().url,
    messages("checkYourAnswers.hidden.zeroRatedSupplies")
  ))

  private val purchasesExceedSuppliesAnswer = purchasesExceedSupplies.map(answer => CheckYourAnswersRowModel(
    messages("checkYourAnswers.question.purchasesExceedSupplies"),
    Html(messages(s"common.${answer.toString}")),
    controllers.zeroRated.routes.PurchasesExceedSuppliesController.show().url,
    messages("checkYourAnswers.hidden.purchasesExceedSupplies")
  ))

  private val commonAnswers: Seq[Option[CheckYourAnswersRowModel]] = Seq(
    accountingAnswer,
    optionTaxAnswer,
    optionTaxValueAnswer,
    capitalAssetsAnswer,
    capitalAssetsValueAnswer,
    stocksAnswer,
    stocksValueAnswer,
    newInvoicesAnswer,
    outstandingInvoicesAnswer,
    chooseDeregDateAnswer,
    deregDateAnswer
  )

  private val standardAnswerSequence: Seq[Option[CheckYourAnswersRowModel]] = Seq(
    deregReasonAnswer,
    ceasedTradingDateAnswer,
    turnoverAnswer,
    nextTurnoverAnswer,
    whyTurnoverBelowAnswer
  ) ++ commonAnswers

  private val zeroRatedAnswerSequence: Seq[Option[CheckYourAnswersRowModel]] = Seq(
    deregReasonAnswer,
    businessActivityChangedAnswer,
    sicCodeAnswer,
    nextTurnoverAnswer,
    zeroRatedSuppliesAnswer,
    purchasesExceedSuppliesAnswer
  ) ++ commonAnswers
}
