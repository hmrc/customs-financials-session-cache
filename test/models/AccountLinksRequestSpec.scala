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

package models

import util.SpecBase
import play.api.libs.json.{JsResultException, JsSuccess, Json}

class AccountLinksRequestSpec extends SpecBase {

  "AccountLinksRequest.format" should {
    "generate correct output for Json Reads" in new Setup {
      import AccountLinksRequest.format

      Json.fromJson(Json.parse(accountLinksRequestObJsString)) mustBe JsSuccess(accountLinksRequestOb)
    }

    "generate correct output for Json Writes" in new Setup {
      Json.toJson(accountLinksRequestOb) mustBe Json.parse(accountLinksRequestObJsString)
    }

    "throw exception for invalid Json" in {
      val invalidJson = "{ \"status\": \"pending\", \"eventId1\": \"test_event\" }"

      intercept[JsResultException] {
        Json.parse(invalidJson).as[AccountLinksRequest]
      }
    }
  }

  "AccountLink.format" should {
    "generate correct output for Json Reads" in new Setup {
      import AccountLink.format

      Json.fromJson(Json.parse(accountLinkObJsString)) mustBe JsSuccess(accountLinkOb)
    }

    "generate correct output for Json Writes" in new Setup {
      Json.toJson(accountLinkOb) mustBe Json.parse(accountLinkObJsString)
    }

    "throw exception for invalid Json" in {
      val invalidJson = "{ \"status\": \"pending\", \"eventId1\": \"test_event\" }"

      intercept[JsResultException] {
        Json.parse(invalidJson).as[AccountLink]
      }
    }
  }

  trait Setup {
    val accStatusId = 34567

    val accountLinkOb: AccountLink = AccountLink(
      eori = "test_eori",
      isNiAccount = true,
      accountNumber = "1234567",
      accountStatus = "Open",
      accountStatusId = Some(accStatusId),
      linkId = "test_link_id"
    )

    val accountLinksRequestOb: AccountLinksRequest = AccountLinksRequest("acfdgt123456sddfff", Seq(accountLinkOb))

    val accountLinkObJsString: String =
      """{
        |"eori":"test_eori",
        |"isNiAccount":true,
        |"accountNumber":"1234567",
        |"accountStatus":"Open",
        |"accountStatusId":34567,
        |"linkId":"test_link_id"
        |}""".stripMargin

    val accountLinksRequestObJsString: String =
      """{
        |"sessionId":"acfdgt123456sddfff",
        |"accountLinks":[
        |{"eori":"test_eori",
        |"isNiAccount":true,
        |"accountNumber":"1234567",
        |"accountStatus":"Open",
        |"accountStatusId":34567,
        |"linkId":"test_link_id"}
        |]
        |}""".stripMargin

  }
}
