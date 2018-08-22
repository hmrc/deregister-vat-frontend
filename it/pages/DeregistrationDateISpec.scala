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

package pages

import java.time.LocalDate

import forms.DeregistrationDateForm
import helpers.IntegrationBaseSpec
import models.{DateModel, DeregistrationDateModel, No, Yes}
import play.api.http.Status._
import play.api.libs.ws.WSResponse

class DeregistrationDateISpec extends IntegrationBaseSpec {

  "Calling the GET Deregistration Date endpoint" when {

    def getRequest(): WSResponse = get("/deregistration-date")

    "the user is authorised" should {

      "return 200 OK" in {

        given.user.isAuthorised

        val response: WSResponse = getRequest()

        response should have(
          httpStatus(OK),
          pageTitle("Does the business want to choose its own deregistration date?")
        )
      }
    }

    "the user is not authenticated" should {

      "return 401 UNAUTHORIZED" in {

        given.user.isNotAuthenticated

        val response: WSResponse = getRequest()

        response should have(
          httpStatus(UNAUTHORIZED),
          pageTitle("Your session has timed out")
        )
      }
    }

    "the user is not authorised" should {

      "return 403 FORBIDDEN" in {

        given.user.isNotAuthorised

        val response: WSResponse = getRequest()

        response should have(
          httpStatus(FORBIDDEN),
          pageTitle("You can’t use this service yet")
        )
      }
    }
  }


  "Calling the POST Deregistration Date endpoint" when {

    def postRequest(data: DeregistrationDateModel): WSResponse =
      post("/deregistration-date")(toFormData(DeregistrationDateForm.deregistrationDateForm, data))

    val validYesModel = DeregistrationDateModel(Yes,Some(DateModel(
      LocalDate.now.getDayOfMonth,
      LocalDate.now.getMonthValue,
      LocalDate.now.getYear
    )))
    val validNoModel = DeregistrationDateModel(No,None)
    val invalidYesModel = DeregistrationDateModel(Yes,None)


    "the user is authorised" when {

      "the post request includes valid Yes data" should {

        "return 303 SEE_OTHER" in {

          given.user.isAuthorised

          val response: WSResponse = postRequest(validYesModel)

          response should have(
            httpStatus(SEE_OTHER),
            redirectURI(controllers.routes.CheckAnswersController.show().url)
          )
        }
      }

      "the post request includes valid No data" should {

        "return 303 SEE_OTHER" in {

          given.user.isAuthorised

          val response: WSResponse = postRequest(validNoModel)

          response should have(
            httpStatus(SEE_OTHER),

            //TODO: Redirect needs updating as part of the routing Sub-Task
            redirectURI(controllers.routes.CheckAnswersController.show().url)
          )
        }
      }

      "the post request includes invalid Yes data" should {

        "return 400 BAD_REQUEST" in {

          given.user.isAuthorised

          val response: WSResponse = postRequest(invalidYesModel)

          response should have(
            httpStatus(BAD_REQUEST),
            pageTitle("Does the business want to choose its own deregistration date?"),
            elementText(".error-message")("Invalid date")
          )
        }
      }
    }

    "the user is not authenticated" should {

      "return 401 UNAUTHORIZED" in {

        given.user.isNotAuthenticated

        val response: WSResponse = postRequest(validYesModel)

        response should have(
          httpStatus(UNAUTHORIZED),
          pageTitle("Your session has timed out")
        )
      }
    }

    "the user is not authorised" should {

      "return 403 FORBIDDEN" in {

        given.user.isNotAuthorised

        val response: WSResponse = postRequest(validYesModel)

        response should have(
          httpStatus(FORBIDDEN),
          pageTitle("You can’t use this service yet")
        )
      }
    }
  }
}
