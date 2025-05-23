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

import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import models.AccountLinksRequest
import repositories.SessionCacheRepository
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import javax.inject.Inject
import scala.concurrent.ExecutionContext

class AccountLinkController @Inject() (cc: ControllerComponents, sessionCacheRepository: SessionCacheRepository)(
  implicit executionContext: ExecutionContext
) extends BackendController(cc) {

  def getAccountLink(id: String, linkId: String): Action[AnyContent] = Action.async {
    sessionCacheRepository
      .get(id, linkId)
      .map {
        case Some(accountLink) => Ok(Json.toJson(accountLink))
        case None              => NotFound
      }
      .recover { case _ => InternalServerError }
  }

  def getAccountLinks(sessionId: String): Action[AnyContent] = Action.async {
    sessionCacheRepository
      .getAccountLinks(sessionId)
      .map {
        case Some(accountNumbers) => Ok(Json.toJson(accountNumbers))
        case _                    => NotFound
      }
      .recover { case _ => InternalServerError }
  }

  def getSessionId(sessionId: String): Action[AnyContent] = Action.async {
    sessionCacheRepository
      .verifySessionId(sessionId)
      .map {
        case true  => Ok(sessionId)
        case false => NotFound
      }
      .recover { case _ => InternalServerError }
  }

  def clearAndInsert(): Action[AccountLinksRequest] = Action.async(parse.json[AccountLinksRequest]) {
    implicit request =>
      sessionCacheRepository
        .clearAndInsert(request.body.sessionId, request.body.accountLinks)
        .map { writeSuccessful =>
          if (writeSuccessful) {
            NoContent
          } else {
            InternalServerError
          }
        }
        .recover { case _ => InternalServerError }
  }

  def remove(id: String): Action[AnyContent] = Action.async {
    sessionCacheRepository
      .remove(id)
      .map { removed =>
        if (removed) {
          NoContent
        } else {
          InternalServerError
        }
      }
      .recover { case _ => InternalServerError }
  }
}
