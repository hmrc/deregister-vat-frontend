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

package mocks

import assets.constants.BaseTestConstants.{arn, vrn}
import controllers.predicates.{AuthPredicate, AuthoriseAsAgent}
import org.scalamock.scalatest.MockFactory
import services.EnrolmentsAuthService
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.{Retrieval, ~}
import uk.gov.hmrc.http.HeaderCarrier
import utils.TestUtil
import views.html.errors.client.Unauthorised

import scala.concurrent.{ExecutionContext, Future}

trait MockAuth extends TestUtil with MockFactory {

  type AuthResponse = Future[~[Option[AffinityGroup], Enrolments]]
  lazy val mockAuthConnector: AuthConnector = mock[AuthConnector]
  lazy val unauthorised: Unauthorised = injector.instanceOf[Unauthorised]

  lazy val mockEnrolmentsAuthService: EnrolmentsAuthService = new EnrolmentsAuthService(mockAuthConnector)
  lazy val mockAuthoriseAsAgent: AuthoriseAsAgent = new AuthoriseAsAgent(mockEnrolmentsAuthService, serviceErrorHandler, mcc, mockConfig)
  lazy val mockAuthPredicate: AuthPredicate = new AuthPredicate(unauthorised,
                                                                mockEnrolmentsAuthService,
                                                                serviceErrorHandler,
                                                                mockAuthoriseAsAgent,
                                                                mcc,
                                                                mockConfig)

  def mockAuthResult(authResponse: AuthResponse, isAgent: Boolean = false): Unit = {

    (mockAuthConnector.authorise(_: Predicate, _: Retrieval[_])(_: HeaderCarrier, _: ExecutionContext))
      .expects(*, *, *, *)
      .returns(authResponse)

    if (isAgent) {
      (mockAuthConnector.authorise(_: Predicate, _: Retrieval[_])(_: HeaderCarrier, _: ExecutionContext))
        .expects(*, *, *, *)
        .returns(authResponse.b)
    }
  }

  val mockAuthorisedIndividual: AuthResponse = Future.successful(
    new ~(Some(AffinityGroup.Individual),
      Enrolments(Set(Enrolment("HMRC-MTD-VAT",
        Seq(EnrolmentIdentifier("VRN", vrn)),
        "Activated"
      )))
    )
  )

  val mockAuthorisedAgent: AuthResponse = Future.successful(
    new ~(Some(AffinityGroup.Agent),
      Enrolments(
        Set(
          Enrolment(
            "HMRC-AS-AGENT",
            Seq(EnrolmentIdentifier("AgentReferenceNumber", arn)),
            "Activated",
            Some("mtd-vat-auth")
          )
        )
      )
    )
  )

  val mockUnauthorisedIndividual: AuthResponse = Future.successful(
    new ~(Some(AffinityGroup.Individual),
      Enrolments(
        Set(
          Enrolment(
            "OTHER-ENROLMENT",
            Seq(EnrolmentIdentifier("", "")),
            "Activated"
          )
        )
      )
    )
  )

  val mockUnauthorisedAgent: AuthResponse = Future.successful(
    new ~(Some(AffinityGroup.Agent),
      Enrolments(
        Set(
          Enrolment(
            "OTHER-ENROLMENT",
            Seq(EnrolmentIdentifier("", "")),
            "Activated"
          )
        )
      )
    )
  )

  val mockNoAffinityGroup: AuthResponse = Future.successful(
    new ~(None,
      Enrolments(
        Set()
      )
    )
  )
}

