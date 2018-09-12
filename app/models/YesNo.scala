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

sealed trait YesNo extends BaseAnswerModel {
  val value: Boolean
}

object YesNo {

  val id = "isYes"

  implicit val writes: Writes[YesNo] = Writes {
    isYes => Json.obj(id -> isYes.value)
  }

  implicit val reads: Reads[YesNo] = for {
    status <- (__ \ id).read[Boolean].map {
      case true => Yes
      case _ => No
    }
  } yield status

  implicit val format: Format[YesNo] = Format(reads, writes)
}

object Yes extends YesNo {
  override val getAnswer: Seq[String] = Seq("Yes")
  override val value: Boolean = true
}

object No extends YesNo {
  override val getAnswer: Seq[String] = Seq("No")
  override val value: Boolean = false
}
