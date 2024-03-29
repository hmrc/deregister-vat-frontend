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

package assets.constants

import models.{No, NumberInputModel, Yes, YesNoAmountModel}

object YesNoAmountTestConstants {

  val stockValue = 1000.0
  val ottValue = 2000.0
  val assetsValue = 3000.50
  val sicCodeValue = "12345"
  val zeroRatedSuppliesValue = NumberInputModel(4000.00)
  val thresholdValue = "83,000"

  val stocksModel: YesNoAmountModel = YesNoAmountModel(Yes,Some(BigDecimal(stockValue)))
  val ottModel: YesNoAmountModel = YesNoAmountModel(Yes,Some(BigDecimal(ottValue)))
  val assetsModel: YesNoAmountModel = YesNoAmountModel(Yes,Some(BigDecimal(assetsValue)))
  val yesNoAmountNo: YesNoAmountModel = YesNoAmountModel(No,None)

}
