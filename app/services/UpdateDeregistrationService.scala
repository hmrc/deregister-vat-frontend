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

package services

import audit.AuditService
import audit.models.{AuditModel, DeregAuditModel}
import cats.data.EitherT
import cats.instances.future._
import config.AppConfig
import connectors.VatSubscriptionConnector
import javax.inject.Inject
import models._
import models.deregistrationRequest.DeregistrationInfo
import play.api.http.Status
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class UpdateDeregistrationService @Inject()(val deregReasonAnswerService: DeregReasonAnswerService,
                                            val ceasedTradingDateAnswerService: CeasedTradingDateAnswerService,
                                            val taxableTurnoverAnswerService: TaxableTurnoverAnswerService,
                                            val nextTaxableTurnoverAnswerService: NextTaxableTurnoverAnswerService,
                                            val whyTurnoverBelowAnswerService: WhyTurnoverBelowAnswerService,
                                            val accountingMethodAnswerService: AccountingMethodAnswerService,
                                            val optionTaxAnswerService: OptionTaxAnswerService,
                                            val stocksAnswerService: StocksAnswerService,
                                            val capitalAssetsAnswerService: CapitalAssetsAnswerService,
                                            val issueNewInvoicesAnswerService: IssueNewInvoicesAnswerService,
                                            val outstandingInvoicesAnswerService: OutstandingInvoicesAnswerService,
                                            val deregDateAnswerService: DeregDateAnswerService,
                                            val auditService: AuditService,
                                            val vatSubscriptionConnector: VatSubscriptionConnector)(implicit val appConfig: AppConfig) {


  def updateDereg(implicit user: User[_], hc: HeaderCarrier, ec: ExecutionContext)
    : Future[Either[ErrorModel, VatSubscriptionResponse]] = {
    buildDeregInfoModel.flatMap{
      case Right(deregInfo) =>
        auditService.auditEvent(
          DeregAuditModel(user, deregInfo),
          Some(controllers.routes.CheckAnswersController.show().url)
        )
        vatSubscriptionConnector.submit(user.vrn, deregInfo)
      case Left(errorModel) => Future.successful(Left(errorModel))
    }
  }


  private def buildDeregInfoModel(implicit user: User[_], hc: HeaderCarrier, ec: ExecutionContext)
    : Future[Either[ErrorModel, DeregistrationInfo]] = {
      (for {
        deregReason <- EitherT(deregReasonAnswerService.getAnswer)
        ceasedTradingDate <- EitherT(ceasedTradingDateAnswerService.getAnswer)
        taxableTurnover <- EitherT(taxableTurnoverAnswerService.getAnswer)
        nextTaxableTurnover <- EitherT(nextTaxableTurnoverAnswerService.getAnswer)
        whyTurnoverBelow <- EitherT(whyTurnoverBelowAnswerService.getAnswer)
        accountingMethod <- EitherT(accountingMethodAnswerService.getAnswer)
        optionTax <- EitherT(optionTaxAnswerService.getAnswer)
        stocks <- EitherT(stocksAnswerService.getAnswer)
        capitalAssets <- EitherT(capitalAssetsAnswerService.getAnswer)
        issueNewInvoices <- EitherT(issueNewInvoicesAnswerService.getAnswer)
        outstandingInvoices <- EitherT(outstandingInvoicesAnswerService.getAnswer)
        deregDate <- EitherT(deregDateAnswerService.getAnswer)
        deregReasonValue <- EitherT(mandatoryCheck(deregReason, "deregReason"))
        accountingMethodValue <- EitherT(mandatoryCheck(accountingMethod, "accountingMethod"))
        optionTaxValue <- EitherT(mandatoryCheck(optionTax, "optionTax"))
        stocksValue <- EitherT(mandatoryCheck(stocks, "stocks"))
        capitalAssetsValue <- EitherT(mandatoryCheck(capitalAssets, "capitalAssets"))
        issueNewInvoicesValue <- EitherT(mandatoryCheck(issueNewInvoices, "issueNewInvoices"))
        model = DeregistrationInfo.customApply(
          deregReasonValue,
          ceasedTradingDate,
          taxableTurnover,
          nextTaxableTurnover,
          whyTurnoverBelow,
          accountingMethodValue,
          optionTaxValue,
          stocksValue,
          capitalAssetsValue,
          issueNewInvoicesValue,
          outstandingInvoices,
          deregDate
        )
      } yield model).value
  }

  private def mandatoryCheck[T](field: Option[T], fieldName: String)(implicit ec: ExecutionContext)
  : Future[Either[ErrorModel, T]] = {
   Future.successful(field.fold[Either[ErrorModel, T]](
     Left(ErrorModel(Status.INTERNAL_SERVER_ERROR, s"Mandatory field of $fieldName was not retrieved from Mongo Store"))
   )(Right(_)))
  }
}
