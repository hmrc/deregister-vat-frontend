/*
 * Copyright 2022 HM Revenue & Customs
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

package views.utils

import config.AppConfig
import models.User
import play.api.i18n.Messages
import play.api.mvc.Request

object ServiceNameUtil{

  def generateHeader(implicit request: Request[_], messages: Messages): String = {
    request match {
      case user: User[_] => if (user.isAgent) messages("common.agentService") else messages("common.clientService")
      case _ => messages("common.vat")
    }
  }

  def generateServiceUrl(implicit request: Request[_], appConfig: AppConfig): Option[String] =
    request match {
      case user: User[_] => if (user.isAgent) Some(appConfig.agentClientLookupAgentHubPath) else Some(appConfig.vatSummaryFrontendUrl)
      case _ => None
    }

}