/*
 * Copyright 2023 HM Revenue & Customs
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

import play.sbt.routes.RoutesKeys
import sbt.Tests.{Group, SubProcess}
import uk.gov.hmrc.DefaultBuildSettings._

val appName: String = "deregister-vat-frontend"
val bootstrapPlayVersion = "8.6.0"

lazy val appDependencies: Seq[ModuleID] = compile ++ test()
lazy val plugins: Seq[Plugins] = Seq.empty
lazy val playSettings: Seq[Setting[_]] = Seq.empty

scalacOptions ++= Seq("-Wconf:cat=unused-imports&site=.*views.html.*:s")
RoutesKeys.routesImport := Seq.empty

lazy val coverageSettings: Seq[Setting[_]] = {
  import scoverage.ScoverageKeys

  val excludedPackages = Seq(
    "<empty>",
    ".*Reverse.*",
    "app.*",
    "prod.*",
    "config.*",
    "testOnly.*",
    "views.*"
  )

  Seq(
    ScoverageKeys.coverageExcludedPackages := excludedPackages.mkString(";"),
    ScoverageKeys.coverageMinimumStmtTotal := 95,
    ScoverageKeys.coverageFailOnMinimum := true,
    ScoverageKeys.coverageHighlighting := true
  )
}

val compile: Seq[ModuleID] = Seq(
  ws,
  "uk.gov.hmrc"   %% "bootstrap-frontend-play-30" % bootstrapPlayVersion,
  "uk.gov.hmrc"   %% "play-frontend-hmrc-play-30" % "9.11.0",
  "org.typelevel" %% "cats-core"                  % "2.12.0"
)

def test(scope: String = "test, it"): Seq[ModuleID] = Seq(
  "uk.gov.hmrc"   %% "bootstrap-test-play-30"      % bootstrapPlayVersion % scope,
  "org.scalamock" %% "scalamock"                   % "6.0.0"              % scope
)

TwirlKeys.templateImports ++= Seq(
  "uk.gov.hmrc.govukfrontend.views.html.components._",
  "uk.gov.hmrc.hmrcfrontend.views.html.components._",
  "uk.gov.hmrc.hmrcfrontend.views.html.helpers._"
)

lazy val microservice: Project = Project(appName, file("."))
  .enablePlugins(Seq(PlayScala, SbtDistributablesPlugin) ++ plugins: _*)
  .disablePlugins(JUnitXmlReportPlugin)
  .settings(PlayKeys.playDefaultPort := 9153)
  .settings(coverageSettings: _*)
  .settings(playSettings: _*)
  .settings(scalaSettings: _*)
  .settings(defaultSettings(): _*)
  .settings(
    majorVersion := 0,
    scalaVersion := "2.13.16",
    libraryDependencies ++= appDependencies,
    scalacOptions ++= Seq("-Wconf:cat=unused-imports&src=html/.*:s", "-Wconf:cat=unused-imports&src=routes/.*:s"),
    retrieveManaged := true
  )
  .configs(IntegrationTest)
  .settings(inConfig(IntegrationTest)(Defaults.itSettings): _*)
  .settings(
    IntegrationTest / Keys.fork := false,
    IntegrationTest / unmanagedSourceDirectories := (IntegrationTest / baseDirectory)(base => Seq(base / "it")).value,
    addTestReportOption(IntegrationTest, "int-test-reports"),
    IntegrationTest / parallelExecution := false)
