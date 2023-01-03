/*
 * Copyright 2023 HM Revenue & Customs
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

object PurchasesExceedSuppliesMessages extends BaseMessages {

  val heading = "Do you expect the VAT on purchases to regularly exceed the VAT on supplies?"
  val title = heading + titleSuffix
  val explanation = "This means the business would claim the VAT on most VAT Returns."
  val purchasesExceedSuppliesError = "Select yes if you expect VAT on purchases to be more than VAT on supplies"

}
