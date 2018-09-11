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

package models

import play.api.libs.json.{Format, Json}


case class DeregCheckYourAnswersModel(singleAnswers: Seq[SingleAnswerModel])

object DeregCheckYourAnswersModel {

  def customApply(accounting: Option[VATAccountsModel],
                  capital: Option[YesNoAmountModel],
                  ceased: Option[DateModel],
                  deregDate: Option[DeregistrationDateModel],
                  deregReason: Option[DeregistrationReason],
                  nextTurnover: Option[TaxableTurnoverModel],
                  optionTax: Option[YesNoAmountModel],
                  owesMoney: Option[YesNo],
                  stocks: Option[YesNoAmountModel],
                  turnover: Option[TaxableTurnoverModel],
                  whyBelow: Option[WhyTurnoverBelowModel]): DeregCheckYourAnswersModel = {
    DeregCheckYourAnswersModel(Seq(
      SingleAnswerModel.customApply("",accounting,""),
      SingleAnswerModel.customApply("",capital,""),
      SingleAnswerModel.customApply("",ceased,""),
      SingleAnswerModel.customApply("",deregDate,""),
      SingleAnswerModel.customApply("",deregReason,""),
      SingleAnswerModel.customApply("",nextTurnover,""),
      SingleAnswerModel.customApply("",optionTax,""),
      SingleAnswerModel.customApply("",owesMoney,""),
      SingleAnswerModel.customApply("",stocks,""),
      SingleAnswerModel.customApply("",turnover,""),
      SingleAnswerModel.customApply("",whyBelow,"")
    ))
  }

  implicit val format: Format[DeregCheckYourAnswersModel] = Json.format[DeregCheckYourAnswersModel]

}

//case class DeregCheckYourAnswersModel(accounting: Option[VATAccountsModel],
//                                      capital: Option[YesNoAmountModel],
//                                      ceased: Option[DateModel],
//                                      deregDate: Option[DeregistrationDateModel],
//                                      deregReason: Option[DeregistrationReason],
//                                      nextTurnover: Option[TaxableTurnoverModel],
//                                      optionTax: Option[YesNoAmountModel],
//                                      owesMoney: Option[YesNo],
//                                      stocks: Option[YesNoAmountModel],
//                                      turnover: Option[TaxableTurnoverModel],
//                                      whyBelow: Option[WhyTurnoverBelowModel]) {

//case class DeregCheckYourAnswersModel(accounting: Option[SingleAnswerModel],
//                                      capital: Option[SingleAnswerModel],
//                                      ceased: Option[SingleAnswerModel],
//                                      deregDate: Option[SingleAnswerModel],
//                                      deregReason: Option[SingleAnswerModel],
//                                      nextTurnover: Option[SingleAnswerModel],
//                                      optionTax: Option[SingleAnswerModel],
//                                      owesMoney: Option[SingleAnswerModel],
//                                      stocks: Option[SingleAnswerModel],
//                                      turnover: Option[SingleAnswerModel],
//                                      whyBe