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
import play.api.libs.json.Format
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.http.HttpClient

import scala.concurrent.{ExecutionContext, Future}


@Singleton
class DeregisterVatConnector @Inject()(val http: HttpClient,
                                       val config: AppConfig)
                                      (implicit hc: HeaderCarrier, ec: ExecutionContext){

  def url(vrn: String, key: String): String = s"${config.deregisterVatUrl}/data/$vrn/$key"
  def url(vrn: String): String = s"${config.deregisterVatUrl}/data/$vrn"

  def getAnswers[T](vrn: String, key: String)(implicit fmt: Format[T]): Future[Either[ErrorModel, T]] =
    http.GET(url(vrn, key))(DeregisterVatHttpParser.getReads[T], hc, ec)

  def putAnswers[T](vrn: String, key: String, model: T)(implicit fmt: Format[T]): Future[Either[ErrorModel, DeregisterVatResponse]] =
    http.PUT[T, Either[ErrorModel, DeregisterVatResponse]](url(vrn, key), model)(fmt, DeregisterVatHttpParser.updateReads[T], hc, ec)

  def deleteAnswer[T](vrn: String, key: String)(implicit fmt: Format[T]): Future[Either[ErrorModel, DeregisterVatResponse]] =
    http.DELETE(url(vrn, key))(DeregisterVatHttpParser.updateReads[T], hc, ec)

  def deleteAllAnswers[T](vrn: String)(implicit fmt: Format[T]): Future[Either[ErrorModel, DeregisterVatResponse]] =
    http.DELETE(url(vrn))(DeregisterVatHttpParser.updateReads[T], hc, ec)

}
