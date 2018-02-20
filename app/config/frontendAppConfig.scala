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
  val features: Features
}

@Singleton
class FrontendAppConfig @Inject()(val runModeConfiguration: Configuration, environment: Environment) extends AppConfig {

  override val mode: Mode = environment.mode

  private val contactHost = getString(Keys.contactFrontendService)
  private val contactFormServiceIdentifier = "VATVC"

  override lazy val analyticsToken: String = getString(Keys.googleAnalyticsToken)
  override lazy val analyticsHost: String = getString(Keys.googleAnalyticsHost)
  override lazy val reportAProblemPartialUrl = s"$contactHost/contact/problem_reports_ajax?service=$contactFormServiceIdentifier"
  override lazy val reportAProblemNonJSUrl = s"$contactHost/contact/problem_reports_nonjs?service=$contactFormServiceIdentifier"

  private def whitelistConfig(key: String): Seq[String] =
    Some(new String(Base64.getDecoder.decode(runModeConfiguration.getString(key)
      .getOrElse("")), "UTF-8")).map(_.split(",")).getOrElse(Array.empty).toSeq

  override lazy val whitelistEnabled: Boolean = runModeConfiguration.getBoolean(Keys.whitelistEnabled).getOrElse(true)
  override lazy val whitelistedIps: Seq[String] = whitelistConfig(Keys.whitelistedIps)
  override lazy val whitelistExcludedPaths: Seq[Call] = whitelistConfig(Keys.whitelistExcludedPaths).map(path => Call("GET", path))
  override lazy val shutterPage: String = getString(Keys.whitelistShutterPage)

  private lazy val signInBaseUrl: String = getString(Keys.signInBaseUrl)
  private lazy val signInContinueBaseUrl: String = runModeConfiguration.getString(Keys.signInContinueBaseUrl).getOrElse("")
  private lazy val signInContinueUrl: String = ContinueUrl(signInContinueBaseUrl + controllers.routes.HelloWorldController.helloWorld().url).encodedUrl
  private lazy val signInOrigin: String = getString("appName")
  override lazy val signInUrl: String = s"$signInBaseUrl?continue=$signInContinueUrl&origin=$signInOrigin"

  override val features = new Features(runModeConfiguration)
}
