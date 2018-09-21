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

import assets.messages.{CheckYourAnswersMessages, CommonMessages, WhyTurnoverBelowMessages}
import models._
import play.twirl.api.Html
import utils.MoneyFormatter


object CheckYourAnswersTestConstants {

  val dateModel = DateModel(1,1,2018)
  val taxableTurnoverAbove = TaxableTurnoverModel(BigDecimal(90000))
  val taxableTurnoverBelow = TaxableTurnoverModel(BigDecimal(200))
  val whyTurnoverBelowAll = WhyTurnoverBelowModel(true,true,true,true,true,true,true)
  val whyTurnoverBelowMin = WhyTurnoverBelowModel(true,false,false,false,false,false,false)
  val vatAccountsModel = VATAccountsModel("standard")
  val yesNoAmountYes = YesNoAmountModel(Yes,Some(BigDecimal(1000)))
  val yesNoAmountNo = YesNoAmountModel(No,None)
  val deregistrationDate = DeregistrationDateModel(Yes,Some(DateModel(1,1,2018)))

  val deregReasonRow = CheckYourAnswersRowModel(
    CheckYourAnswersMessages.reason,
    Html(CheckYourAnswersMessages.reasonCeased),
    controllers.routes.DeregistrationReasonController.show(false).url
  )

  val ceasedTradingRow = CheckYourAnswersRowModel(
    CheckYourAnswersMessages.ceasedTrading,
    Html(dateModel.longDate),
    controllers.routes.CeasedTradingDateController.show().url
  )

  val taxableTurnoverRow = CheckYourAnswersRowModel(
    CheckYourAnswersMessages.taxableTurnover,
    MoneyFormatter.formatHtmlAmount(taxableTurnoverAbove.turnover),
    controllers.routes.TaxableTurnoverController.show().url
  )

  val nextTaxableTurnoverRow = CheckYourAnswersRowModel(
    CheckYourAnswersMessages.nextTaxableTurnover,
    MoneyFormatter.formatHtmlAmount(taxableTurnoverBelow.turnover),
    controllers.routes.NextTaxableTurnoverController.show().url
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
    controllers.routes.WhyTurnoverBelowController.show().url
  )

  val whyBelowRowMin = CheckYourAnswersRowModel(
    CheckYourAnswersMessages.whyBelow,
    Html(WhyTurnoverBelowMessages.reason1),
    controllers.routes.WhyTurnoverBelowController.show().url
  )

  val vatAccountsRow = CheckYourAnswersRowModel(
    CheckYourAnswersMessages.vatAccounts,
    Html(CheckYourAnswersMessages.standard),
    controllers.routes.VATAccountsController.show().url
  )

  val optionTaxRowYes = CheckYourAnswersRowModel(
    CheckYourAnswersMessages.optionTax,
    Html(CommonMessages.yes),
    controllers.routes.OptionTaxController.show().url
  )

  val optionTaxRowNo = CheckYourAnswersRowModel(
    CheckYourAnswersMessages.optionTax,
    Html(CommonMessages.no),
    controllers.routes.OptionTaxController.show().url
  )

  val optionTaxValueRow = CheckYourAnswersRowModel(
    CheckYourAnswersMessages.optionTaxValue,
    MoneyFormatter.formatHtmlAmount(yesNoAmountYes.amount.get),
    controllers.routes.OptionTaxController.show().url
  )

  val stocksRowYes = CheckYourAnswersRowModel(
    CheckYourAnswersMessages.stocks,
    Html(CommonMessages.yes),
    controllers.routes.OptionStocksToSellController.show().url
  )

  val stocksRowNo = CheckYourAnswersRowModel(
    CheckYourAnswersMessages.stocks,
    Html(CommonMessages.no),
    controllers.routes.OptionStocksToSellController.show().url
  )

  val stocksValueRow = CheckYourAnswersRowModel(
    CheckYourAnswersMessages.stocksValue,
    MoneyFormatter.formatHtmlAmount(yesNoAmountYes.amount.get),
    controllers.routes.OptionStocksToSellController.show().url
  )

  val captialAssetsRowYes = CheckYourAnswersRowModel(
    CheckYourAnswersMessages.capitalAssets,
    Html(CommonMessages.yes),
    controllers.routes.CapitalAssetsController.show().url
  )

  val captialAssetsRowNo = CheckYourAnswersRowModel(
    CheckYourAnswersMessages.capitalAssets,
    Html(CommonMessages.no),
    controllers.routes.CapitalAssetsController.show().url
  )

  val captialAssetsValueRow = CheckYourAnswersRowModel(
    CheckYourAnswersMessages.capitalAssetsValue,
    MoneyFormatter.formatHtmlAmount(yesNoAmountYes.amount.get),
    controllers.routes.CapitalAssetsController.show().url
  )

  val outstandingInvoicesRow = CheckYourAnswersRowModel(
    CheckYourAnswersMessages.outstandingInvoices,
    Html(CommonMessages.yes),
    controllers.routes.OutstandingInvoicesController.show().url
  )

  val owesMoneyRow = CheckYourAnswersRowModel(
    CheckYourAnswersMessages.owesMoney,
    Html(CommonMessages.yes),
    controllers.routes.OptionOwesMoneyController.show().url
  )

  val deregDateRow = CheckYourAnswersRowModel(
    CheckYourAnswersMessages.deregistrationDate,
    Html(deregistrationDate.date.get.longDate),
    controllers.routes.DeregistrationDateController.show().url
  )
}