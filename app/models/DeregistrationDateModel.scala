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


case class DeregistrationDateModel(yesNo: YesNo, date: Option[CeasedTradingDateModel]) {

  val checkDate: Boolean = {
    yesNo match {
      case Yes => date.isDefined
      case No => date.isEmpty
    }
  }
}

object DeregistrationDateModel {

  def customApply(yesNo: YesNo,
                   day:Option[Int],
                   month:Option[Int],
                   year:Option[Int]): DeregistrationDateModel ={

    if(day.isDefined && month.isDefined && year.isDefined) {
      DeregistrationDateModel(yesNo, Some(CeasedTradingDateModel(day.get, month.get, year.get)))
    } else {
      DeregistrationDateModel(yesNo, None)
    }
  }

  def customUnapply(arg: DeregistrationDateModel): Option[(YesNo, Option[Int],Option[Int],Option[Int])] = {
    if(arg.date.isDefined) {
      Some((
        arg.yesNo,
        Some(arg.date.get.ceasedTradingDateDay),
        Some(arg.date.get.ceasedTradingDateMonth),
        Some(arg.date.get.ceasedTradingDateYear)
      ))
    } else {
      Some(arg.yesNo,None,None,None)
    }
  }


}
