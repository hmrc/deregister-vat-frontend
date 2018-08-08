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

package mocks

import common.EnrolmentKeys
import org.scalamock.scalatest.MockFactory
import services.EnrolmentsAuthService
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.Retrieval
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.http.HeaderCarrier
import assets.constants.BaseTestConstants.vrn

import scala.concurrent.{ExecutionContext, Future}

trait MockAuth extends MockFactory {

  lazy val mockAuthConnector: AuthConnector = mock[AuthConnector]

  def mockAuthResult[A](authResult: Future[A]) {
    (mockAuthConnector.authorise(_: Predicate, _: Retrieval[A])(_: HeaderCarrier, _: ExecutionContext))
      .expects(*, *, *, *)
      .returns(authResult)
  }

  lazy val mockAuthorisedFunctions: AuthorisedFunctions = new EnrolmentsAuthService(mockAuthConnector)

  val individualAuthorised: Enrolments = Enrolments(
    Set(
      Enrolment(
        EnrolmentKeys.vatEnrolmentId,
        Seq(EnrolmentIdentifier(EnrolmentKeys.vatIdentifierId, vrn)), "Active")
    )
  )

}

