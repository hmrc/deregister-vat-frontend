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

package connectors.httpParsers

import models.{ErrorModel, VatSubscriptionResponse, VatSubscriptionSuccess}
import play.api.http.Status
import uk.gov.hmrc.http.{HttpReads, HttpResponse}
import utils.LoggingUtil


object VatSubscriptionHttpParser extends LoggingUtil{

  def updateReads(): HttpReads[Either[ErrorModel, VatSubscriptionResponse]] = new HttpReads[Either[ErrorModel, VatSubscriptionResponse]] {
    override def read(method: String, url: String, response: HttpResponse): Either[ErrorModel, VatSubscriptionResponse] = {
      implicit val res:HttpResponse = response
      response.status match {
        case Status.OK => Right(VatSubscriptionSuccess)
        case status =>
          warnLogRes(s"[VatSubscriptionHttpParser][updateReads]: Unexpected Response, Status: $status. Body: ${response.body}")
          Left(ErrorModel(status, "Downstream error returned when updating Vat Subscription"))
      }
    }
  }
}
