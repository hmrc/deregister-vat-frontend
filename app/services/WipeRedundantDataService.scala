/*
 * Copyright 2024 HM Revenue & Customs
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
                                         val chooseDateAnswer: ChooseDeregDateAnswerService,
                                         val deregDateAnswer: DeregDateAnswerService,
                                         val businessActivityAnswer: BusinessActivityAnswerService,
                                         val zeroRatedSuppliesValueService: ZeroRatedSuppliesValueService,
                                         val purchaseVatExceedSupplyVatAnswer: PurchasesExceedSuppliesAnswerService,
                                         val sicCodeAnswer: SicCodeAnswerService) {


  def wipeRedundantData(implicit user: User[_], hc: HeaderCarrier, ec: ExecutionContext): Future[Either[ErrorModel, DeregisterVatResponse]] = {
    (for {
      deregReason <- EitherT(deregReasonAnswer.getAnswer)
      capitalAssets <- EitherT(capitalAssetsAnswer.getAnswer)
      issueInvoices <- EitherT(invoicesAnswer.getAnswer)
      outstandingInvoices <- EitherT(outstandingInvoicesAnswer.getAnswer)
      businessActivity <- EitherT(businessActivityAnswer.getAnswer)
      chooseDate <- EitherT(chooseDateAnswer.getAnswer)
      _ <- EitherT(wipeRedundantDeregReasonJourneyData(deregReason))
      _ <- EitherT(wipeOutstandingInvoices(issueInvoices))
      _ <- EitherT(wipeDeregDate(deregReason, capitalAssets, issueInvoices, outstandingInvoices))
      _ <- EitherT(wipeSicCode(businessActivity))
      _ <- EitherT(wipeChosenDate(chooseDate))
    } yield DeregisterVatSuccess).value
  }

  private[services] def wipeRedundantDeregReasonJourneyData(reason: Option[DeregistrationReason])
                                                           (implicit user: User[_], hc: HeaderCarrier, ec: ExecutionContext)
  : Future[Either[ErrorModel, DeregisterVatResponse]] = {
    reason match {
      case Some(Ceased) =>  wipeDataReadyForCeasedTradingJourney
      case Some(BelowThreshold) => wipeDataReadyForBelowThresholdJourney
      case Some(ZeroRated) => wipeDataReadyForZeroRatedJourney
      case Some(ExemptOnly) => wipeDataReadyForExemptOnlyJourney
      case _ => Future.successful(Right(DeregisterVatSuccess))
    }
  }

  private[services] def wipeDataReadyForCeasedTradingJourney(implicit user: User[_], hc: HeaderCarrier, ec: ExecutionContext)
  : Future[Either[ErrorModel, DeregisterVatResponse]] = {
    (for {
      _ <- EitherT(taxableTurnoverAnswer.deleteAnswer)
      _ <- EitherT(whyTurnoverBelow.deleteAnswer)
      _ <- EitherT(nextTaxableTurnoverAnswer.deleteAnswer)
      _ <- EitherT(businessActivityAnswer.deleteAnswer)
      _ <- EitherT(sicCodeAnswer.deleteAnswer)
      _ <- EitherT(zeroRatedSuppliesValueService.deleteAnswer)
      _ <- EitherT(purchaseVatExceedSupplyVatAnswer.deleteAnswer)
    } yield DeregisterVatSuccess).value
  }

  private[services] def wipeDataReadyForBelowThresholdJourney(implicit user: User[_], hc: HeaderCarrier, ec: ExecutionContext)
  : Future[Either[ErrorModel, DeregisterVatResponse]] = {
    (for {
      _ <- EitherT(ceasedTradingDateAnswer.deleteAnswer)
      _ <- EitherT(businessActivityAnswer.deleteAnswer)
      _ <- EitherT(sicCodeAnswer.deleteAnswer)
      _ <- EitherT(zeroRatedSuppliesValueService.deleteAnswer)
      _ <- EitherT(purchaseVatExceedSupplyVatAnswer.deleteAnswer)
    } yield DeregisterVatSuccess).value
  }

  private[services] def wipeDataReadyForZeroRatedJourney(implicit user: User[_], hc: HeaderCarrier, ec: ExecutionContext)
  : Future[Either[ErrorModel, DeregisterVatResponse]] = {
    (for{
      _ <- EitherT(ceasedTradingDateAnswer.deleteAnswer)
      _ <- EitherT(whyTurnoverBelow.deleteAnswer)
      _ <- EitherT(taxableTurnoverAnswer.deleteAnswer)

    } yield DeregisterVatSuccess).value
  }

  private[services] def wipeDataReadyForExemptOnlyJourney(implicit user: User[_], hc: HeaderCarrier, ec: ExecutionContext)
  : Future[Either[ErrorModel, DeregisterVatResponse]] = {
    (for {
      _ <- EitherT(taxableTurnoverAnswer.deleteAnswer)
      _ <- EitherT(ceasedTradingDateAnswer.deleteAnswer)
      _ <- EitherT(whyTurnoverBelow.deleteAnswer)
      _ <- EitherT(nextTaxableTurnoverAnswer.deleteAnswer)
      _ <- EitherT(businessActivityAnswer.deleteAnswer)
      _ <- EitherT(sicCodeAnswer.deleteAnswer)
      _ <- EitherT(zeroRatedSuppliesValueService.deleteAnswer)
      _ <- EitherT(purchaseVatExceedSupplyVatAnswer.deleteAnswer)
    } yield DeregisterVatSuccess).value
  }

  private[services] def wipeOutstandingInvoices(invoicesAnswer: Option[YesNo])
                                               (implicit user: User[_], hc: HeaderCarrier, ec: ExecutionContext)
  : Future[Either[ErrorModel, DeregisterVatResponse]] = {
    invoicesAnswer match {
      case Some(Yes) => outstandingInvoicesAnswer.deleteAnswer
      case _ => Future.successful(Right(DeregisterVatSuccess))
    }
  }

  private[services] def wipeSicCode(businessActivityAnswerService: Option[YesNo])
                                               (implicit user: User[_], hc: HeaderCarrier, ec: ExecutionContext)
  : Future[Either[ErrorModel, DeregisterVatResponse]] = {
    businessActivityAnswerService match {
      case Some(No) => sicCodeAnswer.deleteAnswer
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
      case (Some(Ceased), Some(x), Some(No), Some(No)) if x.yesNo == No =>
        (for {
          _ <- EitherT(chooseDateAnswer.deleteAnswer)
          _ <- EitherT(deregDateAnswer.deleteAnswer)
        } yield DeregisterVatSuccess).value
      case _ => Future.successful(Right(DeregisterVatSuccess))
    }
  }

  private[services] def wipeChosenDate(chooseDate: Option[YesNo])
                                      (implicit user: User[_], hc: HeaderCarrier, ec: ExecutionContext)
  : Future[Either[ErrorModel, DeregisterVatResponse]] = {
    chooseDate match {
      case Some(No) => deregDateAnswer.deleteAnswer
      case _ => Future.successful(Right(DeregisterVatSuccess))
    }
  }

}
