package models

import play.api.libs.json.{Json, OFormat}

case class BusinessDebt(businessInDebt:String)

object BusinessDebt{
  implicit val format: OFormat[BusinessDebt] = Json.format[BusinessDebt]
}