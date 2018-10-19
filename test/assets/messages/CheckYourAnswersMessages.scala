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

package assets.messages

object CheckYourAnswersMessages {

  val title = "Check your answers"
  val change = "Change"
  val reason = "Why the business is deregistering from VAT"
  val reasonCeased = "It has stopped trading"
  val reasonBelowThreshold: String => String = threshold => s"Annual turnover is below £$threshold"
  val reasonOther = "Other"
  val ceasedTrading = "The date your business ceased or will cease trading"
  val taxableTurnover: String => String = threshold => s"Was the business’s taxable turnover in the last 12 months below £$threshold?"
  val nextTaxableTurnover = "What is the business’s expected taxable turnover for the next 12 months?"
  val whyBelow = "Why does the business expect its turnover to be below the threshold?"
  val vatAccounts = "How are the business’s VAT accounts prepared?"
  val optionTax = "Did the business charge VAT on land or commercial property?"
  val optionTaxValue = "What was the total value of the land or commercial property?"
  val stocks = "Does the business intend to keep any stock?"
  val stocksValue = "What is the total value of the stock?"
  val capitalAssets = "Does the business intend to keep any capital assets?"
  val capitalAssetsValue = "What is the total value of capital assets?"
  val outstandingInvoices = "Is the business expecting to receive payment for outstanding invoices after deregistering?"
  val newInvoices = "Is the business expecting to issue any new invoices after deregistering?"
  val deregistrationDate = "Deregistration date"

  val reasonHidden = "Change the reason why the business is deregistering from VAT"
  val ceasedTradingHidden = "Change the date your business stopped or will stop trading"
  val taxableTurnoverHidden = "Change whether last year's turnover was above or below the VAT threshold"
  val nextTaxableTurnoverHidden = "Change the business's expected turnover for the next 12 months"
  val whyBelowHidden = "Change the reason why your expect the business's turnover to be below the threshold"
  val vatAccountsHidden = "The accounting method the business uses to record VAT payments"
  val optionTaxHidden = "Change whether the business charged or claimed VAT on land or commercial property"
  val optionTaxValueHidden = "Change the value of the VAT charged or claimed on land or commercial property"
  val stocksHidden = "Change whether the business will keep any stock"
  val stocksValueHidden = "Change the value of the stock the business will keep"
  val capitalAssetsHidden = "Change whether the business will keep any capital assets"
  val capitalAssetsValueHidden = "Change the value of the capital assets the business will keep"
  val newInvoicesHidden = "Change whether the business will issue new invoices after deregistering"
  val outstandingInvoiceHidden = "Change whether the business will receive payment for outstanding invoices after deregistering"
  val deregistrationDateHidden = "Change whether the business wants to choose its own deregistration date"

  val noDate = "Not Specified"
  val standard = "Standard accounting"
  val cash = "Cash accounting"
  val text = "By confirming this change, you agree that the information you have given is complete and correct."
  val confirm = "Confirm and deregister"
}
