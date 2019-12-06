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

package services

import cats.data.EitherT
import cats.instances.future._
import com.google.inject.{Inject, Singleton}
import models._
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

@Singleton()
class WipeRedundantDataService @Inject()(val deregReasonAnswer: DeregReasonAnswerService,
                                         val ceasedTradingDateAnswer: CeasedTradingDateAnswerService,
                                         val capitalAssetsAnswer: CapitalAssetsAnswerService,
                                         val taxableTurnoverAnswer: TaxableTurnoverAnswerService,
                                         val nextTaxableTurnoverAnswer: NextTaxableTurnoverAnswerService,
                                         val invoicesAnswer: IssueNewInvoicesAnswerService,
                                         val outstandingInvoicesAnswer: OutstandingInvoicesAnswerService,
                                         val whyTurnoverBelow: WhyTurnoverBelowAnswerService,
                                         val deregDateAnswer: DeregDateAnswerService,
                                         val businessActivityAnswer: BusinessActivityAnswerService,
                                         val nextTaxableTurnoverZeroRatedAnswer: NextTaxableTurnoverZeroRatedAnswerService,
                                         val purchaseVatExceedSupplyVatAnswer: PurchaseVatExceedSupplyVatAnswerService,
                                         val sicCodeAnswerService: SICCodeAnswerService) {


  def wipeRedundantData(implicit user: User[_], hc: HeaderCarrier, ec: ExecutionContext): Future[Either[ErrorModel, DeregisterVatResponse]] = {
    (for {
      deregReason <- EitherT(deregReasonAnswer.getAnswer)
      capitalAssets <- EitherT(capitalAssetsAnswer.getAnswer)
      issueInvoices <- EitherT(invoicesAnswer.getAnswer)
      outstandingInvoices <- EitherT(outstandingInvoicesAnswer.getAnswer)
      taxableTurnover <- EitherT(taxableTurnoverAnswer.getAnswer)
      _ <- EitherT(wipeRedundantDeregReasonJourneyData(deregReason))
      _ <- EitherT(wipeOutstandingInvoices(issueInvoices))
      _ <- EitherT(wipeDeregDate(deregReason, capitalAssets, issueInvoices, outstandingInvoices))
      _ <- EitherT(wipeNext12MonthsBelow(taxableTurnover))
    } yield DeregisterVatSuccess).value
  }

  private[services] def wipeRedundantDeregReasonJourneyData(reason: Option[DeregistrationReason])
                                                           (implicit user: User[_], hc: HeaderCarrier, ec: ExecutionContext)
  : Future[Either[ErrorModel, DeregisterVatResponse]] = {
    reason match {
      case Some(Ceased) => wipeBelowThresholdJourney
      case Some(BelowThreshold) => ceasedTradingDateAnswer.deleteAnswer
      case Some(ZeroRated) => ???
      case _ => Future.successful(Right(DeregisterVatSuccess))
    }
  }

  private[services] def wipeBelowThresholdJourney(implicit user: User[_], hc: HeaderCarrier, ec: ExecutionContext)
  : Future[Either[ErrorModel, DeregisterVatResponse]] = {
    (for {
      _ <- EitherT(whyTurnoverBelow.deleteAnswer)
      _ <- EitherT(taxableTurnoverAnswer.deleteAnswer)
      _ <- EitherT(nextTaxableTurnoverAnswer.deleteAnswer)
    } yield DeregisterVatSuccess).value
  }

  private[services] def wipeZeroRatedJourney(implicit user: User[_], hc: HeaderCarrier, ec: ExecutionContext)
  : Future[Either[ErrorModel, DeregisterVatResponse]] = {
    ???
  }

  private[services] def wipeOutstandingInvoices(invoicesAnswer: Option[YesNo])
                                               (implicit user: User[_], hc: HeaderCarrier, ec: ExecutionContext)
  : Future[Either[ErrorModel, DeregisterVatResponse]] = {
    invoicesAnswer match {
      case Some(Yes) => outstandingInvoicesAnswer.deleteAnswer
      case _ => Future.successful(Right(DeregisterVatSuccess))
    }
  }

  private[services] def wipeDeregDate(reason: Option[DeregistrationReason],
                                      capitalAssets: Option[YesNoAmountModel],
                                      issueInvoices: Option[YesNo],
                                      outstandingInvoices: Option[YesNo])
                                     (implicit user: User[_], hc: HeaderCarrier, ec: ExecutionContext)
  : Future[Either[ErrorModel, DeregisterVatResponse]] = {
    (reason, capitalAssets, issueInvoices, outstandingInvoices) match {
      case (Some(Ceased), Some(x), Some(No), Some(No)) if x.yesNo == No => deregDateAnswer.deleteAnswer
      case _ => Future.successful(Right(DeregisterVatSuccess))
    }
  }

  private[services] def wipeNext12MonthsBelow(belowPast12Months: Option[YesNo])(implicit user: User[_], hc: HeaderCarrier, ec: ExecutionContext)
  : Future[Either[ErrorModel, DeregisterVatResponse]] = {
    belowPast12Months match {
      case Some(Yes) => whyTurnoverBelow.deleteAnswer
      case _ => Future.successful(Right(DeregisterVatSuccess))
    }
  }
}
