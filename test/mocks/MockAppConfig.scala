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

package mocks

import config.AppConfig
import config.features.Features
import play.api.{Configuration, Mode}
import play.api.i18n.Lang
import play.api.mvc.Call

class MockAppConfig(implicit val runModeConfiguration: Configuration) extends AppConfig {
  val mode: Mode = Mode.Test
  override val reportAProblemPartialUrl: String = ""
  override val reportAProblemNonJSUrl: String = ""
  override val contactFormServiceIdentifier: String = "TEST"
  override val govUkCancelVatRegistration: String = "https://www.gov.uk/government/publications/vat-application-to-cancel-your-vat-registration-vat7"
  override val govUkVatRatesInfo: String = "govuk/vat-rates-info"
  override val govUkFindSicCode: String = "findSicCode.gov.uk"
  override val signInUrl: String = "/gg-sign-in"
  override def signOutUrl(identifier: String): String = s"/some-gg-signout-url/$identifier"
  override val agentServicesGovUkGuidance: String = "guidance/get-an-hmrc-agent-services-account"
  override val clientServicesGovUkGuidance: String =
    "government/publications/vat-returns-and-ec-sales-list-commercial-software-suppliers/vat-commercial-software-suppliers"
  override def surveyUrl(identifier: String): String = s"/some-survey-url/$identifier"
  override val unauthorisedSignOutUrl: String = ""
  override val manageVatSubscriptionFrontendUrl: String = "http://localhost:9150/vat-through-software/account/change-business-details"
  override val agentClientLookupAgentHubPath: String = "/representative/client-vat-account"
  override val vatAgentClientLookupFrontendUrl: String = "http://localhost:9149/vat-through-software/agent-lookup/client-vat-number"
  override val vatSummaryFrontendUrl: String = "/vat-through-software/vat-overview"
  override val vatSubscriptionUrl: String = "http://localhost:9567/vat-subscription"
  override val deregisterVatUrl: String = "http://localhost:9164"
  override val deregThreshold: Int = 83000
  val thresholdString: String = java.text.NumberFormat.getIntegerInstance.format(deregThreshold)

  override def vatAgentClientLookupHandoff(redirectUrl: String): String = s"/vaclfHandoff/$redirectUrl"
  override def vatAgentClientLookupUnauthorised(redirectUrl: String): String = s"/vaclfUnauth/$redirectUrl"
  override def agentClientLookupUrl: String = "/redirect/to/vaclf"
  override def agentClientUnauthorisedUrl: String = "/redirect/to/vaclf-unauth"

  override val features: Features = new Features
  override val feedbackUrl: String = "/feedback"
  override val platformHost: String = "/platform/host"
  override val timeoutCountdown: Int = 100
  override val timeoutPeriod: Int = 200

  override val languageFallbackUrl: String = "/vat-through-software/account/deregister/"
  override val languageMap: Map[String, Lang] = Map(
    "english" -> Lang("en"),
    "cymraeg" -> Lang("cy")
  )
  override val routeToSwitchLanguage: String => Call = (lang: String) => controllers.routes.LanguageController.switchLanguage(lang)
  override val changeClientUrl: String = "/changeClient"
  override val accessibilityStatementUrl: String = "http://localhost:9152/vat-through-software/accessibility"
  override val gtmContainer: String = "x"
}

