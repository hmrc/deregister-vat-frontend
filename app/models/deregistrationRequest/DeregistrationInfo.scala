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
                              deregDate: Option[LocalDate],
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

  def customApply(deregReason: Option[DeregistrationReason],
                  ceasedTradingDate: Option[DateModel],
                  taxableTurnover: Option[TaxableTurnoverModel],
                  nextTaxableTurnover: Option[TaxableTurnoverModel],
                  whyTurnoverBelow: Option[WhyTurnoverBelowModel],
                  accountingMethod: Option[VATAccountsModel],
                  optionTax: Option[YesNoAmountModel],
                  stocks: Option[YesNoAmountModel],
                  capitalAssets: Option[YesNoAmountModel],
                  issueNewInvoices: Option[YesNo],
                  outstandingInvoices: Option[YesNo],
                  deregDate: Option[DeregistrationDateModel])(implicit appConfig: AppConfig): Either[ErrorModel,DeregistrationInfo] = {

    deregReason match {
      case Some(deregInfoReason) if deregInfoReason != Other => {
        Right(DeregistrationInfo(
          deregInfoReason,
          deregInfoDate(deregInfoReason,ceasedTradingDate),
          deregLaterDate(deregDate),
          turnoverBelowThreshold(taxableTurnover,nextTaxableTurnover,whyTurnoverBelow,appConfig.deregThreshold),
          retrieveYesNo(optionTax),
          retrieveYesNo(capitalAssets),
          additionalTaxInvoices(issueNewInvoices,outstandingInvoices),
          cashAccountingScheme(accountingMethod),
          retrieveAmount(optionTax),
          retrieveAmount(stocks),
          retrieveAmount(capitalAssets)
        ))
      }
      case _ => Left(ErrorModel(Status.INTERNAL_SERVER_ERROR,"Invalid DeregistrationInfo model"))
    }
  }

  private[deregistrationRequest] def deregInfoDate(deregReason: DeregistrationReason, ceasedTradingDate: Option[DateModel]): Option[LocalDate] =
    (deregReason,ceasedTradingDate) match {
      case (Ceased,Some(dateModel)) => dateModel.date
      case (BelowThreshold,_) => Some(LocalDate.now)
      case _ => None
    }

  private[deregistrationRequest] def deregLaterDate(deregDate: Option[DeregistrationDateModel]): Option[LocalDate] = deregDate match {
    case Some(dateModel) => dateModel.getLocalDate
    case _ => None
  }

  private[deregistrationRequest] def belowThresholdReason(taxableTurnover: Option[TaxableTurnoverModel],
                                                          threshold: Int): Option[BelowThresholdReason] = taxableTurnover match {
    case Some(turnover) if turnover.turnover > threshold => Some(BelowPast12Months)
    case Some(_) => Some(BelowNext12Months)
    case _ => None
  }

  private[deregistrationRequest] def nextTwelveMonthsTurnover(nextTaxableTurnover: Option[TaxableTurnoverModel]): Option[BigDecimal] =
    nextTaxableTurnover match {
      case Some(amount) => Some(amount.turnover)
      case _ => None
    }

  private[deregistrationRequest] def turnoverBelowThreshold(taxableTurnover: Option[TaxableTurnoverModel],
                             nextTaxableTurnover: Option[TaxableTurnoverModel],
                             whyTurnoverBelow: Option[WhyTurnoverBelowModel],
                             threshold: Int): Option[TurnoverBelowThreshold] = {
    (belowThresholdReason(taxableTurnover,threshold), nextTwelveMonthsTurnover(nextTaxableTurnover)) match {
      case (Some(belowThresholdAnswer), Some(taxableTurnoverAnswer)) =>
        Some(TurnoverBelowThreshold(belowThresholdAnswer,taxableTurnoverAnswer,whyTurnoverBelow))
      case _ => None
    }
  }

  private[deregistrationRequest] def retrieveYesNo: Option[YesNoAmountModel] => Boolean = {
    case Some(model) => model.yesNo == Yes
    case _ => false
  }

  private[deregistrationRequest] def additionalTaxInvoices(issueNewInvoices: Option[YesNo], outstandingInvoices: Option[YesNo]): Boolean =
    (issueNewInvoices,outstandingInvoices) match {
      case (Some(Yes),_) => true
      case (_,Some(Yes)) => true
      case _ => false
    }

  private[deregistrationRequest] def cashAccountingScheme(accountingMethod: Option[VATAccountsModel]): Boolean = accountingMethod match {
    case Some(CashAccounting) => true
    case _ => false
  }

  private[deregistrationRequest] def retrieveAmount: Option[YesNoAmountModel] => Option[BigDecimal] = {
    case Some(model) => model.amount
    case _ => None
  }

  implicit val writes: Writes[DeregistrationInfo] = (
    (__ \ "deregReason").write[DeregistrationReason](DeregistrationReason.submissionWrites) and
      (__ \ "deregDate").writeNullable[LocalDate] and
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
