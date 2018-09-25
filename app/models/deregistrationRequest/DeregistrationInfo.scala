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
import play.api.libs.json.{Json, Writes}

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

  implicit val writes: Writes[DeregistrationInfo] = Json.writes[DeregistrationInfo]

}
