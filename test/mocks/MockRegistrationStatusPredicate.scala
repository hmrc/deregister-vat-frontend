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

package mocks

import assets.constants.BaseTestConstants.vrn
import connectors.mocks.MockVatSubscriptionConnector
import controllers.predicates.RegistrationStatusPredicate
import models.User
import org.scalamock.scalatest.MockFactory
import play.api.mvc.Result
import services.CustomerDetailsService
import utils.TestUtil

import scala.concurrent.Future

trait MockRegistrationStatusPredicate extends TestUtil with MockFactory with MockVatSubscriptionConnector {

  val mockRegistrationStatusPredicate: RegistrationStatusPredicate = {

    object MockPredicate extends RegistrationStatusPredicate(
      new CustomerDetailsService(mockVatSubscriptionConnector),
      serviceErrorHandler,
      messagesApi,
      mockConfig,
      ec
    ) {
      override def refine[A](request: User[A]): Future[Either[Result, User[A]]] =
        Future.successful(Right(User(vrn)(request)))
    }

    MockPredicate
  }

}
