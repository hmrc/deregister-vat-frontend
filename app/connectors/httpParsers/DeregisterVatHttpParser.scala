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

package connectors.httpParsers

import models.{DeregisterVatResponse, DeregisterVatSuccess, ErrorModel}
import play.api.Logger
import play.api.http.Status
import play.api.libs.json.Format
import uk.gov.hmrc.http.{HttpReads, HttpResponse}

object DeregisterVatHttpParser {

  def getReads[T](implicit fmt: Format[T]): HttpReads[Either[ErrorModel, Option[T]]] = new HttpReads[Either[ErrorModel, Option[T]]] {
    override def read(method: String, url: String, response: HttpResponse): Either[ErrorModel, Option[T]] = {
      response.status match {
        case Status.OK => {
          Logger.debug("[DeregisterVatHttpParser][getReads]: Status OK")
          response.json.validate[T].fold(
            invalid => {
              Logger.debug(s"[DeregisterVatHttpParser][getReads]: Invalid Json $invalid")
              Logger.warn(s"[DeregisterVatHttpParser][getReads]: Invalid Json from deregister-vat")
              Left(ErrorModel(Status.INTERNAL_SERVER_ERROR, "Invalid Json returned from deregister-vat"))
            },
            valid => Right(Some(valid))
          )
        }
        case Status.NOT_FOUND => Right(None)
        case status =>
          Logger.warn(s"[DeregisterVatHttpParser][getReads]: Unexpected Response, Status $status returned")
          Left(ErrorModel(status, s"Downstream error returned when retrieving Model from Deregister Vat"))
      }
    }
  }

  def updateReads: HttpReads[Either[ErrorModel, DeregisterVatResponse]] = new HttpReads[Either[ErrorModel, DeregisterVatResponse]] {
    override def read(method: String, url: String, response: HttpResponse): Either[ErrorModel, DeregisterVatResponse] = {
      response.status match {
        case Status.NO_CONTENT => Right(DeregisterVatSuccess)
        case status =>
          Logger.warn(s"[DeregisterVatHttpParser][updateReads]: Unexpected Response, Status $status returned")
          Left(ErrorModel(status, s"Downstream error returned when updating Deregister Vat"))
      }
    }
  }
}
