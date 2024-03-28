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

package services

import config.AppConfig
import models.VatThreshold
import uk.gov.hmrc.http.InternalServerException
import utils.{LoggingUtil, MoneyFormatter}
import play.api.mvc.Request

import java.time._
import javax.inject.{Inject, Singleton}

@Singleton
class ThresholdService @Inject()()(implicit appConfig: AppConfig) extends LoggingUtil {


  def now: LocalDateTime = LocalDateTime.now()

  def getVatThreshold(): Option[VatThreshold] = {
    logger.debug(s"[ThresholdService][getVatThreshold] thresholds from config \n ${appConfig.thresholds}")
    appConfig.thresholds
      .sortWith(_.dateTime isAfter _.dateTime)
      .find(model => now.isAfter(model.dateTime) || now.isEqual(model.dateTime))
  }

  def formattedVatThresholdVal(): String = {
    getVatThreshold().fold {
      val msg = "[ThresholdService][formattedThreshold] Could not retrieve threshold from config"
      logger.error(msg)
      throw new InternalServerException(msg)
    } { threshold =>
      logger.info(s"[ThresholdService][formattedThreshold] displayed threshold ${threshold.amount}")
      MoneyFormatter.currencyFormat(threshold.amount)
    }
  }

  def formattedVatThreshold()(implicit request: Request[_]): String = {
    getVatThreshold().fold{
      val msg = "[ThresholdService][formattedThreshold] Could not retrieve threshold from config"
      errorLog(msg)
      throw new InternalServerException(msg)
    }{ threshold =>
      infoLog(s"[ThresholdService][formattedThreshold] displayed threshold ${threshold.amount}")
      MoneyFormatter.currencyFormat(threshold.amount)
    }
  }
}