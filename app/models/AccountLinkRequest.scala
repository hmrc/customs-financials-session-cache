/*
 * Copyright 2021 HM Revenue & Customs
 *
 */

package models


import play.api.libs.json.{Json, OFormat}

case class AccountLinksRequest(sessionId: String,
                               accountLinks: Seq[AccountLink])

object AccountLinksRequest {
  implicit val format: OFormat[AccountLinksRequest] = Json.format[AccountLinksRequest]
}

case class AccountLink(eori: String,
                       accountNumber: String,
                       accountStatus: String,
                       accountStatusId: Option[Int],
                       linkId: String)

object AccountLink {
  implicit val format: OFormat[AccountLink] = Json.format[AccountLink]
}
