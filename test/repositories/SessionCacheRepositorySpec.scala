/*
 * Copyright 2021 HM Revenue & Customs
 *
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

    "return none if there is data stored by a given session id but no data found associated to a given linkId" in new Setup {
      val accountLink: AccountLink = AccountLink("someEori", "someAccountNumber", "open", Some(1), "linkId")

      running(app) {
        val repository = app.injector.instanceOf[SessionCacheRepository]
        await(repository.clearAndInsert("someSessionId", Seq(accountLink)))
        val result = await(repository.get("someSessionId", "invalidLinkId"))
        result mustBe None
      }
    }

    "return accountLink if the sessionId is stored and there is a linkId match in the session" in new Setup {
      val accountLink: AccountLink = AccountLink("someEori", "someAccountNumber", "open", Some(1), "linkId")

      running(app) {
        val repository = app.injector.instanceOf[SessionCacheRepository]
        await(repository.clearAndInsert("someSessionId", Seq(accountLink)))
        val result = await(repository.get("someSessionId", "linkId"))
        result.value mustBe accountLink
      }
    }
  }

  "clearAndInsert" should {
    "remove the existing data associated with the sessionId and populate the links" in new Setup {
      val accountLinks: Seq[AccountLink] = Seq(
        AccountLink("someEori", "someAccountNumber", "open", Some(1), "linkId"),
        AccountLink("someEori2", "someAccountNumber2", "closed", Some(1), "linkId2"),
        AccountLink("someEori3", "someAccountNumber3", "inhibited", Some(1), "linkId3"),
        AccountLink("someEori4", "someAccountNumber4", "suspended", Some(1), "linkId4")
      )
      running(app) {
        val preInsertResult = await(repository.get("someSessionId", "linkId"))
        preInsertResult mustBe None
        await(repository.clearAndInsert("someSessionId", accountLinks))
        val postInsertResult1 = await(repository.get("someSessionId", "linkId"))
        val postInsertResult2 = await(repository.get("someSessionId", "linkId2"))
        val postInsertResult3 = await(repository.get("someSessionId", "linkId3"))
        val postInsertResult4 = await(repository.get("someSessionId", "linkId4"))

        postInsertResult1 mustBe Some(AccountLink("someEori", "someAccountNumber", "open", Some(1), "linkId"))
        postInsertResult2 mustBe Some(AccountLink("someEori2", "someAccountNumber2", "closed", Some(1), "linkId2"))
        postInsertResult3 mustBe Some(AccountLink("someEori3", "someAccountNumber3", "inhibited", Some(1), "linkId3"))
        postInsertResult4 mustBe Some(AccountLink("someEori4", "someAccountNumber4", "suspended", Some(1), "linkId4"))
      }
    }
  }

  "remove" should {
    "return true if the remove was successful" in new Setup {
      val accountLink: AccountLink = AccountLink("someEori", "someAccountNumber", "open", Some(1), "linkId")

      await(repository.clearAndInsert("someSessionId", Seq(accountLink)))
      running(app){
        await(repository.get("someSessionId", "linkId")) mustBe Some(accountLink)
        await(repository.remove("someSessionId"))
        await(repository.get("someSessionId", "linkId")) mustBe None
      }
    }
  }

  trait Setup {
    val app: Application = new GuiceApplicationBuilder().build()
    val repository: SessionCacheRepository = app.injector.instanceOf[SessionCacheRepository]
  }

  override def afterEach(): Unit = {
    val app: Application = new GuiceApplicationBuilder().build()
    val repository: SessionCacheRepository = app.injector.instanceOf[SessionCacheRepository]
    running(app) {
      await(repository.remove("someSessionId"))
    }
  }
}
