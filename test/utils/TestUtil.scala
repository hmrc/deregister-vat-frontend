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

package utils

import assets.constants.BaseTestConstants._
import com.codahale.metrics.SharedMetricRegistries
import common.SessionKeys
import config.ServiceErrorHandler
import mocks.MockAppConfig
import models.User
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Configuration
import play.api.i18n.{Lang, Messages, MessagesApi, MessagesImpl}
import play.api.inject.Injector
import play.api.mvc._
import play.api.test.FakeRequest
import play.api.test.Helpers.{stubBodyParser, stubControllerComponents, stubMessagesApi}
import services.ContactPreferencesService
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.test._

import scala.concurrent.ExecutionContext

trait TestUtil extends UnitSpec with GuiceOneAppPerSuite with BeforeAndAfterEach with MaterializerSupport {

  override def beforeEach() {
    super.beforeEach()
    mockConfig.features.stubAgentClientLookup(true)
    mockConfig.features.stubContactPreferences(true)
    mockConfig.features.useLanguageSelector(true)
    mockConfig.features.accessibilityStatement(true)
    mockConfig.features.zeroRatedJourney(true)
    SharedMetricRegistries.clear()
  }



  private lazy val cc: ControllerComponents = stubControllerComponents()

  private lazy val messagesActionBuilder: MessagesActionBuilder =
    new DefaultMessagesActionBuilderImpl(stubBodyParser[AnyContent](), stubMessagesApi())

  lazy val mcc: MessagesControllerComponents = DefaultMessagesControllerComponents(
    messagesActionBuilder,
    DefaultActionBuilder(stubBodyParser[AnyContent]()),
    cc.parsers,
    fakeApplication.injector.instanceOf[MessagesApi],
    cc.langs,
    cc.fileMimeTypes,
    ExecutionContext.global
  )

  lazy implicit val config: Configuration = app.configuration
  lazy implicit val mockConfig: MockAppConfig = new MockAppConfig
  lazy implicit val mockUserContactPref: ContactPreferencesService = injector.instanceOf[ContactPreferencesService]
  lazy implicit val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()
  lazy val requestWithVRN: FakeRequest[AnyContentAsEmpty.type] = FakeRequest().withSession(SessionKeys.CLIENT_VRN -> vrn)
  lazy val injector: Injector = app.injector
  lazy val messagesApi: MessagesApi = injector.instanceOf[MessagesApi]
  implicit lazy val messages: Messages = MessagesImpl(Lang("en-GB"), messagesApi)
  implicit lazy val user: User[AnyContentAsEmpty.type] = User[AnyContentAsEmpty.type](vrn,active = true)(request)
  lazy val agentUserPrefYes: User[AnyContentAsEmpty.type] =
    User[AnyContentAsEmpty.type](vrn,active = true, Some(arn))(request.withSession(SessionKeys.verifiedAgentEmail -> agentEmail))
  lazy val agentUserPrefNo: User[AnyContentAsEmpty.type] = User[AnyContentAsEmpty.type](vrn,active = true, Some(arn))(request)
  implicit lazy val hc: HeaderCarrier = HeaderCarrier()
  implicit lazy val ec: ExecutionContext = injector.instanceOf[ExecutionContext]
  lazy val serviceErrorHandler: ServiceErrorHandler = injector.instanceOf[ServiceErrorHandler]
  lazy val defaultActionBuilder: DefaultActionBuilder = injector.instanceOf[DefaultActionBuilder]
}
