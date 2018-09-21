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

package models

import play.api.libs.json._

trait VATAccountsModel {
  val value: String
}

object VATAccountsModel {

  val id: String = "accountingMethod"

  implicit val writes: Writes[VATAccountsModel] = Writes {
    accountType => Json.obj(id -> accountType.value)
  }

  implicit val reads: Reads[VATAccountsModel] = for {
    accountType <- (__ \ id).read[String].map{
      case StandardAccounting.value => StandardAccounting
      case CashAccounting.value => CashAccounting
    }
  } yield accountType

  implicit val format: Format[VATAccountsModel] = Format(reads, writes)
}

object StandardAccounting extends VATAccountsModel {
  override val value = "standard"
}

object CashAccounting extends VATAccountsModel {
  override val value = "cash"
}
