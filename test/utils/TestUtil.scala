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

package utils

import mocks.MockAppConfig
import models.User
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.i18n.{Lang, Messages, MessagesApi}
import play.api.inject.Injector
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import uk.gov.hmrc.play.test._

trait TestUtil extends UnitSpec with GuiceOneAppPerSuite with BeforeAndAfterEach with MaterializerSupport {

  lazy implicit val mockConfig: MockAppConfig = new MockAppConfig(app.configuration)
  lazy implicit val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()
  lazy val injector: Injector = app.injector
  lazy val messagesApi: MessagesApi = injector.instanceOf[MessagesApi]
  implicit lazy val messages: Messages = Messages(Lang("en-GB"), messagesApi)
  implicit lazy val user: User[AnyContentAsEmpty.type] = User[AnyContentAsEmpty.type]("12344566789",true)(request)

}
