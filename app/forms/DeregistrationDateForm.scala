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

package forms

import forms.YesNoForm._
import forms.DateForm._
import models._
import play.api.data.{Form, FormError}
import play.api.data.Forms._
import play.api.data.format.Formatter
import play.api.data.validation.{Constraint, Valid}

import scala.util.{Failure, Success, Try}

object DeregistrationDateForm {

  val formatter: Formatter[Option[Int]] = new Formatter[Option[Int]] {
    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], Option[Int]] = {
      (data.get(YesNoForm.yesNo), data.get(key)) match {
        case (Some(YesNoForm.yes), Some(value)) if value.trim == "" => Left(Seq(invalidDateError(key)))
        case (Some(YesNoForm.yes), Some(stringValue)) => Try(stringValue.toInt) match {
          case Success(intValue) =>
            if(isValidDate(key, intValue)) {Right(Some(intValue))}
            else {Left(Seq(invalidDateError(key)))}
          case Failure(_) => Left(Seq(FormError(key, "error.date.invalidCharacters")))
        }
        case _ => Right(None)
      }
    }

    override def unbind(key: String, value: Option[Int]): Map[String, String] = {
      val stringValue  = value match {
        case Some(intValue) => intValue.toString
        case _ => ""
      }
      Map(key -> stringValue)
    }
  }

//  def checkValidDateIfYes(errMsg: String): Constraint[DateModel] = Constraint[DeregistrationDateModel]("checkValidDateIfYes") {
//    yesNo match {
//      case YesNoForm.yes => isValidDate(errMsg)
//      case _ => Valid
//    }
//  }

  val deregistrationDateForm: Form[DeregistrationDateModel] = Form(
    mapping(
      yesNo -> of(YesNoForm.formatter),
      day -> of(formatter),
      month -> of(formatter),
      year -> of(formatter)
    )(DeregistrationDateModel.customApply)(DeregistrationDateModel.customUnapply)
      .verifying("error.date.invalid", _.checkValidDateIfYes)
  )
}




