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

sealed trait DeregistrationReason {
  val value: String
}

object DeregistrationReason {

  val id = "deregReason"

  implicit val writes: Writes[DeregistrationReason] = Writes {
    reason => Json.obj(id -> reason.value)
  }

  val submissionWrites: Writes[DeregistrationReason] = Writes {
    reason => JsString(reason.value)
  }

  implicit val reads: Reads[DeregistrationReason] = (__ \ id).read[String].map {
      case Ceased.value => Ceased
      case BelowThreshold.value => BelowThreshold
      case ZeroRated.value => ZeroRated
      case Other.value => Other
  }
}

object Ceased extends DeregistrationReason {
  override val value = "ceased"
}

object BelowThreshold extends DeregistrationReason {
  override val value = "belowThreshold"
}

object ZeroRated extends DeregistrationReason {
  override val value = "zeroRated"
}

object Other extends DeregistrationReason {
  override val value = "other"
}