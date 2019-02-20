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

package controllers

import config.{AppConfig, ConfigKeys, ServiceErrorHandler}
import controllers.predicates.AuthPredicate
import javax.inject.Inject
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent}
import services.{ContactPreferencesService, CustomerDetailsService, DeleteAllStoredAnswersService}
import testOnly.views.html.featureSwitch
import uk.gov.hmrc.play.bootstrap.controller.FrontendController

class DeregistrationConfirmationController @Inject()(val messagesApi: MessagesApi,
                                                     val authentication: AuthPredicate,
                                                     val deleteAllStoredAnswersService: DeleteAllStoredAnswersService,
                                                     val serviceErrorHandler: ServiceErrorHandler,
                                                     val customerDetailsService: CustomerDetailsService,
                                                     implicit val customerContactPreference: ContactPreferencesService,
                                                     implicit val appConfig: AppConfig) extends FrontendController with I18nSupport {

  val show: Action[AnyContent] = authentication.async { implicit user =>

    if(appConfig.features.useContactPreferences()){

      for {
        deleteAllAStoredAnswers <- deleteAllStoredAnswersService.deleteAllAnswers
        customerDetails <- customerDetailsService.getCustomerDetails(user.vrn)
        contactPreference <- customerContactPreference.getCustomerContactPreferences(user.vrn)
      } yield (deleteAllAStoredAnswers, customerDetails, contactPreference) match {
        case (Left(_),_,_) =>
          serviceErrorHandler.showInternalServerError
        case (_, Right(custDetails), Right(custPreference)) =>
            Ok(views.html.deregistrationConfirmation(custDetails.businessName,Some(custPreference.preference)))
        case (_, _, Right(pref)) =>
          Ok(views.html.deregistrationConfirmation(preference = Some(pref.preference)))
        case _ =>
          Ok(views.html.deregistrationConfirmation())
      }
    } else {
      for {
        deleteAllAStoredAnswers <- deleteAllStoredAnswersService.deleteAllAnswers
        customerDetails <- customerDetailsService.getCustomerDetails(user.vrn)
      } yield (deleteAllAStoredAnswers, customerDetails) match {
        case (Left(_), _) =>
          serviceErrorHandler.showInternalServerError
        case (_, Right(custDetails)) =>
          Ok(views.html.deregistrationConfirmation(custDetails.businessName))
        case _ =>
          Ok(views.html.deregistrationConfirmation(None))
      }
    }
  }
}
