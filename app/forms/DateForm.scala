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

package forms

import forms.utils.FormValidation
import models.DateModel
import play.api.data.Forms._
import play.api.data.format.Formatter
import play.api.data.{Form, FormError}

import scala.util.{Failure, Success, Try}


object DateForm extends FormValidation {

  val day = "dateDay"
  val month = "dateMonth"
  val year = "dateYear"

  def invalidDateError(key: String): FormError =
      key match {
        case `day` => FormError(key, "error.date.day")
        case `month` => FormError(key, "error.date.month")
        case `year` => FormError(key, "error.date.year")
  }

  def isValidDate(key: String, value: Int): Boolean = key match {
    case `day` => 1 to 31 contains value
    case `month` => 1 to 12 contains value
    case `year` => value.toString.length == 4
  }

  val formatter: Formatter[Int] = new Formatter[Int] {
    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], Int] = {
      data.get(key) match {
        case Some(value) if value.trim == "" =>
          Left(Seq(invalidDateError(key)))
        case Some(stringValue) => Try(stringValue.toInt) match {
          case Success(intValue) =>
            if(isValidDate(key, intValue)) {
              Right(intValue)
            }
            else {
              Left(Seq(invalidDateError(key)))
            }
          case Failure(_) =>
            Left(Seq(FormError(key, "error.date.invalidCharacters")))
        }
        case _ =>
          Left(Seq(invalidDateError(key)))
      }
    }

    override def unbind(key: String, value: Int): Map[String, String] = {
      Map(key -> value.toString)
    }
  }

  val dateForm: Form[DateModel] = Form(
    mapping(
      day -> of(formatter),
      month -> of(formatter),
      year -> of(formatter)
    )(DateModel.apply)(DateModel.unapply)
      .verifying(isValidDateConstraint("ceasedTrading.error.date.noEntry"))
  )
}
