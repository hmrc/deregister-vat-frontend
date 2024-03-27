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

package config

import com.typesafe.config.{ConfigList, ConfigRenderOptions}
import config.features.Features
import config.{ConfigKeys => Keys}
import models.VatThreshold
import play.api.Configuration
import play.api.i18n.Lang
import play.api.mvc.Call
import uk.gov.hmrc.play.bootstrap.binders.SafeRedirectUrl
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig
import play.api.libs.json.Json

import javax.inject.{Inject, Singleton}

trait AppConfig {
  val reportAProblemPartialUrl: String
  val reportAProblemNonJSUrl: String
  val signInUrl: String
  def signOutUrl(identifier: String): String
  def surveyUrl(identifier: String): String
  val unauthorisedSignOutUrl: String
  val agentServicesGovUkGuidance: String
  val clientServicesGovUkGuidance: String
  val govUkCancelVatRegistration: String
  val govUkVatRatesInfo: String
  val govUkFindSicCode: String
  val manageVatSubscriptionFrontendUrl: String
  val vatSummaryFrontendUrl: String
  val changeClientUrl: String
  def vatAgentClientLookupHandoff(redirectUrl: String): String

  val vatAgentClientLookupFrontendUrl: String
  val agentClientLookupAgentHubPath: String

  def vatAgentClientLookupUnauthorised(redirectUrl: String): String

  def agentClientLookupUrl: String

  def agentClientUnauthorisedUrl: String

  val vatSubscriptionUrl: String
  val deregisterVatUrl: String
  val thresholdString: String
  val thresholds: Seq[VatThreshold]
  val deregThreshold: Int
  val features: Features
  val feedbackUrl: String
  val platformHost: String
  val timeoutPeriod: Int
  val timeoutCountdown: Int

  val languageFallbackUrl: String
  val languageMap: Map[String, Lang]
  val routeToSwitchLanguage: String => Call

  val gtmContainer: String
  val btaHomeUrl: String
}

@Singleton
class FrontendAppConfig @Inject()(servicesConfig: ServicesConfig, implicit val runModeConfiguration: Configuration) extends AppConfig {


  lazy val appName: String = servicesConfig.getString("appName")

  private lazy val contactHost = servicesConfig.getString(Keys.contactFrontendService)
  private lazy val contactFormServiceIdentifier = servicesConfig.getString(Keys.contactFrontendIdentifier)

  override lazy val reportAProblemPartialUrl = s"$contactHost/contact/problem_reports_ajax?service=$contactFormServiceIdentifier"
  override lazy val reportAProblemNonJSUrl = s"$contactHost/contact/problem_reports_nonjs?service=$contactFormServiceIdentifier"

  override lazy val feedbackUrl: String = s"$contactHost/contact/beta-feedback?service=$contactFormServiceIdentifier" +
    s"&backUrl=${SafeRedirectUrl(platformHost + controllers.routes.DeregisterForVATController.show.url).encodedUrl}"

  private lazy val signInBaseUrl: String = servicesConfig.getString(Keys.signInBaseUrl)
  private lazy val signInContinueBaseUrl: String = servicesConfig.getString(Keys.signInContinueBaseUrl)
  private lazy val signInContinueUrl: String = SafeRedirectUrl(signInContinueBaseUrl + controllers.routes
                                               .DeregisterForVATController.show.url).encodedUrl
  private lazy val signInOrigin: String = servicesConfig.getString("appName")
  override lazy val signInUrl: String = s"$signInBaseUrl?continue=$signInContinueUrl&origin=$signInOrigin"

  override lazy val manageVatSubscriptionFrontendUrl: String =
    servicesConfig.getString(Keys.manageVatSubscriptionFrontendHost) +
    servicesConfig.getString(Keys.manageVatSubscriptionFrontendUrl)

  override lazy val vatSummaryFrontendUrl: String = servicesConfig.getString(Keys.vatSummaryFrontendHost) +
                                                    servicesConfig.getString(Keys.vatSummaryFrontendUrl)

  override lazy val changeClientUrl: String =
    servicesConfig.getString(Keys.vatAgentClientLookupFrontendHost) + servicesConfig.getString(Keys.changeClientUrl) +
      s"?redirectUrl=${SafeRedirectUrl(servicesConfig.getString(ConfigKeys.platformHost) +
      "/vat-through-software/account/deregister/").encodedUrl}"

  override lazy val vatAgentClientLookupFrontendUrl: String =
    servicesConfig.getString(Keys.vatAgentClientLookupFrontendHost) +
    servicesConfig.getString(Keys.vatAgentClientLookupFrontendUrl)

  override lazy val agentClientLookupAgentHubPath: String =
    vatAgentClientLookupFrontendUrl + servicesConfig.getString(Keys.agentClientLookupAgentHub)

  override def vatAgentClientLookupHandoff(redirectUrl: String): String =
    vatAgentClientLookupFrontendUrl + s"/client-vat-number?redirectUrl=${SafeRedirectUrl(servicesConfig.getString(Keys.platformHost) +
    redirectUrl).encodedUrl}"

  override def vatAgentClientLookupUnauthorised(redirectUrl: String): String =
    vatAgentClientLookupFrontendUrl + s"/unauthorised-for-client?redirectUrl=${SafeRedirectUrl(servicesConfig.getString(Keys.platformHost) +
    redirectUrl).encodedUrl}"

  override def agentClientUnauthorisedUrl: String =
    if (features.stubAgentClientLookup()) {
      testOnly.controllers.routes.StubAgentClientLookupController.unauth(controllers.routes.DeregisterForVATController.show.url).url
    } else {
      vatAgentClientLookupUnauthorised(controllers.routes.DeregisterForVATController.show.url)
    }

  override def agentClientLookupUrl: String =
    if (features.stubAgentClientLookup()) {
      testOnly.controllers.routes.StubAgentClientLookupController.show(controllers.routes.DeregisterForVATController.show.url).url
    } else {
      vatAgentClientLookupHandoff(controllers.routes.DeregisterForVATController.show.url)
    }

  override lazy val vatSubscriptionUrl: String = servicesConfig.baseUrl(Keys.vatSubscriptionService)

  override lazy val deregisterVatUrl: String = servicesConfig.baseUrl(Keys.deregisterVatService)

  private lazy val governmentGatewayHost: String = servicesConfig.getString(Keys.governmentGatewayHost)

  private lazy val surveyBaseUrl = servicesConfig.getString(Keys.surveyHost) + servicesConfig.getString(Keys.surveyUrl)
  override def surveyUrl(identifier: String): String = s"$surveyBaseUrl/$identifier"

  override def signOutUrl(identifier: String): String =
    s"$governmentGatewayHost/bas-gateway/sign-out-without-state?continue=${surveyUrl(identifier)}"

  override lazy val unauthorisedSignOutUrl: String =
    s"$governmentGatewayHost/bas-gateway/sign-out-without-state?continue=$signInContinueUrl"

  override lazy val agentServicesGovUkGuidance: String = servicesConfig.getString(Keys.govUkSetupAgentServices)

  override lazy val clientServicesGovUkGuidance: String = servicesConfig.getString(Keys.govUkSetupClientServices)

  override lazy val govUkCancelVatRegistration: String = servicesConfig.getString(Keys.govUkCancelVatRegistration)

  override lazy val govUkVatRatesInfo: String = servicesConfig.getString(Keys.govUkVatRatesInfo)

  override lazy val govUkFindSicCode: String = servicesConfig.getString(Keys.govUkFindSicCode)

  override lazy val thresholdString: String = runModeConfiguration.get[ConfigList]("vat-threshold").render(ConfigRenderOptions.concise())
  override lazy val thresholds: Seq[VatThreshold] = Json.parse(thresholdString).as[List[VatThreshold]]

  override lazy val deregThreshold: Int = servicesConfig.getInt(Keys.deregThreshold)

  override val features = new Features

  override lazy val platformHost: String = servicesConfig.getString(Keys.platformHost)

  override lazy val timeoutCountdown: Int = servicesConfig.getInt(Keys.timeoutCountdown)
  override lazy val timeoutPeriod: Int = servicesConfig.getInt(Keys.timeoutPeriod)

  override val languageFallbackUrl: String = "/vat-through-software/account/deregister/"
  override val languageMap: Map[String, Lang] = Map(
    "english" -> Lang("en"),
    "cymraeg" -> Lang("cy")
  )
  override val routeToSwitchLanguage: String => Call = (lang: String) => controllers.routes.LanguageController.switchLanguage(lang)

  override val gtmContainer: String = servicesConfig.getString(Keys.gtmContainer)

  override lazy val btaHomeUrl: String = servicesConfig.getString(Keys.businessTaxAccountHost) +
    servicesConfig.getString(Keys.businessTaxAccountUrl)
}
