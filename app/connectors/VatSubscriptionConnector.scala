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

package connectors

import config.AppConfig
import connectors.httpParsers.CustomerDetailsHttpParser.CustomerDetailsReads
import connectors.httpParsers.VatSubscriptionHttpParser
import models.deregistrationRequest.DeregistrationInfo
import models.{CustomerDetails, ErrorModel, VatSubscriptionResponse}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}
import javax.inject.{Inject, Singleton}
import utils.LoggerUtil

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class VatSubscriptionConnector @Inject()(val http: HttpClient,
                                         val config: AppConfig) extends LoggerUtil{

  def url(vrn: String): String = s"${config.vatSubscriptionUrl}/vat-subscription/$vrn/deregister"

  private[connectors] def getFullDetailsUrl(vrn: String) = s"${config.vatSubscriptionUrl}/vat-subscription/$vrn/full-information"

  def submit(vrn: String, deregistrationInfoModel: DeregistrationInfo)
            (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Either[ErrorModel, VatSubscriptionResponse]] = {
    http.PUT[DeregistrationInfo, Either[ErrorModel, VatSubscriptionResponse]](url(vrn), deregistrationInfoModel)(
      DeregistrationInfo.writes, VatSubscriptionHttpParser.updateReads, hc, ec)
  }

  def getCustomerDetails(id: String)(implicit headerCarrier: HeaderCarrier,
                                     ec: ExecutionContext): Future[Either[ErrorModel, CustomerDetails]] = {
    val url = getFullDetailsUrl(id)
    logger.debug(s"[VatSubscriptionConnector][getCustomerInfo]: Calling getCustomerInfo with URL - $url")
    http.GET(url)(CustomerDetailsReads, headerCarrier, ec)
  }
}
