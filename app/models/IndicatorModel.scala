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

import play.api.libs.functional.syntax._
import play.api.libs.json._

case class IndicatorModel(deregistration: Option[PendingDeregModel], emailVerified: Option[Boolean])

object IndicatorModel {

  private val deregPath = __ \ "changeIndicators"
  private val emailPath = __ \\ "emailVerified"

  implicit val reads: Reads[IndicatorModel] = (
    deregPath.readNullable[PendingDeregModel] and
    emailPath.readNullable[Boolean]) (IndicatorModel.apply _)
}

case class PendingDeregModel(dereg: Boolean)

object PendingDeregModel {

  private val path = __ \ "deregister"

  implicit val reads: Reads[PendingDeregModel] = path.read[Boolean].map(PendingDeregModel.apply)

}
