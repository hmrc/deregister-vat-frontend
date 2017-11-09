/*
 * Copyright 2017 HM Revenue & Customs
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

package config.features

import config.AppConfig

trait Feature {

  def conf: AppConfig

  def name: String
  def enabled: Boolean

  def switch(to: Boolean): Unit = {
//    sys.props.foreach(println)
    println(sys.props.get(name))

    sys.props += name -> to.toString
    println
    println
    println
    println
    println(sys.props.get(name))
    println("name: "+name)
    println(conf.authFeatureEnabled)
//    sys.props.foreach(println)

  }

}
