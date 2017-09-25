
import play.core.PlayVersion
import play.sbt.PlayImport.ws
import sbt.{ForkOptions, TestDefinition}
import sbt.Tests.{Group, SubProcess}
import sbt._

private object TestPhases {

  def oneForkedJvmPerTest(tests: Seq[TestDefinition]): Seq[Group] =
    tests map {
      test => Group(
        test.name,
        Seq(test),
        SubProcess(ForkOptions(runJVMOptions = Seq("-Dtest.name=" + test.name, "-Dlogger.resource=logback-test.xml")))
      )
    }
}

private object AppDependencies {

  val compile = Seq(
    ws,
    "uk.gov.hmrc" %% "frontend-bootstrap" % "8.6.0",
    "uk.gov.hmrc" %% "play-partials" % "6.0.0",
    "uk.gov.hmrc" %% "play-whitelist-filter" % "2.0.0"
  )

  def test(scope: String = "test"): Seq[ModuleID] = Seq(
    "uk.gov.hmrc" %% "hmrctest" % "2.4.0" % scope,
    "org.scalatest" %% "scalatest" % "3.0.1" % scope,
    "org.pegdown" % "pegdown" % "1.6.0" % scope,
    "org.jsoup" % "jsoup" % "1.10.3" % scope,
    "com.typesafe.play" %% "play-test" % PlayVersion.current % scope,
    "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.0" % scope,
    "org.mockito" % "mockito-core" % "2.9.0" % scope
  )

  def apply(): Seq[ModuleID] = compile ++ test()

}


