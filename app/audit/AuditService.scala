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

package audit

import audit.models.AuditModel
import javax.inject.{Inject, Singleton}
import play.api.http.HeaderNames
import play.api.libs.json.{JsObject, JsValue, Json}
import play.api.{Configuration, Logger}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.AuditExtensions
import uk.gov.hmrc.play.audit.http.connector.AuditResult.{Disabled, Failure, Success}
import uk.gov.hmrc.play.audit.http.connector.{AuditConnector, AuditResult}
import uk.gov.hmrc.play.audit.model.ExtendedDataEvent
import uk.gov.hmrc.play.config.AppName

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AuditService @Inject()(auditConnector: AuditConnector,
                             override val appNameConfiguration: Configuration) extends AppName {

  def auditEvent(event: AuditModel, path: Option[String])(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[AuditResult] = {
    val dataEvent = toExtendedDataEvent(event, path)
    Logger.debug(s"[AuditService][auditEvent] Auditing data event: \n\n$dataEvent")
    auditConnector.sendExtendedEvent(dataEvent).map(handleAuditResult)
  }

  private[audit] def toExtendedDataEvent(event: AuditModel, path: Option[String])
                                           (implicit hc: HeaderCarrier, ec: ExecutionContext): ExtendedDataEvent = {

    val auditPath = path.fold(hc.headers.find(_._1 == HeaderNames.REFERER).map(_._2).getOrElse("-"))(x => x)

    val eventTags = AuditExtensions.auditHeaderCarrier(hc).toAuditTags(event.transactionName, auditPath)

    val eventDetails: JsValue = Json.toJson(AuditExtensions.auditHeaderCarrier(hc).toAuditDetails()).as[JsObject].deepMerge(event.detail.as[JsObject])

    ExtendedDataEvent(
      auditSource = appName,
      auditType = event.auditType,
      detail = eventDetails,
      tags = eventTags
    )
  }

  private[audit] def handleAuditResult: AuditResult => AuditResult = {
    case Success =>
      Logger.debug("Splunk Audit Successful")
      Success
    case failure@Failure(err, _) =>
      Logger.debug(s"Splunk Audit Error, message: $err")
      failure
    case Disabled =>
      Logger.debug(s"Auditing Disabled")
      Disabled
  }

}
