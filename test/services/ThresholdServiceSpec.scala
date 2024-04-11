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

package services


import config.FrontendAppConfig
import models.VatThreshold
import org.mockito.Mockito.{reset, when}
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.mvc.Request
import play.api.test.FakeRequest
import uk.gov.hmrc.http.InternalServerException
import utils.TestUtil

import java.time.LocalDateTime

class ThresholdServiceSpec extends TestUtil with BeforeAndAfterEach {

  val mockConf: FrontendAppConfig = mock[FrontendAppConfig]

  val req: Request[_] = FakeRequest()

  class TestThresholdService(fakeNow: LocalDateTime) extends ThresholdService()(mockConf) {
    override def now: LocalDateTime = fakeNow
  }

  def service(fakeNow: LocalDateTime = LocalDateTime.now()): TestThresholdService = new TestThresholdService(fakeNow)

  val dateTime: LocalDateTime = LocalDateTime.of(2017, 4, 1, 0, 0, 0)
  val amount: BigDecimal = BigDecimal(83000)
  val testThreshold: VatThreshold = VatThreshold(dateTime, amount)

  val dateTime2: LocalDateTime = LocalDateTime.of(2024, 3, 31, 23, 0, 0)
  val amount2: BigDecimal = BigDecimal(88000)
  val testThreshold2: VatThreshold = VatThreshold(dateTime2, amount2)

  override def afterEach(): Unit = {
    reset(mockConf)
  }


  "getVatThreshold" when {

    "there is a single threshold in config after today" must {

      "return the threshold" in {

        when(mockConf.thresholds).thenReturn(Seq(testThreshold))

        val expectedResult = Some(testThreshold)
        val actualResult = service().getVatThreshold()

        expectedResult shouldBe actualResult
      }
    }

    "there are multiple thresholds in config before today" must {

      "return the latest threshold" in {

        when(mockConf.thresholds).thenReturn(Seq(testThreshold, testThreshold2))

        val expectedResult = Some(testThreshold2)
        val actualResult = service(dateTime2.plusDays(1)).getVatThreshold()

        expectedResult shouldBe actualResult
      }
    }

    "there are multiple thresholds in config but only one before today" must {

      "return the threshold in the past" in {

        when(mockConf.thresholds).thenReturn(Seq(testThreshold, testThreshold2))

        val expectedResult = Some(testThreshold)
        val actualResult = service(dateTime2.minusDays(1)).getVatThreshold()

        expectedResult shouldBe actualResult
      }
    }

    "there are no thresholds in config before today" must {

      "return the threshold in the past" in {

        when(mockConf.thresholds).thenReturn(Seq(testThreshold, testThreshold2))

        val expectedResult = None
        val actualResult = service(dateTime.minusDays(1)).getVatThreshold()

        expectedResult shouldBe actualResult
      }
    }

    "there are no thresholds in config" must {

      "return the threshold in the past" in {

        when(mockConf.thresholds).thenReturn(Seq())

        val expectedResult = None
        val actualResult = service().getVatThreshold

        expectedResult shouldBe actualResult
      }
    }
  }

  "formattedVatThreshold" when {

    "getVatThreshold returns a value" must {

      "return a formatted amount" in {

        when(mockConf.thresholds).thenReturn(Seq(testThreshold, testThreshold2))

        val expectedResult = "88,000"
        val actualResult = service(dateTime2.plusDays(1)).formattedVatThreshold()(req)

        expectedResult shouldBe actualResult
      }
    }

    "getVatThreshold does not return a value" must {

      "throw ISE" in {

        when(mockConf.thresholds).thenReturn(Seq())

        intercept[InternalServerException](service().formattedVatThreshold()(req))

      }
    }
  }
}