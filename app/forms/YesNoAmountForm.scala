/*
 * Copyright 2021 HM Revenue & Customs
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

import common.Constants
import forms.YesNoForm._
import forms.utils.FormValidation
import models.YesNoAmountModel
import play.api.data.Forms._
import play.api.data.format.Formatter
import play.api.data.{Form, FormError}

object YesNoAmountForm extends FormValidation {

  val amount = "amount"

  private def validateAmount(key: String): BigDecimal => Either[Seq[FormError], Option[BigDecimal]] = {
    case x if x < 0 => Left(Seq(FormError(key, "common.error.negative")))
    case x if x >= Constants.maxAmount => Left(Seq(FormError(key, "common.error.tooManyDigitsBeforeDecimal")))
    case x if hasMoreThanTwoDecimals(x.toString) => Left(Seq(FormError(key, "common.error.tooManyDecimals")))
    case x => Right(Some(x))
  }

   def formatter(emptyAmount: String): Formatter[Option[BigDecimal]] = new Formatter[Option[BigDecimal]] {
    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], Option[BigDecimal]] = {
      (data.get(yesNo), data.get(key)) match {
        case (Some(a), Some(x)) if a == yes && (x.trim == "") => Left(Seq(FormError(key, emptyAmount)))
        case (Some(a), Some(x)) if a == yes && (!isNumeric(x)) => Left(Seq(FormError(key, "common.error.mandatoryAmount")))
        case (Some(a), Some(x)) if a == yes => validateAmount(key)(BigDecimal(x.stripPrefix("£")))
        case _ => Right(None)
      }
    }

    override def unbind(key: String, value: Option[BigDecimal]): Map[String, String] = {
      val stringValue = value match {
        case Some(x) => x.toString
        case _ => ""
      }
      Map(key -> stringValue)
    }
  }

  def yesNoAmountForm(yesNoError: String, emptyAmount: String): Form[YesNoAmountModel] = Form(
    mapping(
      yesNo -> of(YesNoForm.formatter(yesNoError)),
      amount -> of(formatter(emptyAmount))
    )(YesNoAmountModel.apply)(YesNoAmountModel.unapply)
  )
}
