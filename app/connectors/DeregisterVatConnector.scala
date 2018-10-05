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

package connectors

import javax.inject.{Inject, Singleton}

import config.AppConfig
import connectors.httpParsers.DeregisterVatHttpParser
import models.{DeregisterVatResponse, ErrorModel}
import play.api.Logger
import play.api.libs.json.Format
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.http.HttpClient

import scala.concurrent.{ExecutionContext, Future}


@Singleton
class DeregisterVatConnector @Inject()(val http: HttpClient,
                                       val config: AppConfig) {

  def url(vrn: String, key: String): String = s"${config.deregisterVatUrl}/deregister-vat/data/$vrn/$key"
  def url(vrn: String): String = s"${config.deregisterVatUrl}/deregister-vat/data/$vrn"

  def getAnswers[T](vrn: String, key: String)(implicit fmt: Format[T], hc: HeaderCarrier, ec: ExecutionContext)
  : Future[Either[ErrorModel, Option[T]]] = {
    Logger.debug(s"[DeregisterVatConnector][getAnswers] Getting answer for key: $key and vrn: $vrn")
    http.GET(url(vrn, key))(DeregisterVatHttpParser.getReads[T], hc, ec)
  }

  def putAnswers[T](vrn: String, key: String, model: T)(implicit fmt: Format[T], hc: HeaderCarrier, ec: ExecutionContext)
  : Future[Either[ErrorModel, DeregisterVatResponse]] = {
    Logger.debug(s"[DeregisterVatConnector][putAnswers] Calling PUT method to store data for URL: ${url(vrn, key)}")
    http.PUT[T, Either[ErrorModel, DeregisterVatResponse]](url(vrn, key), model)(fmt, DeregisterVatHttpParser.updateReads, hc, ec)
  }

  def deleteAnswer(vrn: String, key: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Either[ErrorModel, DeregisterVatResponse]] =
    http.DELETE(url(vrn, key))(DeregisterVatHttpParser.updateReads, hc, ec)

  def deleteAllAnswers(vrn: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Either[ErrorModel, DeregisterVatResponse]] =
    http.DELETE(url(vrn))(DeregisterVatHttpParser.updateReads, hc, ec)

}
