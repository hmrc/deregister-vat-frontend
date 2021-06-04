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

package utils

import org.slf4j.{Logger, LoggerFactory}
import play.api.{LoggerLike, MarkerContext}

trait LoggerUtil {

  lazy val className: String = this.getClass.getSimpleName.stripSuffix("$")

  val logger = new LoggerLike {

    override val logger: Logger = LoggerFactory.getLogger(s"application.$className")

    override def debug(message: => String)(implicit mc: MarkerContext): Unit = super.debug(message)

    override def info(message: => String)(implicit mc: MarkerContext): Unit = super.info(message)

    override def warn(message: => String)(implicit mc: MarkerContext): Unit = super.warn(message)

  }
}
