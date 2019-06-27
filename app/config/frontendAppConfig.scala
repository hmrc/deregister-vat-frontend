/*
 * Copyright 2019 HM Revenue & Customs
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

import java.util.Base64

import javax.inject.{Inject, Singleton}
import config.features.Features
import play.api.mvc.Call
import play.api.{Configuration, Environment}
import uk.gov.hmrc.play.binders.ContinueUrl
import uk.gov.hmrc.play.config.ServicesConfig
import config.{ConfigKeys => Keys}
import play.api.Mode.Mode
import play.api.i18n.Lang

trait AppConfig extends ServicesConfig {
  val analyticsToken: String
  val analyticsHost: String
  val reportAProblemPartialUrl: String
  val reportAProblemNonJSUrl: String
  val whitelistEnabled: Boolean
  val whitelistedIps: Seq[String]
  val whitelistExcludedPaths: Seq[Call]
  val shutterPage: String
  val signInUrl: String
  val signOutUrl: String
  val timeOutSignOutUrl: String
  val surveyUrl: String
  val unauthorisedSignOutUrl: String
  val agentServicesGovUkGuidance: String
  val clientServicesGovUkGuidance: String
  val govUkCancelVatRegistration: String
  val manageVatSubscriptionFrontendUrl: String
  val changeClientUrl: String

  def vatAgentClientLookupHandoff(redirectUrl: String): String

  val vatAgentClientLookupFrontendUrl: String

  def vatAgentClientLookupUnauthorised(redirectUrl: String): String

  def agentClientLookupUrl: String

  def agentClientUnauthorisedUrl: String

  val vatSubscriptionUrl: String
  val deregisterVatUrl: String
  val deregThreshold: Int
  val features: Features
  val feedbackUrl: String
  val platformHost: String
  val timeoutPeriod: Int
  val timeoutCountdown: Int
  val contactPreferencesService: String

  def contactPreferencesUrl(vrn: String): String

  val languageFallbackUrl: String
  val languageMap: Map[String, Lang]
  val routeToSwitchLanguage: String => Call
}

@Singleton
class FrontendAppConfig @Inject()(environment: Environment, implicit val runModeConfiguration: Configuration) extends AppConfig {

  lazy val appName: String = runModeConfiguration.getString("appName").getOrElse(throw new Exception("Missing configuration key: appName"))


  override val mode: Mode = environment.mode

  private val contactHost = getString(Keys.contactFrontendService)
  private val contactFormServiceIdentifier = "VATVC"

  override lazy val analyticsToken: String = getString(Keys.googleAnalyticsToken)
  override lazy val analyticsHost: String = getString(Keys.googleAnalyticsHost)
  override lazy val reportAProblemPartialUrl = s"$contactHost/contact/problem_reports_ajax?service=$contactFormServiceIdentifier"
  override lazy val reportAProblemNonJSUrl = s"$contactHost/contact/problem_reports_nonjs?service=$contactFormServiceIdentifier"

  override lazy val feedbackUrl: String = s"$contactHost/contact/beta-feedback?service=$contactFormServiceIdentifier" +
    s"&backUrl=${ContinueUrl(platformHost + controllers.routes.DeregisterForVATController.show().url).encodedUrl}"

  private def whitelistConfig(key: String): Seq[String] =
    Some(new String(Base64.getDecoder.decode(runModeConfiguration.getString(key)
      .getOrElse("")), "UTF-8")).map(_.split(",")).getOrElse(Array.empty).toSeq

  override lazy val whitelistEnabled: Boolean = runModeConfiguration.getBoolean(Keys.whitelistEnabled).getOrElse(true)
  override lazy val whitelistedIps: Seq[String] = whitelistConfig(Keys.whitelistedIps)
  override lazy val whitelistExcludedPaths: Seq[Call] = whitelistConfig(Keys.whitelistExcludedPaths).map(path => Call("GET", path))
  override lazy val shutterPage: String = getString(Keys.whitelistShutterPage)

  private lazy val signInBaseUrl: String = getString(Keys.signInBaseUrl)
  private lazy val signInContinueBaseUrl: String = runModeConfiguration.getString(Keys.signInContinueBaseUrl).getOrElse("")
  private lazy val signInContinueUrl: String = ContinueUrl(signInContinueBaseUrl + controllers.routes.DeregisterForVATController.show().url).encodedUrl
  private lazy val signInOrigin: String = getString("appName")
  override lazy val signInUrl: String = s"$signInBaseUrl?continue=$signInContinueUrl&origin=$signInOrigin"

  override lazy val manageVatSubscriptionFrontendUrl: String =
    getString(Keys.manageVatSubscriptionFrontendHost) + getString(Keys.manageVatSubscriptionFrontendUrl)

  override lazy val changeClientUrl: String =
    getString(Keys.vatAgentClientLookupFrontendHost) + getString(Keys.changeClientUrl) +
      s"?redirectUrl=${ContinueUrl(getString(ConfigKeys.platformHost) + "/vat-through-software/account/deregister/").encodedUrl}"

  override lazy val vatAgentClientLookupFrontendUrl: String =
    getString(Keys.vatAgentClientLookupFrontendHost) + getString(Keys.vatAgentClientLookupFrontendUrl)

  override def vatAgentClientLookupHandoff(redirectUrl: String): String =
    vatAgentClientLookupFrontendUrl + s"/client-vat-number?redirectUrl=${ContinueUrl(getString(Keys.platformHost) + redirectUrl).encodedUrl}"

  override def vatAgentClientLookupUnauthorised(redirectUrl: String): String =
    vatAgentClientLookupFrontendUrl + s"/unauthorised-for-client?redirectUrl=${ContinueUrl(getString(Keys.platformHost) + redirectUrl).encodedUrl}"

  override def agentClientUnauthorisedUrl: String =
    if (features.stubAgentClientLookup()) {
      testOnly.controllers.routes.StubAgentClientLookupController.unauth(controllers.routes.DeregisterForVATController.show().url).url
    } else {
      vatAgentClientLookupUnauthorised(controllers.routes.DeregisterForVATController.show().url)
    }

  override def agentClientLookupUrl: String =
    if (features.stubAgentClientLookup()) {
      testOnly.controllers.routes.StubAgentClientLookupController.show(controllers.routes.DeregisterForVATController.show().url).url
    } else {
      vatAgentClientLookupHandoff(controllers.routes.DeregisterForVATController.show().url)
    }

  override lazy val contactPreferencesService: String = {
    if (features.stubContactPreferences()) {
      baseUrl("vat-subscription-dynamic-stub")
    } else {
      baseUrl(Keys.contactPreferencesService)
    }
  }

  override def contactPreferencesUrl(vrn: String): String = contactPreferencesService + s"/contact-preferences/vat/vrn/$vrn"

  override lazy val vatSubscriptionUrl: String = baseUrl(Keys.vatSubscriptionService)

  override lazy val deregisterVatUrl: String = baseUrl(Keys.deregisterVatService)

  private lazy val governmentGatewayHost: String = getString(Keys.governmentGatewayHost)

  private lazy val surveyBaseUrl = getString(Keys.surveyHost) + getString(Keys.surveyUrl)
  override lazy val surveyUrl = s"$surveyBaseUrl/$contactFormServiceIdentifier"

  override lazy val signOutUrl = s"$governmentGatewayHost/gg/sign-out?continue=$surveyUrl"

  private lazy val timeOutRedirectUrl = platformHost + controllers.routes.SignOutController.timeout().url
  override lazy val timeOutSignOutUrl = s"$governmentGatewayHost/gg/sign-out?continue=$timeOutRedirectUrl"

  override lazy val unauthorisedSignOutUrl: String = s"$governmentGatewayHost/gg/sign-out?continue=$signInContinueUrl"

  override lazy val agentServicesGovUkGuidance: String = getString(Keys.govUkSetupAgentServices)

  override lazy val clientServicesGovUkGuidance: String = getString(Keys.govUkSetupClientServices)

  override lazy val govUkCancelVatRegistration: String = getString(Keys.govUkCancelVatRegistration)

  override lazy val deregThreshold: Int = getInt(Keys.deregThreshold)

  override val features = new Features

  override lazy val platformHost: String = getString(Keys.platformHost)

  override lazy val timeoutCountdown: Int = getInt(Keys.timeoutCountdown)
  override lazy val timeoutPeriod: Int = getInt(Keys.timeoutPeriod)

  override val languageFallbackUrl: String = "/vat-through-software/account/deregister/"
  override val languageMap: Map[String, Lang] = Map(
    "english" -> Lang("en"),
    "cymraeg" -> Lang("cy")
  )
  override val routeToSwitchLanguage: String => Call = (lang: String) => controllers.routes.LanguageController.switchLanguage(lang)
}
