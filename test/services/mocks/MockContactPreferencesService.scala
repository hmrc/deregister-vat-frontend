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

package services.mocks

import models.contactPreferences.ContactPreference
import models.{ChangeIndicatorModel, CustomerDetails, ErrorModel}
import org.scalamock.scalatest.MockFactory
import services.{ContactPreferencesServices, CustomerDetailsService}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.test.UnitSpec

import scala.concurrent.ExecutionContext

trait MockContactPreferencesService extends UnitSpec with MockFactory {

  val mockContactPreferencesService: ContactPreferencesServices = mock[ContactPreferencesServices]

  def setupMockContactPreferences(vrn: String)(response: Either[ErrorModel, ContactPreference])(implicit hc: HeaderCarrier, ec: ExecutionContext): Unit = {
    (mockContactPreferencesService.getCustomerContactPreferences(_: String)(_: HeaderCarrier, _: ExecutionContext))
      .expects(vrn, *, *)
      .returns(response)
  }

}
