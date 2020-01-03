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

package models.deregistrationRequest

import java.time.LocalDate

import config.AppConfig
import models._
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
                              capitalAssetsValue: Option[BigDecimal],
                              stocksValue: Option[BigDecimal],
                              transactorOrCapacitorEmail: Option[String])

object DeregistrationInfo {

  def customApply(deregReason: DeregistrationReason,
                  ceasedTradingDate: Option[DateModel],
                  taxableTurnover: Option[YesNo],
                  nextTaxableTurnover: Option[NumberInputModel],
                  whyTurnoverBelow: Option[WhyTurnoverBelowModel],
                  accountingMethod: VATAccountsModel,
                  optionTax: YesNoAmountModel,
                  capitalAssets: YesNoAmountModel,
                  stocks: YesNoAmountModel,
                  issueNewInvoices: YesNo,
                  outstandingInvoices: Option[YesNo],
                  deregDate: Option[DeregistrationDateModel],
                  transactorOrCapacitorEmail: Option[String])(implicit appConfig: AppConfig): DeregistrationInfo = {

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
          capitalAssets.amount,
          stocks.amount,
          transactorOrCapacitorEmail
        )
  }

  private[deregistrationRequest] val deregInfoDate: Option[DateModel] => LocalDate = _.fold(LocalDate.now)(_.date.fold(LocalDate.now)(x => x))

  private[deregistrationRequest] val deregLaterDate: Option[DeregistrationDateModel] => Option[LocalDate] = _.flatMap(_.getLocalDate)

  private[deregistrationRequest] def taxableTurnoverBelowReason(taxableTurnover: YesNo)(implicit appConfig: AppConfig): BelowThresholdReason =
    if (taxableTurnover.value) BelowNext12Months else BelowPast12Months

  private[deregistrationRequest] val nextTwelveMonthsTurnover: Option[NumberInputModel] => Option[BigDecimal] = _.map(_.value)

  private[deregistrationRequest] def turnoverBelowThreshold(taxableTurnover: Option[YesNo],
                                                            nextTaxableTurnover: Option[NumberInputModel],
                                                            whyTurnoverBelow: Option[WhyTurnoverBelowModel])
                                                           (implicit appConfig: AppConfig): Option[TurnoverBelowThreshold] = {
    taxableTurnover.flatMap { turnover =>
      nextTwelveMonthsTurnover(nextTaxableTurnover).map { nextTaxableTurnover =>
        TurnoverBelowThreshold(taxableTurnoverBelowReason(turnover), nextTaxableTurnover, whyTurnoverBelow)
      }
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
      (__ \ "capitalAssetsValue").writeNullable[BigDecimal] and
      (__ \ "stocksValue").writeNullable[BigDecimal] and
      (__ \ "transactorOrCapacitorEmail").writeNullable[String]
    )(unlift(DeregistrationInfo.unapply))
}
