/*
 * Copyright 2019 HM Revenue & Customs
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

case class ChangeIndicatorModel(deregistration: Option[PendingDeregModel])

object ChangeIndicatorModel {

  private val deregPath = __ \ "changeIndicators"

  implicit val reads: Reads[ChangeIndicatorModel] = deregPath.readNullable[PendingDeregModel].orElse(Reads.pure(None)).map(ChangeIndicatorModel.apply)
}

case class PendingDeregModel(dereg: Boolean)

object PendingDeregModel {

  private val path = __ \ "deregister"

  implicit val reads: Reads[PendingDeregModel] = path.read[Boolean].map(PendingDeregModel.apply)

}
