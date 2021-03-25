/*
 * Copyright 2021 HM Revenue & Customs
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

package views.components

import config.AppConfig
import javax.inject.{Inject, Singleton}
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.footer.FooterItem

@Singleton
class FooterLinks @Inject()(implicit appConfig: AppConfig) {

  def cookiesLink()(implicit messages: Messages): FooterItem = FooterItem(
    Some(messages("footerLinks.cookies")),
    Some(appConfig.footerCookiesUrl)
  )

  def accessibilityLink()(implicit messages: Messages): FooterItem = FooterItem(
    Some(messages("footerLinks.accessibility")),
    Some(appConfig.accessibilityStatementUrl)
  )

  def privacyLink()(implicit messages: Messages): FooterItem = FooterItem(
    Some(messages("footerLinks.privacyPolicy")),
    Some(appConfig.footerPrivacyUrl)
  )

  def termsConditionsLink()(implicit messages: Messages): FooterItem = FooterItem(
    Some(messages("footerLinks.termsAndConditions")),
    Some(appConfig.footerTermsConditionsUrl)
  )

  def govukHelpLink()(implicit messages: Messages): FooterItem = FooterItem(
    Some(messages("footerLinks.help")),
    Some(appConfig.footerHelpUrl)
  )

  def items(implicit messages: Messages): Seq[FooterItem] = Seq(
    cookiesLink,
    accessibilityLink,
    privacyLink,
    termsConditionsLink,
    govukHelpLink
  )
}