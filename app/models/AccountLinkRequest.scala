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

import play.api.libs.json.{Json, OFormat}

case class AccountLinksRequest(sessionId: String, accountLinks: Seq[AccountLink])

object AccountLinksRequest {
  implicit val format: OFormat[AccountLinksRequest] = Json.format[AccountLinksRequest]
}

case class AccountLink(
  eori: String,
  isNiAccount: Boolean,
  accountNumber: String,
  accountStatus: String,
  accountStatusId: Option[Int],
  linkId: String
)

object AccountLink {
  implicit val format: OFormat[AccountLink] = Json.format[AccountLink]
}
