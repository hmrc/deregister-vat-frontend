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

package assets.constants

import assets.constants.YesNoAmountTestConstants._
import assets.constants.NextTaxableTurnoverTestConstants._
import assets.messages.{CheckYourAnswersMessages, CommonMessages, WhyTurnoverBelowMessages}
import models._
import play.twirl.api.Html
import utils.{MoneyFormatter, TestUtil}


object CheckYourAnswersTestConstants extends TestUtil {

  val dateModel = DateModel(1,1,2018)
  val deregistrationDate = DeregistrationDateModel(Yes,Some(DateModel(1,1,2018)))

  val deregReasonRow = CheckYourAnswersRowModel(
    CheckYourAnswersMessages.reason,
    Html(CheckYourAnswersMessages.reasonCeased),
    controllers.routes.DeregistrationReasonController.show().url,
    CheckYourAnswersMessages.reasonHidden,
    "reason"
  )

  val ceasedTradingRow = CheckYourAnswersRowModel(
    CheckYourAnswersMessages.ceasedTrading,
    Html(dateModel.longDate),
    controllers.routes.CeasedTradingDateController.show().url,
    CheckYourAnswersMessages.ceasedTradingHidden,
    "ceased-trading"
  )

  val taxableTurnoverRow: String => CheckYourAnswersRowModel = threshold => CheckYourAnswersRowModel(
    CheckYourAnswersMessages.taxableTurnover(threshold),
    Html(CommonMessages.yes),
    controllers.routes.TaxableTurnoverController.show().url,
    CheckYourAnswersMessages.taxableTurnoverHidden,
    "below-threshold"
  )

  val nextTaxableTurnoverRow = CheckYourAnswersRowModel(
    CheckYourAnswersMessages.nextTaxableTurnover,
    MoneyFormatter.formatHtmlAmount(nextTaxableTurnoverBelow.value),
    controllers.routes.NextTaxableTurnoverController.show().url,
    CheckYourAnswersMessages.nextTaxableTurnoverHidden,
    "expected-turnover"
  )

  val whyBelowRowMax = CheckYourAnswersRowModel(
    CheckYourAnswersMessages.whyBelow,
    Html(s"${WhyTurnoverBelowMessages.reason1}, " +
         s"${WhyTurnoverBelowMessages.reason2}, " +
         s"${WhyTurnoverBelowMessages.reason3}, " +
         s"${WhyTurnoverBelowMessages.reason4}, " +
         s"${WhyTurnoverBelowMessages.reason5}, " +
         s"${WhyTurnoverBelowMessages.reason6}, " +
         s"${WhyTurnoverBelowMessages.reason7}"
    ),
    controllers.routes.WhyTurnoverBelowController.show().url,
    CheckYourAnswersMessages.whyBelowHidden,
    "why-turnover-below"
  )

  val whyBelowRowMin = CheckYourAnswersRowModel(
    CheckYourAnswersMessages.whyBelow,
    Html(WhyTurnoverBelowMessages.reason1),
    controllers.routes.WhyTurnoverBelowController.show().url,
    CheckYourAnswersMessages.whyBelowHidden,
    "why-turnover-below"
  )

  val vatAccountsRow = CheckYourAnswersRowModel(
    CheckYourAnswersMessages.vatAccounts,
    Html(CheckYourAnswersMessages.standard),
    controllers.routes.VATAccountsController.show().url,
    CheckYourAnswersMessages.vatAccountsHidden,
    "accounting"
  )

  val optionTaxRowYes = CheckYourAnswersRowModel(
    CheckYourAnswersMessages.optionTax,
    Html(CommonMessages.yes),
    controllers.routes.OptionTaxController.show().url,
    CheckYourAnswersMessages.optionTaxHidden,
    "option-to-tax"
  )

  val optionTaxRowNo = CheckYourAnswersRowModel(
    CheckYourAnswersMessages.optionTax,
    Html(CommonMessages.no),
    controllers.routes.OptionTaxController.show().url,
    CheckYourAnswersMessages.optionTaxHidden,
    "option-to-tax"
  )

  val optionTaxValueRow = CheckYourAnswersRowModel(
    CheckYourAnswersMessages.optionTaxValue,
    MoneyFormatter.formatHtmlAmount(ottModel.amount.get),
    controllers.routes.OptionTaxController.show().url,
    CheckYourAnswersMessages.optionTaxValueHidden,
    "option-to-tax-value"
  )

  val stocksRowYes = CheckYourAnswersRowModel(
    CheckYourAnswersMessages.stocks,
    Html(CommonMessages.yes),
    controllers.routes.OptionStocksToSellController.show().url,
    CheckYourAnswersMessages.stocksHidden,
    "stock"
  )

  val stocksRowNo = CheckYourAnswersRowModel(
    CheckYourAnswersMessages.stocks,
    Html(CommonMessages.no),
    controllers.routes.OptionStocksToSellController.show().url,
    CheckYourAnswersMessages.stocksHidden,
    "stock"
  )

  val stocksValueRow = CheckYourAnswersRowModel(
    CheckYourAnswersMessages.stocksValue,
    MoneyFormatter.formatHtmlAmount(stocksModel.amount.get),
    controllers.routes.OptionStocksToSellController.show().url,
    CheckYourAnswersMessages.stocksValueHidden,
    "stock-value"
  )

  val captialAssetsRowYes = CheckYourAnswersRowModel(
    CheckYourAnswersMessages.capitalAssets,
    Html(CommonMessages.yes),
    controllers.routes.CapitalAssetsController.show().url,
    CheckYourAnswersMessages.capitalAssetsHidden,
    "capital"
  )

  val captialAssetsRowNo = CheckYourAnswersRowModel(
    CheckYourAnswersMessages.capitalAssets,
    Html(CommonMessages.no),
    controllers.routes.CapitalAssetsController.show().url,
    CheckYourAnswersMessages.capitalAssetsHidden,
    "capital"
  )

  val captialAssetsValueRow = CheckYourAnswersRowModel(
    CheckYourAnswersMessages.capitalAssetsValue,
    MoneyFormatter.formatHtmlAmount(assetsModel.amount.get),
    controllers.routes.CapitalAssetsController.show().url,
    CheckYourAnswersMessages.capitalAssetsValueHidden,
    "capital-value"
  )

  val outstandingInvoicesRow = CheckYourAnswersRowModel(
    CheckYourAnswersMessages.outstandingInvoices,
    Html(CommonMessages.yes),
    controllers.routes.OutstandingInvoicesController.show().url,
    CheckYourAnswersMessages.outstandingInvoiceHidden,
    "outstanding-invoices"
  )

  val newInvoicesRow = CheckYourAnswersRowModel(
    CheckYourAnswersMessages.newInvoices,
    Html(CommonMessages.yes),
    controllers.routes.IssueNewInvoicesController.show().url,
    CheckYourAnswersMessages.newInvoicesHidden,
    "new-invoices"
  )

  val deregDateRow = CheckYourAnswersRowModel(
    CheckYourAnswersMessages.deregistrationDate,
    Html(deregistrationDate.date.get.longDate),
    controllers.routes.DeregistrationDateController.show().url,
    CheckYourAnswersMessages.deregistrationDateHidden,
    "dereg-date"
  )
}
