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

package config

object ConfigKeys {

  val contactFrontendService: String = "contact-frontend.host"

  val signInBaseUrl: String = "signIn.url"
  val signInContinueBaseUrl: String = "signIn.continueBaseUrl"

  val surveyHost: String = "feedback-frontend.host"
  val surveyUrl: String = "feedback-frontend.url"

  val governmentGatewayHost: String = "government-gateway.host"

  val stubAgentClientLookupFeature: String = "features.stubAgentClientLookup"
  val stubContactPreferencesFeature: String = "features.stubContactPreferences.enabled"
  val useLanguageSelectorFeature: String = "features.useLanguageSelectorFeature.enabled"
  val accessibilityStatement: String = "features.accessibilityStatement.enabled"
  val zeroRatedJourney: String = "features.zeroRatedJourney.enabled"
  val emailVerifiedFeature: String = "features.emailVerifiedFeature.enabled"
  val bulkPaperOffFeature: String = "features.bulkPaperOffFeature.enabled"
  val contactPrefMigrationFeature: String = "features.contactPrefMigration.enabled"

  val govUkCancelVatRegistration: String = "gov-uk.cancelVatRegistration.url"

  val govUkSetupAgentServices: String = "gov-uk.setupAgentServices.url"

  val govUkSetupClientServices: String = "gov-uk.setupClientServices.url"

  val govUkVatRatesInfo: String = "gov-uk.vatRatesInfo.url"

  val govUkFindSicCode: String = "gov-uk.findSicCode.url"

  val manageVatSubscriptionFrontendHost: String = "manage-vat-subscription-frontend.host"
  val manageVatSubscriptionFrontendUrl: String = "manage-vat-subscription-frontend.url"

  val vatSummaryFrontendHost: String = "vat-summary-frontend.host"
  val vatSummaryFrontendUrl: String = "vat-summary-frontend.url"

  val vatAgentClientLookupFrontendHost: String = "vat-agent-client-lookup-frontend.host"
  val vatAgentClientLookupFrontendUrl: String = "vat-agent-client-lookup-frontend.url"
  val changeClientUrl: String = "vat-agent-client-lookup-frontend.changeClientUrl"
  val agentClientLookupAgentHub: String = "vat-agent-client-lookup-frontend.agentHub"

  val vatSubscriptionService: String = "vat-subscription"

  val deregisterVatService: String = "deregister-vat"

  val deregThreshold: String = "thresholds.deregistrationThreshold"

  val platformHost: String = "platform.host"

  val timeoutPeriod: String = "timeout.period"
  val timeoutCountdown: String = "timeout.countdown"

  val contactPreferencesService: String = "contact-preferences"

  val accessibilityStatementHost: String = "accessibility-statement.host"
  val accessibilityStatementUrl: String = "accessibility-statement.url"

  val gtmContainer: String = "tracking-consent-frontend.gtm.container"
}
