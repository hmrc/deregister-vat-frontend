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

package connectors.httpParsers

import models.{ChangeIndicatorModel, ErrorModel}
import play.api.Logger
import play.api.http.Status
import uk.gov.hmrc.http.{HttpReads, HttpResponse}

object PendingDeregHttpParser {

  type GetDeregPendingResponse = Either[ErrorModel, ChangeIndicatorModel]

  implicit object PendingDeregReads extends HttpReads[Either[ErrorModel, ChangeIndicatorModel]] {

    override def read(method: String, url: String, response: HttpResponse): Either[ErrorModel, ChangeIndicatorModel] = {

      response.status match {
        case Status.OK => {
          Logger.debug("[PendingDeregHttpParser][read]: Status OK")
          response.json.validate[ChangeIndicatorModel].fold(
            invalid => {
              // $COVERAGE-OFF$
              Logger.warn(s"[PendingDeregHttpParser][read] - Invalid JSON")
              Logger.debug(s"[PendingDeregHttpParser][read] - Invalid JSON: $invalid")
              // $COVERAGE-ON$
              Left(ErrorModel(Status.INTERNAL_SERVER_ERROR, "Invalid Json"))
            },
            valid => {
              // $COVERAGE-OFF$
              Logger.debug(s"Successfully parsed the get customer info JSON: $valid")
                // $COVERAGE-ON$
              Right(valid)
            }
          )
        }
        case status =>
          // $COVERAGE-OFF$
          Logger.warn(s"[PendingDeregHttpParser][read]: Unexpected Response, Status $status returned,with " +
            s"response: ${response.body}")
          // $COVERAGE-ON$
          Left(ErrorModel(status, "Downstream error returned when retrieving pending deregistration status"))
      }
    }
  }
}
