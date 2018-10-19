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

package assets.constants

import assets.constants.YesNoAmountTestConstants._
import assets.constants.NextTaxableTurnoverTestConstants._
import assets.messages.{CheckYourAnswersMessages, CommonMessages, WhyTurnoverBelowMessages}
import models._
import play.twirl.api.Html
import utils.{MoneyFormatter, TestUtil}


object CheckYourAnswersTestConstants extends TestUtil{

  val dateModel = DateModel(1,1,2018)
  val deregistrationDate = DeregistrationDateModel(Yes,Some(DateModel(1,1,2018)))

  val deregReasonRow = CheckYourAnswersRowModel(
    CheckYourAnswersMessages.reason,
    Html(CheckYourAnswersMessages.reasonCeased),
    controllers.routes.DeregistrationReasonController.show(false).url,
    CheckYourAnswersMessages.reasonHidden
  )

  val ceasedTradingRow = CheckYourAnswersRowModel(
    CheckYourAnswersMessages.ceasedTrading,
    Html(dateModel.longDate),
    controllers.routes.CeasedTradingDateController.show().url,
    CheckYourAnswersMessages.ceasedTradingHidden
  )

  val taxableTurnoverRow: String => CheckYourAnswersRowModel = threshold => CheckYourAnswersRowModel(
    CheckYourAnswersMessages.taxableTurnover(threshold),
    Html(CommonMessages.yes),
    controllers.routes.TaxableTurnoverController.show().url,
    CheckYourAnswersMessages.taxableTurnoverHidden
  )

  val nextTaxableTurnoverRow = CheckYourAnswersRowModel(
    CheckYourAnswersMessages.nextTaxableTurnover,
    MoneyFormatter.formatHtmlAmount(nextTaxableTurnoverBelow.turnover),
    controllers.routes.NextTaxableTurnoverController.show().url,
    CheckYourAnswersMessages.nextTaxableTurnoverHidden
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
    CheckYourAnswersMessages.whyBelowHidden
  )

  val whyBelowRowMin = CheckYourAnswersRowModel(
    CheckYourAnswersMessages.whyBelow,
    Html(WhyTurnoverBelowMessages.reason1),
    controllers.routes.WhyTurnoverBelowController.show().url,
    CheckYourAnswersMessages.whyBelowHidden
  )

  val vatAccountsRow = CheckYourAnswersRowModel(
    CheckYourAnswersMessages.vatAccounts,
    Html(CheckYourAnswersMessages.standard),
    controllers.routes.VATAccountsController.show().url,
    CheckYourAnswersMessages.vatAccountsHidden
  )

  val optionTaxRowYes = CheckYourAnswersRowModel(
    CheckYourAnswersMessages.optionTax,
    Html(CommonMessages.yes),
    controllers.routes.OptionTaxController.show().url,
    CheckYourAnswersMessages.optionTaxHidden
  )

  val optionTaxRowNo = CheckYourAnswersRowModel(
    CheckYourAnswersMessages.optionTax,
    Html(CommonMessages.no),
    controllers.routes.OptionTaxController.show().url,
    CheckYourAnswersMessages.optionTaxHidden
  )

  val optionTaxValueRow = CheckYourAnswersRowModel(
    CheckYourAnswersMessages.optionTaxValue,
    MoneyFormatter.formatHtmlAmount(ottModel.amount.get),
    controllers.routes.OptionTaxController.show().url,
    CheckYourAnswersMessages.optionTaxValueHidden
  )

  val stocksRowYes = CheckYourAnswersRowModel(
    CheckYourAnswersMessages.stocks,
    Html(CommonMessages.yes),
    controllers.routes.OptionStocksToSellController.show().url,
    CheckYourAnswersMessages.stocksHidden
  )

  val stocksRowNo = CheckYourAnswersRowModel(
    CheckYourAnswersMessages.stocks,
    Html(CommonMessages.no),
    controllers.routes.OptionStocksToSellController.show().url,
    CheckYourAnswersMessages.stocksHidden
  )

  val stocksValueRow = CheckYourAnswersRowModel(
    CheckYourAnswersMessages.stocksValue,
    MoneyFormatter.formatHtmlAmount(stocksModel.amount.get),
    controllers.routes.OptionStocksToSellController.show().url,
    CheckYourAnswersMessages.stocksValueHidden
  )

  val captialAssetsRowYes = CheckYourAnswersRowModel(
    CheckYourAnswersMessages.capitalAssets,
    Html(CommonMessages.yes),
    controllers.routes.CapitalAssetsController.show().url,
    CheckYourAnswersMessages.capitalAssetsHidden
  )

  val captialAssetsRowNo = CheckYourAnswersRowModel(
    CheckYourAnswersMessages.capitalAssets,
    Html(CommonMessages.no),
    controllers.routes.CapitalAssetsController.show().url,
    CheckYourAnswersMessages.capitalAssetsHidden
  )

  val captialAssetsValueRow = CheckYourAnswersRowModel(
    CheckYourAnswersMessages.capitalAssetsValue,
    MoneyFormatter.formatHtmlAmount(assetsModel.amount.get),
    controllers.routes.CapitalAssetsController.show().url,
    CheckYourAnswersMessages.capitalAssetsValueHidden
  )

  val outstandingInvoicesRow = CheckYourAnswersRowModel(
    CheckYourAnswersMessages.outstandingInvoices,
    Html(CommonMessages.yes),
    controllers.routes.OutstandingInvoicesController.show().url,
    CheckYourAnswersMessages.outstandingInvoiceHidden
  )

  val newInvoicesRow = CheckYourAnswersRowModel(
    CheckYourAnswersMessages.newInvoices,
    Html(CommonMessages.yes),
    controllers.routes.IssueNewInvoicesController.show().url,
    CheckYourAnswersMessages.newInvoicesHidden
  )

  val deregDateRow = CheckYourAnswersRowModel(
    CheckYourAnswersMessages.deregistrationDate,
    Html(deregistrationDate.date.get.longDate),
    controllers.routes.DeregistrationDateController.show().url,
    CheckYourAnswersMessages.deregistrationDateHidden
  )
}
