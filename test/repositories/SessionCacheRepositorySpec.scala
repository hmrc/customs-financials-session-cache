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

package repositories

import models._
import org.scalatest.BeforeAndAfterEach
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers._
import util.SpecBase

class SessionCacheRepositorySpec extends SpecBase with BeforeAndAfterEach {

  "get" should {
    "return none if no session data stored by a given id" in new Setup {
      running(app) {
        val result = await(repository.get("someSessionId", "someLinkId"))
        result mustBe None
      }
    }

    "return none if there is data stored by a session id but no data found associated to the linkId" in new Setup {
      val accountLink: AccountLink = AccountLink("someEori", false,
        "someAccountNumber", "open", Some(testValue), "linkId")

      running(app) {
        val repository = app.injector.instanceOf[SessionCacheRepository]
        await(repository.clearAndInsert("someSessionId", Seq(accountLink)))
        val result = await(repository.get("someSessionId", "invalidLinkId"))
        result mustBe None
      }
    }

    "return accountLink if the sessionId is stored and there is a linkId match in the session" in new Setup {
      val accountLink: AccountLink = AccountLink("someEori", false,
        "someAccountNumber", "open", Some(testValue), "linkId")

      running(app) {
        val repository = app.injector.instanceOf[SessionCacheRepository]
        await(repository.clearAndInsert("someSessionId", Seq(accountLink)))
        val result = await(repository.get("someSessionId", "linkId"))
        result.value mustBe accountLink
      }
    }
  }

  "getAccountLinks" should {
    "return none if no session data stored by a given id" in new Setup {
      running(app) {
        val result = await(repository.getAccountLinks("someSessionId"))
        result mustBe None
      }
    }

    "return none if there is data stored by a session id but no data found associated to the eori" in new Setup {
      val accountLink: AccountLink = AccountLink("someEori", false,
        "someAccountNumber", "open", Some(testValue), "linkId")

      running(app) {
        val repository = app.injector.instanceOf[SessionCacheRepository]
        await(repository.clearAndInsert("someSessionId", Seq(accountLink)))
        val result = await(repository.getAccountLinks("someSessionId"))
        result mustBe Some(Vector())
      }
    }

    "return accountLinks if the sessionId is stored and there is a eori match in the session" in new Setup {
      val accountLink: AccountLink = AccountLink("someEori", false, accNum, "open", Some(testValue), "linkId")

      running(app) {
        val repository = app.injector.instanceOf[SessionCacheRepository]
        await(repository.clearAndInsert("someSessionId", Seq(accountLink)))
        val result = await(repository.getAccountLinks("someSessionId"))
        result mustBe Some(Vector(accountLink))
      }
    }
  }

  "clearAndInsert" should {
    "remove the existing data associated with the sessionId and populate the links" in new Setup {
      val accountLinks: Seq[AccountLink] = Seq(
        AccountLink("someEori", false, "someAccountNumber", "open", Some(testValue), "linkId"),
        AccountLink("someEori2", false, "someAccountNumber2", "closed", Some(testValue), "linkId2"),
        AccountLink("someEori3", false, "someAccountNumber3", "inhibited", Some(testValue), "linkId3"),
        AccountLink("someEori4", false, "someAccountNumber4", "suspended", Some(testValue), "linkId4")
      )
      running(app) {
        val preInsertResult = await(repository.get("someSessionId", "linkId"))
        preInsertResult mustBe None
        await(repository.clearAndInsert("someSessionId", accountLinks))
        val postInsertResult1 = await(repository.get("someSessionId", "linkId"))
        val postInsertResult2 = await(repository.get("someSessionId", "linkId2"))
        val postInsertResult3 = await(repository.get("someSessionId", "linkId3"))
        val postInsertResult4 = await(repository.get("someSessionId", "linkId4"))

        postInsertResult1 mustBe Some(AccountLink(
          "someEori", false, "someAccountNumber", "open", Some(testValue), "linkId"))

        postInsertResult2 mustBe Some(AccountLink(
          "someEori2", false, "someAccountNumber2", "closed", Some(testValue), "linkId2"))

        postInsertResult3 mustBe Some(AccountLink(
          "someEori3", false, "someAccountNumber3", "inhibited", Some(testValue), "linkId3"))

        postInsertResult4 mustBe Some(AccountLink(
          "someEori4", false, "someAccountNumber4", "suspended", Some(testValue), "linkId4"))
      }
    }
  }

  "remove" should {
    "return true if the remove was successful" in new Setup {
      val accountLink: AccountLink = AccountLink("someEori", false,
        "someAccountNumber", "open", Some(testValue), "linkId")

      await(repository.clearAndInsert("someSessionId", Seq(accountLink)))

      running(app){
        await(repository.get("someSessionId", "linkId")) mustBe Some(accountLink)
        await(repository.remove("someSessionId"))
        await(repository.get("someSessionId", "linkId")) mustBe None
      }
    }
  }

  "getSessionId" should {
    "return true if valid sessionID" in new Setup {
      val accountLink: AccountLink = AccountLink("someEori", false,
        "someAccountNumber", "open", Some(testValue), "linkId")

      running(app) {
        val repository = app.injector.instanceOf[SessionCacheRepository]
        await(repository.clearAndInsert("someSessionId", Seq(accountLink)))
        val result = await(repository.verifySessionId("someSessionId"))
        result mustBe true
      }
    }

    "return false is no sessionID found" in new Setup {
      running(app) {
        val repository = app.injector.instanceOf[SessionCacheRepository]
        val result = await(repository.verifySessionId("someSessionId"))
        result mustBe false
      }
    }
  }

  trait Setup {
    val app: Application = new GuiceApplicationBuilder().build()
    val repository: SessionCacheRepository = app.injector.instanceOf[SessionCacheRepository]

    val testValue = 1
    val accNum = "1234567"
  }

  override def afterEach(): Unit = {
    val app: Application = new GuiceApplicationBuilder().build()
    val repository: SessionCacheRepository = app.injector.instanceOf[SessionCacheRepository]
    running(app) {
      await(repository.remove("someSessionId"))
    }
  }
}
