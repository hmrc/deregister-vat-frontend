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

import play.api.libs.json.{Format, Json}

case class DeregistrationDateModel(yesNo: YesNo, date: Option[DateModel]) extends BaseAnswerModel {
  override val getAnswer: Seq[String] = {
    date.fold(Seq("Not Specified"))(dateModel => dateModel.getAnswer)
  }
}

object DeregistrationDateModel {

  def customApply(yesNo: YesNo,
                  day:Option[Int],
                  month:Option[Int],
                  year:Option[Int]): DeregistrationDateModel ={

    (yesNo,day,month,year) match {
      case (Yes,Some(d),Some(m),Some(y)) =>
        DeregistrationDateModel(yesNo, Some(DateModel(d, m, y)))
      case _ =>
        DeregistrationDateModel(yesNo, None)
    }
  }

  def customUnapply(dateModel: DeregistrationDateModel): Option[(YesNo, Option[Int],Option[Int],Option[Int])] = {

    Some(dateModel.date match {
      case Some(date) => (
        dateModel.yesNo,
        Some(date.dateDay),
        Some(date.dateMonth),
        Some(date.dateYear)
      )
      case _ => (dateModel.yesNo,None,None,None)
    })
  }

  implicit val format: Format[DeregistrationDateModel] = Json.format[DeregistrationDateModel]
}
