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

package models.deregistrationRequest

import java.time.LocalDate

import config.AppConfig
import models._
import play.api.http.Status
import play.api.libs.functional.syntax._
import play.api.libs.json._

case class DeregistrationInfo(deregReason: DeregistrationReason,
                              deregDate: LocalDate,
                              deregLaterDate: Option[LocalDate],
                              turnoverBelowThreshold: Option[TurnoverBelowThreshold],
                              optionToTax: Boolean,
                              intendSellCapitalAssets: Boolean,
                              additionalTaxInvoices: Boolean,
                              cashAccountingScheme: Boolean,
                              optionToTaxValue: Option[BigDecimal],
                              stocksValue: Option[BigDecimal],
                              capitalAssetsValue: Option[BigDecimal])

object DeregistrationInfo {

  def customApply(deregReason: DeregistrationReason,
                  ceasedTradingDate: Option[DateModel],
                  taxableTurnover: Option[TaxableTurnoverModel],
                  nextTaxableTurnover: Option[TaxableTurnoverModel],
                  whyTurnoverBelow: Option[WhyTurnoverBelowModel],
                  accountingMethod: VATAccountsModel,
                  optionTax: YesNoAmountModel,
                  stocks: YesNoAmountModel,
                  capitalAssets: YesNoAmountModel,
                  issueNewInvoices: YesNo,
                  outstandingInvoices: Option[YesNo],
                  deregDate: Option[DeregistrationDateModel])(implicit appConfig: AppConfig): DeregistrationInfo = {

        DeregistrationInfo(
          deregReason,
          deregInfoDate(ceasedTradingDate),
          deregLaterDate(deregDate),
          turnoverBelowThreshold(taxableTurnover, nextTaxableTurnover, whyTurnoverBelow),
          optionTax.yesNo.value,
          capitalAssets.yesNo.value,
          taxInvoices(issueNewInvoices, outstandingInvoices),
          isCashAccounting(accountingMethod),
          optionTax.amount,
          stocks.amount,
          capitalAssets.amount
        )
  }

  private[deregistrationRequest] val deregInfoDate: Option[DateModel] => LocalDate = _.fold(LocalDate.now)(_.date.fold(LocalDate.now)(x => x))

  private[deregistrationRequest] val deregLaterDate: Option[DeregistrationDateModel] => Option[LocalDate] = _.flatMap(_.getLocalDate)

  private[deregistrationRequest] def belowThresholdReason(taxableTurnover: TaxableTurnoverModel)
                                                         (implicit appConfig: AppConfig): BelowThresholdReason =
    if (taxableTurnover.turnover > appConfig.deregThreshold) BelowNext12Months else BelowPast12Months

  private[deregistrationRequest] val nextTwelveMonthsTurnover: Option[TaxableTurnoverModel] => Option[BigDecimal] = _.map(_.turnover)

  private[deregistrationRequest] def turnoverBelowThreshold(taxableTurnover: Option[TaxableTurnoverModel],
                             nextTaxableTurnover: Option[TaxableTurnoverModel],
                             whyTurnoverBelow: Option[WhyTurnoverBelowModel])(implicit appConfig: AppConfig): Option[TurnoverBelowThreshold] =
    taxableTurnover.flatMap { turnover =>
      nextTwelveMonthsTurnover(nextTaxableTurnover).map { nextTaxableTurnover =>
        TurnoverBelowThreshold(belowThresholdReason(turnover), nextTaxableTurnover, whyTurnoverBelow)
      }
    }

  private[deregistrationRequest] val taxInvoices: (YesNo, Option[YesNo]) => Boolean =
    (issueNewInvoices, outstandingInvoices) => issueNewInvoices.value || outstandingInvoices.fold(false)(_.value)

  private[deregistrationRequest] val isCashAccounting: VATAccountsModel => Boolean = _ == CashAccounting

  implicit val writes: Writes[DeregistrationInfo] = (
    (__ \ "deregReason").write[DeregistrationReason](DeregistrationReason.submissionWrites) and
      (__ \ "deregDate").write[LocalDate] and
      (__ \ "deregLaterDate").writeNullable[LocalDate] and
      (__ \ "turnoverBelowThreshold").writeNullable[TurnoverBelowThreshold] and
      (__ \ "optionToTax").write[Boolean] and
      (__ \ "intendSellCapitalAssets").write[Boolean] and
      (__ \ "additionalTaxInvoices").write[Boolean] and
      (__ \ "cashAccountingScheme").write[Boolean] and
      (__ \ "optionToTaxValue").writeNullable[BigDecimal] and
      (__ \ "stocksValue").writeNullable[BigDecimal] and
      (__ \ "capitalAssetsValue").writeNullable[BigDecimal]
    )(unlift(DeregistrationInfo.unapply))
}
