/*
 * Copyright 2017 HM Revenue & Customs
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

import common.EnrolmentKeys
import uk.gov.hmrc.auth.core.{Enrolment, EnrolmentIdentifier, Enrolments, InternalError}

case class User(vrn: String, active: Boolean = true)

object User {
  def apply(enrolments: Enrolments): User = {
    val vatEnrolments = enrolments.enrolments.filter(enrolment => enrolment.key == EnrolmentKeys.VatEnrolmentId)

    if (vatEnrolments.isEmpty) {
      throw InternalError("VAT enrolment missing")
    }
    else {
      vatEnrolments.collectFirst {
        case Enrolment(EnrolmentKeys.VatEnrolmentId, EnrolmentIdentifier(_, vrn) :: _, status, _) => User(vrn, status == "Activated")
      }.getOrElse(throw InternalError("VRN missing"))
    }
  }
}
