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

package audit.services

import audit.models.ExtendedAuditModel
import config.FrontendAppConfig
import play.api.http.HeaderNames
import play.api.libs.json.{Json, Writes}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.AuditExtensions
import uk.gov.hmrc.play.audit.http.connector.{AuditConnector, AuditResult}
import uk.gov.hmrc.play.audit.http.connector.AuditResult.{Disabled, Failure, Success}
import uk.gov.hmrc.play.audit.model.ExtendedDataEvent
import utils.LoggingUtil

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AuditService @Inject()(appConfig: FrontendAppConfig, val auditConnector: AuditConnector) extends LoggingUtil {

  val referrer: HeaderCarrier => String = _.extraHeaders.find(_._1 == HeaderNames.REFERER).map(_._2).getOrElse("-")

  def auditExtendedEvent[T <: ExtendedAuditModel](auditModel: T)
                                                 (implicit hc: HeaderCarrier, ec: ExecutionContext, writes: Writes[T]): Unit = {
    auditConnector.sendExplicitAudit(auditModel.auditType, Json.toJson(auditModel))
  }

  def extendedAudit(auditModel: ExtendedAuditModel, path: Option[String] = None)(implicit hc: HeaderCarrier, ec: ExecutionContext): Unit = {
    val extendedDataEvent = toExtendedDataEvent(appConfig.appName, auditModel, path.fold(referrer(hc))(x => x))
    debug(s"Splunk Audit Event:\n\n$extendedDataEvent")
    handleAuditResult(auditConnector.sendExtendedEvent(extendedDataEvent))
  }

  def toExtendedDataEvent(appName: String, auditModel: ExtendedAuditModel, path: String)(implicit hc: HeaderCarrier): ExtendedDataEvent = {

    ExtendedDataEvent(
      auditSource = appName,
      auditType = auditModel.auditType,
      tags = AuditExtensions.auditHeaderCarrier(hc).toAuditTags(auditModel.transactionName, path),
      detail = auditModel.detail
    )
  }

  private def handleAuditResult(auditResult: Future[AuditResult])(implicit ec: ExecutionContext): Unit = auditResult.map {
    //$COVERAGE-OFF$ Disabling scoverage as returns Unit, only used for Debug messages
    case Success =>
      debug("Splunk Audit Successful")
    case Failure(err, _) =>
      debug(s"Splunk Audit Error, message: $err")
    case Disabled =>
      debug(s"Auditing Disabled")
    //$COVERAGE-ON$
  }
}
