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

package models

import play.api.libs.json._

case class CustomerDetails(firstName: Option[String],
                           lastName: Option[String],
                           organisationName: Option[String],
                           tradingName: Option[String],
                           partyType: Option[String],
                           emailVerified: Option[Boolean],
                           pendingDereg: Boolean,
                           alreadyDeregistered: Boolean) {

  val isOrg: Boolean = organisationName.isDefined
  val isInd: Boolean = firstName.isDefined || lastName.isDefined
  val userName: Option[String] = {
    val name = s"${firstName.getOrElse("")} ${lastName.getOrElse("")}".trim
    if (name.isEmpty) None else Some(name)
  }
  val businessName: Option[String] = if(isOrg) organisationName else userName
  val clientName: Option[String] = if(tradingName.isDefined) tradingName else businessName
}

object CustomerDetails {

  private val firstNamePath = __ \ "firstName"
  private val lastNamePath = __ \ "lastName"
  private val organisationNamePath = __ \ "organisationName"
  private val tradingNamePath = __ \ "tradingName"
  private val partyTypePath = __ \ "partyType"
  private val emailPath = __ \\ "emailVerified"
  private val pendingDeregPath = __ \ "changeIndicators" \ "deregister"
  private val effectDateOfCancellationPath = __ \ "deregistration" \ "effectDateOfCancellation"

  implicit val reads: Reads[CustomerDetails] = for {
    firstName <- firstNamePath.readNullable[String]
    lastName <- lastNamePath.readNullable[String]
    orgName <- organisationNamePath.readNullable[String]
    tradingName <- tradingNamePath.readNullable[String]
    partyType <- partyTypePath.readNullable[String]
    emailVerified <- emailPath.readNullable[Boolean]
    deregChangeIndicator <- pendingDeregPath.readNullable[Boolean].orElse(Reads.pure(None))
    deregDate <- effectDateOfCancellationPath.readNullable[String].orElse(Reads.pure(None))
  } yield {

    val pendingDereg = deregChangeIndicator.getOrElse(false)
    val alreadyDeregistered = deregDate.isDefined

    CustomerDetails(
      firstName, lastName, orgName, tradingName, partyType, emailVerified, pendingDereg, alreadyDeregistered
    )
  }
}
