/*
 * Copyright 2022 HM Revenue & Customs
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

object CheckYourAnswersMessages extends BaseMessages {

  val title: String = "Check your answers" + titleSuffix
  val heading = "Check your answers"
  val change = "Change"
  val reason = "Reason for cancelling VAT registration"
  val reasonCeased = "Business stopped trading"
  val reasonBelowThreshold: String => String = threshold => s"Annual turnover is below £$threshold"
  val reasonZeroRated = "Supplies are mainly or only zero-rated for VAT"
  val reasonExemptOnly = "Making only VAT exempt supplies"
  val reasonOther = "Other"
  val ceasedTrading = "Date the business stopped or will stop trading"
  val taxableTurnover: String => String = threshold => s"Taxable turnover below £$threshold in last 12 months"
  val nextTaxableTurnover = "Expected taxable turnover for next 12 months"
  val whyBelow = "Why is turnover expected to be below threshold?"
  val vatAccounts = "How VAT accounts are prepared"
  val optionTax = "VAT charges on land or commercial property"
  val optionTaxValue = "Value of land or commercial property"
  val stocks = "Keeping stock"
  val stocksValue = "Value of stock"
  val capitalAssets = "Keeping capital assets"
  val capitalAssetsValue = "Value of capital assets"
  val outstandingInvoices = "Payment for invoices after cancelling registration"
  val newInvoices = "New invoices after cancelling registration"
  val chooseDeregDate = "Choose cancellation date"
  val deregistrationDate = "Cancellation date"
  val businessActivity = "Has the business activity changed?"
  val sicCodeValue = "Standard Industrial Classification (SIC) Code"
  val zeroRatedSuppliesValue = "Value of zero-rated supplies for next 12 months"
  val purchasesExceedSupplies = "Will VAT on purchases exceed VAT on supplies?"

  val reasonHidden = "Change the reason why the business is cancelling its VAT registration"
  val ceasedTradingHidden = "Change the date your business stopped or will stop trading"
  val taxableTurnoverHidden = "Change whether last year’s turnover was above or below the VAT threshold"
  val nextTaxableTurnoverHidden = "Change the business’s expected turnover for the next 12 months"
  val whyBelowHidden = "Change the reason why you expect the business’s turnover to be below the threshold"
  val vatAccountsHidden = "Change the accounting method the business uses to record VAT payments"
  val optionTaxHidden = "Change whether the business charged or claimed VAT on land or commercial property"
  val optionTaxValueHidden = "Change the value of the VAT charged or claimed on land or commercial property"
  val stocksHidden = "Change whether the business will keep any stock"
  val stocksValueHidden = "Change the value of the stock the business will keep"
  val capitalAssetsHidden = "Change whether the business will keep any capital assets"
  val capitalAssetsValueHidden = "Change the value of the capital assets the business will keep"
  val newInvoicesHidden = "Change whether the business will issue new invoices after cancelling VAT registration"
  val outstandingInvoiceHidden = "Change whether the business will receive payment for outstanding invoices after cancelling VAT registration"
  val chooseDeregDateHidden = "Change whether you want to change the cancellation date"
  val deregistrationDateHidden = "Change the cancellation date"
  val businessActivityHidden = "Change whether the business activity has changed"
  val sicCodeHidden = "Change the business’s Standard Industrial Classification (SIC) Code"
  val zeroRatedSuppliesHidden = "Change the value of zero-rated supplies for the next 12 months"
  val purchasesExceedSuppliesHidden = "Change whether the VAT on purchases will exceed the VAT on supplies"

  val noDate = "Not Specified"
  val standard = "Standard accounting"
  val cash = "Cash accounting"
  val confirm = "Confirm and cancel VAT registration"
}
