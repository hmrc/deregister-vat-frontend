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

package assets.constants

import models.CustomerDetails
import play.api.libs.json.{JsObject, Json}

object CustomerDetailsTestConstants {

  val orgName = "Ancient Antiques Ltd"
  val tradingName = "Dusty Relics"
  val firstName = "Fred"
  val lastName = "Flintstone"
  val partyTypeValue = "50"
  val vatGroupPartyTypeValue = "Z2"

  val customerDetailsJsonMax: JsObject = Json.obj(
    "organisationName" -> orgName,
    "firstName" -> firstName,
    "lastName" -> lastName,
    "tradingName" -> tradingName,
    "partyType" -> partyTypeValue,
    "ppob" -> Json.obj("contactDetails" -> Some(Json.obj("emailVerified" -> Some(true))))
  )


  val customerDetailsJsonMin: JsObject = Json.obj()

  val customerDetailsMax: CustomerDetails = CustomerDetails(
    Some(firstName),
    Some(lastName),
    Some(orgName),
    Some(tradingName),
    Some(partyTypeValue),
    Some(true),
    pendingDereg = false,
    alreadyDeregistered = false
  )

  val customerDetailsMin: CustomerDetails = CustomerDetails(
    None,
    None,
    None,
    None,
    None,
    None,
    pendingDereg = false,
    alreadyDeregistered = false
  )

  val customerDetailsUnverifiedEmail: CustomerDetails = CustomerDetails(
    Some(firstName),
    Some(lastName),
    Some(orgName),
    Some(tradingName),
    Some(partyTypeValue),
    Some(false),
    pendingDereg = false,
    alreadyDeregistered = false
  )

  val customerDetailsPendingDeregJson: JsObject = customerDetailsJsonMax ++ Json.obj(
    "changeIndicators" -> Json.obj(
      "deregister" -> true
    ))

  val customerDetailsAlreadyDeregisteredJson: JsObject = customerDetailsJsonMax ++ Json.obj(
    "deregistration" -> Json.obj(
      "effectDateOfCancellation" -> "2018-01-01"
    )
  )

  val customerDetailsPendingDereg: CustomerDetails = customerDetailsMax.copy(pendingDereg = true)
  val customerDetailsAlreadyDeregistered: CustomerDetails = customerDetailsMax.copy(alreadyDeregistered = true)
  val customerDetailsVatGroup: CustomerDetails = customerDetailsMax.copy(partyType = Some(vatGroupPartyTypeValue))
}
