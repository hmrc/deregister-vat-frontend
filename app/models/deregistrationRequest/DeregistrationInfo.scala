/*
 * Copyright 2022 HM Revenue & Customs
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

import models.{ZeroRated => ZeroRatedDeregReason, _}
import play.api.libs.functional.syntax._
import play.api.libs.json._

import java.time.LocalDate

case class DeregistrationInfo(deregReason: DeregistrationReason,
                              deregDate: LocalDate,
                              deregLaterDate: Option[LocalDate],
                              turnoverBelowThreshold: Option[TurnoverBelowThreshold],
                              zeroRated: Option[ZeroRated],
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
                  deregDate: Option[DateModel],
                  purchasesExceedSupplies: Option[YesNo],
                  sicCode: Option[String],
                  zeroRatedSuppliesValue: Option[NumberInputModel],
                  transactorOrCapacitorEmail: Option[String]): DeregistrationInfo = {

        DeregistrationInfo(
          deregReason,
          deregInfoDate(ceasedTradingDate),
          deregLaterDate(deregDate),
          turnoverBelowThreshold(deregReason, taxableTurnover, nextTaxableTurnover, whyTurnoverBelow),
          zeroRated(deregReason, purchasesExceedSupplies, sicCode, zeroRatedSuppliesValue, nextTaxableTurnover),
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

  private[deregistrationRequest] val deregLaterDate: Option[DateModel] => Option[LocalDate] = _.flatMap(_.date)

  private[deregistrationRequest] def taxableTurnoverBelowReason(taxableTurnover: YesNo): BelowThresholdReason =
    if (taxableTurnover.value) BelowNext12Months else BelowPast12Months

  private[deregistrationRequest] val nextTwelveMonthsTurnover: Option[NumberInputModel] => Option[BigDecimal] = _.map(_.value)

  private[deregistrationRequest] def turnoverBelowThreshold(deregistrationReason: DeregistrationReason,
                                                            taxableTurnover: Option[YesNo],
                                                            nextTaxableTurnover: Option[NumberInputModel],
                                                            whyTurnoverBelow: Option[WhyTurnoverBelowModel]):
                                                            Option[TurnoverBelowThreshold] = {
    if(deregistrationReason equals BelowThreshold) {
      taxableTurnover.flatMap { turnover =>
        nextTwelveMonthsTurnover(nextTaxableTurnover).map { nextTaxableTurnover =>
          TurnoverBelowThreshold(taxableTurnoverBelowReason(turnover), nextTaxableTurnover, whyTurnoverBelow)
        }
      }
    } else {
      None
    }
  }

  private[deregistrationRequest] def zeroRated(deregistrationReason: DeregistrationReason,
                                               purchasesExceedSupplies: Option[YesNo],
                                               sicCode: Option[String],
                                               zeroRatedSuppliesValue: Option[NumberInputModel],
                                               nextTaxableTurnover: Option[NumberInputModel]): Option[ZeroRated] = {

    if(deregistrationReason equals ZeroRatedDeregReason) {
      (purchasesExceedSupplies, zeroRatedSuppliesValue, nextTaxableTurnover) match {
        case (Some(purchasesExceedSuppliesAnswer), Some(zeroRatedSuppliesAnswer), Some(nextTaxableTurnoverAnswer)) =>
          Some(ZeroRated(purchasesExceedSuppliesAnswer.value, sicCode, zeroRatedSuppliesAnswer.value, nextTaxableTurnoverAnswer.value))
        case _ => None
      }
    } else {
      None
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
      (__ \ "zeroRatedExmpApplication").writeNullable[ZeroRated] and
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
