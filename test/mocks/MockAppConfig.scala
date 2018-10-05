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

package mocks

import config.AppConfig
import config.features.Features
import play.api.{Configuration, Mode}
import play.api.mvc.Call
import play.api.Mode.Mode

class MockAppConfig(val runModeConfiguration: Configuration, val mode: Mode = Mode.Test) extends AppConfig {
  override val analyticsToken: String = ""
  override val analyticsHost: String = ""
  override val reportAProblemPartialUrl: String = ""
  override val reportAProblemNonJSUrl: String = ""
  override val whitelistEnabled: Boolean = false
  override val whitelistedIps: Seq[String] = Seq("")
  override val whitelistExcludedPaths: Seq[Call] = Nil
  override val shutterPage: String = "https://www.tax.service.gov.uk/shutter/vat-through-software"
  override val govUkCancelVatRegistration: String = "https://www.gov.uk/government/publications/vat-application-to-cancel-your-vat-registration-vat7"
  override val signInUrl: String = ""
  override val signOutUrl: String = "/some-gg-signout-url"
  override val agentServicesGovUkGuidance: String = "guidance/get-an-hmrc-agent-services-account"
  override val clientServicesGovUkGuidance: String = "government/publications/vat-returns-and-ec-sales-list-commercial-software-suppliers/vat-commercial-software-suppliers"
  override val surveyUrl: String = "/some-survey-url"
  override val unauthorisedSignOutUrl: String = ""
  override val manageVatSubscriptionFrontendUrl: String = "http://localhost:9150/vat-through-software/account/change-business-details"
  override val deregisterVatUrl: String = "http://localhost:9164"
  override val deregThreshold: Int = 83000
  val thresholdString: String = java.text.NumberFormat.getIntegerInstance.format(deregThreshold)

  override val features: Features = new Features(runModeConfiguration)
}

