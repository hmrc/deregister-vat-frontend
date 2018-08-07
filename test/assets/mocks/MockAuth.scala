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

package assets.mocks

import config.ServiceErrorHandler
import controllers.ControllerBaseSpec
import controllers.predicates.{AuthPredicate, AuthoriseAsAgent}
import services.EnrolmentsAuthService
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.{Retrieval, ~}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

trait MockAuth extends ControllerBaseSpec {

  def setup(authResponse: Future[_]): AuthPredicate = {
    val mockAuthConnector = mock[AuthConnector]

    (mockAuthConnector.authorise(_: Predicate, _: Retrieval[_])(_: HeaderCarrier, _: ExecutionContext))
      .expects(*, *, *, *)
      .returns(authResponse)

    val mockEnrolmentsAuthService: EnrolmentsAuthService = new EnrolmentsAuthService(mockAuthConnector)
    val serviceErrorHandler = mock[ServiceErrorHandler]
    val mockAuthoriseAsAgent = new AuthoriseAsAgent(mockEnrolmentsAuthService, serviceErrorHandler, messagesApi, mockConfig)
    new AuthPredicate(mockEnrolmentsAuthService, serviceErrorHandler, mockAuthoriseAsAgent, messagesApi, mockConfig)
  }

  val mockAuthorisedIndividual =
    new ~(Some(AffinityGroup.Individual),
      Enrolments(Set(Enrolment("HMRC-MTD-VAT",
        Seq(EnrolmentIdentifier("VRN", "999999999")),
        "Activated"
      )))
    )
}
