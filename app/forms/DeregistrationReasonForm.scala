/*
 * Copyright 2021 HM Revenue & Customs
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

package forms

import models._
import play.api.data.{Form, FormError}
import play.api.data.Forms._
import play.api.data.format.Formatter

object DeregistrationReasonForm {

  val reason: String = "reason"
  val ceased: String = "stoppedTrading"
  val belowThreshold: String = "turnoverBelowThreshold"
  val zeroRated: String = "zeroRated"
  val exemptOnly: String = "exemptOnly"
  val other: String = "other"

  val error: String = "deregistrationReason.reason.mandatoryRadioOption"

  private val formatter: Formatter[DeregistrationReason] = new Formatter[DeregistrationReason] {
    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], DeregistrationReason] = {
      data.get(key) match {
        case Some(`ceased`) => Right(Ceased)
        case Some(`belowThreshold`) => Right(BelowThreshold)
        case Some(`zeroRated`) => Right(ZeroRated)
        case Some(`exemptOnly`) => Right(ExemptOnly)
        case Some(`other`) => Right(Other)
        case _ => Left(Seq(FormError(key, error)))
      }
    }

    override def unbind(key: String, value: DeregistrationReason): Map[String, String] = {
      val stringValue = value match {
        case Ceased => ceased
        case BelowThreshold => belowThreshold
        case ZeroRated => zeroRated
        case ExemptOnly => exemptOnly
        case Other => other
      }
      Map(key -> stringValue)
    }
  }

  val deregistrationReasonForm: Form[DeregistrationReason] = Form(
    single(
      reason -> of(formatter)
    )
  )
}
