/*
 * Copyright 2023 HM Revenue & Customs
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

package forms

import models._
import play.api.data.Forms._
import play.api.data.format.Formatter
import play.api.data.{Form, FormError}

object VATAccountsForm {

  private val formatter: Formatter[VATAccountsModel] = new Formatter[VATAccountsModel] {

    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], VATAccountsModel] = {
      data.get(key) match {
        case Some(StandardAccounting.value) => Right(StandardAccounting)
        case Some(CashAccounting.value) => Right(CashAccounting)
        case _ => Left(Seq(FormError(key, "vatAccounts.showMeAnExample.mandatorySelection")))
      }
    }

    override def unbind(key: String, value: VATAccountsModel): Map[String, String] = {
      val stringValue = value match {
        case StandardAccounting => StandardAccounting.value
        case CashAccounting => CashAccounting.value
      }
      Map(key -> stringValue)
    }
  }
  val vatAccountsForm: Form[VATAccountsModel] = Form(
    single(
      VATAccountsModel.id -> of(formatter)
    )
  )

}
