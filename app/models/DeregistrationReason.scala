/*
 * Copyright 2024 HM Revenue & Customs
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
      case ExemptOnly.value => ExemptOnly
      case Other.value => Other
  }
}

object Ceased extends DeregistrationReason {
  override val value = "ceased"
  implicit val writes: Writes[Ceased.type] = Writes { _ => Json.obj("deregReason" -> value) }
  val submissionWrites: Writes[Ceased.type] = Writes { _ => JsString(value)}
}

object BelowThreshold extends DeregistrationReason {
  override val value = "belowThreshold"
  implicit val writes: Writes[BelowThreshold.type] = Writes { _ => Json.obj("deregReason" -> value) }
  val submissionWrites: Writes[BelowThreshold.type] = Writes { _ => JsString(value)}
}

object ZeroRated extends DeregistrationReason {
  override val value = "zeroRated"
  implicit val writes: Writes[ZeroRated.type] = Writes { _ => Json.obj("deregReason" -> value) }
  val submissionWrites: Writes[ZeroRated.type] = Writes { _ => JsString(value)}
}

object ExemptOnly extends DeregistrationReason {
  override val value = "exemptOnly"
  implicit val writes: Writes[ExemptOnly.type] = Writes { _ => Json.obj("deregReason" -> value) }
  val submissionWrites: Writes[ExemptOnly.type] = Writes { _ => JsString(value)}
}

object Other extends DeregistrationReason {
  override val value = "other"
  implicit val writes: Writes[Other.type] = Writes { _ => Json.obj("deregReason" -> value) }
  val submissionWrites: Writes[Other.type] = Writes { _ => JsString(value)}
}
