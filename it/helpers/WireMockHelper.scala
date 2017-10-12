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

package helpers

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.core.WireMockConfiguration._
import org.scalatest.concurrent.{Eventually, IntegrationPatience}
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.libs.ws.WSClient

object WireMockHelper extends Eventually with IntegrationPatience {

  val wmPort: Int = 11111
  val host: String = "localhost"

  def verifyGet(uri: String): Unit = {
    verify(getRequestedFor(urlEqualTo(uri)))
  }

  def verifyPost(uri: String, optBody: Option[String] = None): Unit = {
    val uriMapping = postRequestedFor(urlEqualTo(uri))
    val postRequest = optBody match {
      case Some(body) => uriMapping.withRequestBody(equalTo(body))
      case None => uriMapping
    }
    verify(postRequest)
  }
}

trait WireMockHelper {

  self: GuiceOneServerPerSuite =>

  import WireMockHelper._

  lazy val wsClient: WSClient = app.injector.instanceOf[WSClient]
  lazy val wmConfig: WireMockConfiguration = wireMockConfig.port(wmPort)
  lazy val wmServer: WireMockServer = new WireMockServer(wmConfig)

  def startWireMock(): Unit = {
    wmServer.start()
    WireMock.configureFor(host, wmPort)
  }

  def stopWireMock(): Unit = wmServer.stop()

  def resetWireMock(): Unit = WireMock.reset()
}
