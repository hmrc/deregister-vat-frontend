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

import cats.data.EitherT
import cats.instances.future._
import config.AppConfig
import connectors.VatSubscriptionConnector
import javax.inject.Inject
import models.deregistrationRequest.DeregistrationInfo
import models._
import play.api.libs.json.Format
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
                                            val vatSubscriptionConnector: VatSubscriptionConnector)(implicit val appConfig: AppConfig) {


  def updateDereg(implicit user: User[_], fmt: Format[DeregistrationInfo], hc: HeaderCarrier, ec: ExecutionContext)
    : Future[Either[ErrorModel, VatSubscriptionResponse]] = {
    buildDeregInfoModel.flatMap{
      case Right(deregInfo) => vatSubscriptionConnector.submit(user.vrn, deregInfo)
      case Left(errorModel) => Future.successful(Left(errorModel))
    }
  }


  private def buildDeregInfoModel(implicit user: User[_], fmt: Format[DeregistrationInfo], hc: HeaderCarrier, ec: ExecutionContext)
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
        model <- EitherT(Future.successful(DeregistrationInfo.customApply(
          deregReason,
          ceasedTradingDate,
          taxableTurnover,
          nextTaxableTurnover,
          whyTurnoverBelow,
          accountingMethod,
          optionTax,
          stocks,
          capitalAssets,
          issueNewInvoices,
          outstandingInvoices,
          deregDate
        )))
      } yield model).value
  }
}
