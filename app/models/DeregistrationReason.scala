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

import play.api.libs.json._

sealed trait DeregistrationReason {
  val value: String
}

object DeregistrationReason {

  val id = "deregistrationReason"

  implicit val writes: Writes[DeregistrationReason] = Writes {
    reason => Json.obj(id -> reason.value)
  }

  implicit val reads: Reads[DeregistrationReason] = for {
    status <- (__ \ id).read[String].map {
      case Ceased.value => Ceased
      case BelowThreshold.value => BelowThreshold
      case Other.value => Other
    }
  } yield status

  implicit val format: Format[DeregistrationReason] = Format(reads, writes)
}

object Ceased extends DeregistrationReason {
  override val value = "ceased"
}

object BelowThreshold extends DeregistrationReason {
  override val value = "belowThreshold"
}

object Other extends DeregistrationReason {
  override val value = "other"
}
