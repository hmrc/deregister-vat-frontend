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

package helpers

import common.SessionKeys
import config.AppConfig
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, GivenWhenThen, TestSuite}
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.data.Form
import play.api.http.HeaderNames
import play.api.i18n.{Lang, Messages, MessagesApi}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.ws.{WSRequest, WSResponse}
import play.api.{Application, Environment, Mode}
import stubs.{AuthStub, VatSubscriptionStub}
import uk.gov.hmrc.play.test.UnitSpec

trait IntegrationBaseSpec extends UnitSpec
  with WireMockHelper
  with GuiceOneServerPerSuite
  with TestSuite
  with BeforeAndAfterEach
  with BeforeAndAfterAll
  with GivenWhenThen
  with CustomMatchers {

  val mockHost: String = WireMockHelper.host
  val mockPort: String = WireMockHelper.wmPort.toString
  val appRouteContext: String = "/vat-through-software/account/cancel-vat"

  implicit lazy val appConfig: AppConfig = app.injector.instanceOf[AppConfig]
  lazy val messagesApi: MessagesApi = app.injector.instanceOf[MessagesApi]
  implicit lazy val messages: Messages = messagesApi.preferred(Seq(Lang("en-GB")))

  val titleSuffix = " - Business tax account - GOV.UK"
  val titleSuffixOther = " - VAT - GOV.UK"

  class PreconditionBuilder {
    implicit val builder: PreconditionBuilder = this

    def user: User = new User()
  }

  def isNotInsolvent:  Map[String, String] = Map(SessionKeys.insolventWithoutAccessKey -> "false")

  def formatPendingDereg: Option[String] => Map[String, String] =
    _.fold(Map.empty[String, String])(x => Map(SessionKeys.registrationStatusKey -> x))

  def given: PreconditionBuilder = new PreconditionBuilder

  class User()(implicit builder: PreconditionBuilder) {

    def isAuthorised: PreconditionBuilder = {
      Given("User is enrolled to HMRC-MTD-VAT")
      AuthStub.authorisedIndividual()
      builder
    }

    def isNotAuthenticated: PreconditionBuilder = {
      Given("User is not logged in")
      AuthStub.unauthenticated()
      builder
    }

    def isNotAuthorised: PreconditionBuilder = {
      Given("User is not enrolled to HMRC-MTD-VAT")
      AuthStub.unauthorisedIndividualMissingEnrolment()
      builder
    }

    def noDeregPending: PreconditionBuilder = {
      Given("User has no deregistration request pending")
      VatSubscriptionStub.noDeregPending()
      builder
    }

    def deregPending: PreconditionBuilder = {
      Given("User has a deregistration request pending")
      VatSubscriptionStub.deregPending()
      builder
    }

    def noPendingData: PreconditionBuilder = {
      Given("User has no pending data")
      VatSubscriptionStub.noPendingData()
      builder
    }
  }

  def servicesConfig: Map[String, String] = Map(
    "play.filters.csrf.header.bypassHeaders.Csrf-Token" -> "nocheck",
    "microservice.services.auth.host" -> mockHost,
    "microservice.services.auth.port" -> mockPort,
    "microservice.services.deregister-vat.host" -> mockHost,
    "microservice.services.deregister-vat.port" -> mockPort,
    "microservice.services.vat-subscription.port" -> mockPort,
    "microservice.services.vat-subscription.host" -> mockHost,
    "microservice.services.vat-subscription-dynamic-stub.port" -> mockPort,
    "microservice.services.vat-subscription-dynamic-stub.host" -> mockHost
  )

  override implicit lazy val app: Application = new GuiceApplicationBuilder()
    .in(Environment.simple(mode = Mode.Dev))
    .configure(servicesConfig)
    .build()

  override def beforeAll(): Unit = {
    super.beforeAll()
    startWireMock()
  }

  override def afterAll(): Unit = {
    stopWireMock()
    super.afterAll()
  }

  def get(path: String, additionalCookies: Map[String, String] = Map.empty): WSResponse = await(
    buildRequest(path, additionalCookies).get()
  )

  def post(path: String, additionalCookies: Map[String, String] = Map.empty)(body: Map[String, Seq[String]]): WSResponse = await(
    buildRequest(path, additionalCookies).post(body)
  )

  def put(path: String, additionalCookies: Map[String, String] = Map.empty)(body: Map[String, Seq[String]]): WSResponse = await(
    buildRequest(path, additionalCookies).put(body)
  )

  def buildRequest(path: String, additionalCookies: Map[String, String] = Map.empty): WSRequest =
    client.url(s"http://localhost:$port$appRouteContext$path")
      .withHttpHeaders(HeaderNames.COOKIE -> SessionCookieBaker.bakeSessionCookie(additionalCookies), "Csrf-Token" -> "nocheck")
      .withFollowRedirects(false)

  def redirectLocation(response: WSResponse): Option[String] = response.header(HeaderNames.LOCATION)

  def toFormData[T](form: Form[T], data: T): Map[String, Seq[String]] =
    form.fill(data).data map { case (k, v) => k -> Seq(v) }
}
