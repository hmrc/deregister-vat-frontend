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

import models.DeregistrationReason
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

  implicit val reads: Reads[DeregistrationInfo] = (
    __.read[DeregistrationReason] and
      (__ \ "deregDate").readNullable[LocalDate] and
      (__ \ "deregLaterDate").readNullable[LocalDate] and
      (__ \ "turnoverBelowThreshold").readNullable[TurnoverBelowThreshold] and
      (__ \ "optionToTax").read[Boolean] and
      (__ \ "intendSellCapitalAssets").read[Boolean] and
      (__ \ "additionalTaxInvoices").read[Boolean] and
      (__ \ "cashAccountingScheme").read[Boolean] and
      (__ \ "optionToTaxValue").readNullable[BigDecimal] and
      (__ \ "stocksValue").readNullable[BigDecimal] and
      (__ \ "capitalAssetsValue").readNullable[BigDecimal]
    )(DeregistrationInfo.apply _)

}
