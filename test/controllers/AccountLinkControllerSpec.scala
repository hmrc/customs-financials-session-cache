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

package controllers

import models.AccountLink
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.{Application, inject}
import repositories.SessionCacheRepository
import util.SpecBase
import scala.concurrent.Future

import org.mockito.Mockito.when
import org.mockito.ArgumentMatchers.{eq => eqTo}

class AccountLinkControllerSpec extends SpecBase {

  "getAccountLink" must {
    "return OK with a account link json if found in the session cache" in new Setup {
      running(app) {
        val accountLink = AccountLink("someEori", false, "someAccountNumber", "open", Some(1), "linkId")
        when(mockSessionCache.get(eqTo(testSessionId), eqTo(testLinkId)))
          .thenReturn(Future.successful(Some(accountLink)))

        val result = route(
          app,
          fakeRequest(GET, controllers.routes.AccountLinkController.getAccountLink(testSessionId, testLinkId).url)
        ).value

        status(result) mustBe OK

        val accountLinkResult = contentAsJson(result).as[AccountLink]
        accountLinkResult mustBe accountLink
      }
    }

    "return NOT_FOUND when no data found in the session cache when retrieving an accountLink" in new Setup {
      when(mockSessionCache.get(eqTo(testSessionId), eqTo(testLinkId)))
        .thenReturn(Future.successful(None))

      running(app) {
        val result = route(
          app,
          fakeRequest(GET, controllers.routes.AccountLinkController.getAccountLink(testSessionId, testLinkId).url)
        ).value

        status(result) mustBe NOT_FOUND
      }
    }

    "return an INTERNAL_SERVER_ERROR if an exception was thrown when retrieving an account link" in new Setup {
      when(mockSessionCache.get(eqTo(testSessionId), eqTo(testLinkId)))
        .thenReturn(Future.failed(new RuntimeException("Something went wrong")))

      running(app) {
        val result = route(
          app,
          fakeRequest(GET, controllers.routes.AccountLinkController.getAccountLink(testSessionId, testLinkId).url)
        ).value

        status(result) mustBe INTERNAL_SERVER_ERROR
      }
    }
  }

  "clearAndInsert" must {
    "return NO_CONTENT if the data has successfully been written to the database" in new Setup {
      when(mockSessionCache.clearAndInsert(eqTo(testSessionId), eqTo(Seq(accountLink))))
        .thenReturn(Future.successful(true))

      running(app) {
        val result = route(
          app,
          fakeRequest(POST, controllers.routes.AccountLinkController.clearAndInsert().url).withJsonBody(
            Json.obj(
              "sessionId"    -> testSessionId,
              "accountLinks" -> Json.arr(Json.toJson(accountLink))
            )
          )
        ).value

        status(result) mustBe NO_CONTENT
      }
    }

    "return INTERNAL_SERVER_ERROR if the data has failed to write to the database" in new Setup {
      when(mockSessionCache.clearAndInsert(eqTo(testSessionId), eqTo(Seq(accountLink))))
        .thenReturn(Future.successful(false))

      running(app) {
        val result = route(
          app,
          fakeRequest(POST, controllers.routes.AccountLinkController.clearAndInsert().url).withJsonBody(
            Json.obj(
              "sessionId"    -> testSessionId,
              "accountLinks" -> Json.arr(Json.toJson(accountLink))
            )
          )
        ).value

        status(result) mustBe INTERNAL_SERVER_ERROR
      }
    }

    "return INTERNAL_SERVER_ERROR if an exception was thrown when writing to the cache" in new Setup {
      when(mockSessionCache.clearAndInsert(eqTo(testSessionId), eqTo(Seq(accountLink))))
        .thenReturn(Future.failed(new RuntimeException("Something went wrong")))

      running(app) {
        val result = route(
          app,
          fakeRequest(POST, controllers.routes.AccountLinkController.clearAndInsert().url).withJsonBody(
            Json.obj(
              "sessionId"    -> testSessionId,
              "accountLinks" -> Json.arr(Json.toJson(accountLink))
            )
          )
        ).value

        status(result) mustBe INTERNAL_SERVER_ERROR
      }
    }
  }

  "remove" must {
    "return NO_CONTENT if record for the session id is removed" in new Setup {
      when(mockSessionCache.remove(eqTo(testSessionId)))
        .thenReturn(Future.successful(true))

      running(app) {
        val result =
          route(app, fakeRequest(DELETE, controllers.routes.AccountLinkController.remove(testSessionId).url)).value

        status(result) mustBe NO_CONTENT
      }
    }

    "return INTERNAL_SERVER_ERROR if the data has failed remove record from database" in new Setup {
      when(mockSessionCache.remove(eqTo(testSessionId)))
        .thenReturn(Future.successful(false))

      running(app) {
        val result =
          route(app, fakeRequest(DELETE, controllers.routes.AccountLinkController.remove(testSessionId).url)).value

        status(result) mustBe INTERNAL_SERVER_ERROR
      }
    }

    "return INTERNAL_SERVER_ERROR if an exception was thrown when removing record from database" in new Setup {
      when(mockSessionCache.remove(eqTo(testSessionId)))
        .thenReturn(Future.failed(new RuntimeException("Something went wrong")))

      running(app) {
        val result =
          route(app, fakeRequest(DELETE, controllers.routes.AccountLinkController.remove(testSessionId).url)).value

        status(result) mustBe INTERNAL_SERVER_ERROR
      }
    }
  }

  "getAccountNumber" must {
    "return OK with a account number json if found in the session cache" in new Setup {
      running(app) {
        val accountLink = Seq(AccountLink("someEori", true, "1234567", "open", Some(1), "linkId"))

        when(mockSessionCache.getAccountLinks(testSessionId))
          .thenReturn(Future.successful(Option(accountLink)))

        val result = route(
          app,
          fakeRequest(GET, controllers.routes.AccountLinkController.getAccountLinks(testSessionId).url)
        ).value

        status(result) mustBe OK

        val acountLinkResult = contentAsJson(result).as[Seq[AccountLink]]
        acountLinkResult mustBe accountLink
      }
    }

    "return NOT_FOUND when no data found in the session cache when retrieving an accountNumber" in new Setup {
      when(mockSessionCache.getAccountLinks(eqTo(testSessionId)))
        .thenReturn(Future.successful(None))

      running(app) {
        val result = route(
          app,
          fakeRequest(GET, controllers.routes.AccountLinkController.getAccountLinks(testSessionId).url)
        ).value

        status(result) mustBe NOT_FOUND
      }
    }

    "return an INTERNAL_SERVER_ERROR if an exception was thrown when retrieving an accountNumber" in new Setup {
      when(mockSessionCache.getAccountLinks(eqTo(testSessionId)))
        .thenReturn(Future.failed(new RuntimeException("Something went wrong")))

      running(app) {
        val result = route(
          app,
          fakeRequest(GET, controllers.routes.AccountLinkController.getAccountLinks(testSessionId).url)
        ).value

        status(result) mustBe INTERNAL_SERVER_ERROR
      }
    }
  }

  "getSessionId" should {
    "Return OK when session is valid" in new Setup {
      running(app) {
        when(mockSessionCache.verifySessionId(testSessionId)).thenReturn(Future.successful(true))

        val result =
          route(app, fakeRequest(GET, controllers.routes.AccountLinkController.getSessionId(testSessionId).url)).value

        status(result) mustBe OK
      }
    }

    "Return NotFound when sessionId is missing" in new Setup {
      running(app) {
        when(mockSessionCache.verifySessionId(testSessionId)).thenReturn(Future.successful(false))

        val result =
          route(app, fakeRequest(GET, controllers.routes.AccountLinkController.getSessionId(testSessionId).url)).value

        status(result) mustBe NOT_FOUND
      }
    }

    "Return INTERNAL_SERVER_ERROR if an exception was thrown when retrieving an " in new Setup {
      running(app) {
        when(mockSessionCache.verifySessionId(eqTo(testSessionId)))
          .thenReturn(Future.failed(new RuntimeException("Something went wrong")))

        running(app) {
          val result =
            route(app, fakeRequest(GET, controllers.routes.AccountLinkController.getSessionId(testSessionId).url)).value

          status(result) mustBe INTERNAL_SERVER_ERROR
        }
      }
    }
  }

  trait Setup {
    val mockSessionCache: SessionCacheRepository = mock[SessionCacheRepository]
    val testSessionId: String                    = "sessionId"
    val testLinkId: String                       = "linkId"
    val testEori: String                         = "someEori"
    val accountLink: AccountLink                 = AccountLink("someEori", false, "someAccountNumber", "open", Some(1), "linkId")

    val app: Application = applicationBuilder()
      .overrides(
        inject.bind[SessionCacheRepository].toInstance(mockSessionCache)
      )
      .build()
  }
}
