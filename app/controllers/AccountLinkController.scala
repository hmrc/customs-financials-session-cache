/*
 * Copyright 2021 HM Revenue & Customs
 *
 */

package controllers

import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import models.AccountLinksRequest
import repositories.SessionCacheRepository
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class AccountLinkController @Inject()(
                                       cc: ControllerComponents,
                                       sessionCacheRepository: SessionCacheRepository
                                     )(implicit executionContext: ExecutionContext) extends BackendController(cc) {

  def getAccountLink(id: String, linkId: String): Action[AnyContent] = Action.async {
    sessionCacheRepository.get(id, linkId).map {
      case Some(accountLink) => Ok(Json.toJson(accountLink))
      case None => NotFound
    }.recover { case _ => InternalServerError}
  }

  def clearAndInsert(): Action[AccountLinksRequest] = Action.async(parse.json[AccountLinksRequest]) { implicit request =>
    sessionCacheRepository.clearAndInsert(request.body.sessionId, request.body.accountLinks).map { writeSuccessful =>
      if(writeSuccessful){
        NoContent
      } else InternalServerError
    }.recover { case _ => InternalServerError}
  }

  def remove(id:String):Action[AnyContent] = Action.async {
    sessionCacheRepository.remove(id).map{ removed =>
      if(removed){
        NoContent
      }else InternalServerError
    }.recover{
      case _ => InternalServerError
    }
  }
}
