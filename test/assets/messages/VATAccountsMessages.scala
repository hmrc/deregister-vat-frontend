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

package assets.messages

object VATAccountsMessages extends BaseMessages {

  val title = "How are the business’s VAT accounts prepared?" + titleSuffix
  val heading = "How are the business’s VAT accounts prepared?"
  val accountant = "If you have an accountant, ask them what accounting method they use."
  val standard = "Standard accounting"
  val invoice = "You record VAT whenever you send or receive an invoice"
  val cash = "Cash accounting"
  val payment = "You record VAT whenever you make or receive a payment"
  val p1 = "For example, you invoice someone in March but do not receive the money until June. If you record the VAT on this payment in:"
  val bullet1 = "March, then you use standard accounting"
  val bullet2 = "June, then you use cash accounting"

}
