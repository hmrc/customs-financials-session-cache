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

import com.mongodb.client.model.Indexes.ascending
import models.AccountLink
import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.model.{IndexModel, IndexOptions, ReplaceOptions}
import play.api.Configuration
import play.api.libs.functional.syntax.{unlift, _}
import play.api.libs.json.{Format, OWrites, Reads, __}
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository
import uk.gov.hmrc.mongo.play.json.formats.MongoJavatimeFormats

import java.time.LocalDateTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DefaultSessionCacheRepository @Inject()(mongoComponent: MongoComponent,
                                              config: Configuration)
                                             (implicit executionContext: ExecutionContext)
  extends PlayMongoRepository[AccountLinksMongo](
    collectionName = "customs-financials-session-cache",
    mongoComponent = mongoComponent,
    domainFormat = AccountLinksMongo.format,
    indexes = Seq(
      IndexModel(
        ascending("lastUpdated"),
        IndexOptions().name("customs-financials-cache-last-updated-index")
          .unique(true)
          .expireAfter(config.get[Int]("mongodb.timeToLiveInSeconds"), TimeUnit.SECONDS)
      )
    )) with SessionCacheRepository {

  override def get(sessionId: String, linkId: String): Future[Option[AccountLink]] = {
    for {
      record <- collection.find(equal("_id", sessionId)).toSingle().toFutureOption()
      result = record.flatMap(_.accountLinks.find(_.linkId == linkId))
    } yield result
  }

  override def clearAndInsert(sessionId: String, accountLinks: Seq[AccountLink]): Future[Boolean] = {
    val record = AccountLinksMongo(accountLinks)
    for {
      writeSuccessful <- collection.replaceOne(
        equal("_id", sessionId),
        record,
        ReplaceOptions().upsert(true)
      ).toFuture().map(_.wasAcknowledged())
    } yield writeSuccessful
  }

  override def remove(sessionId: String): Future[Boolean] = {
    collection.deleteOne(equal("_id", sessionId))
      .toFuture()
      .map(_.wasAcknowledged())
  }
}

trait SessionCacheRepository {
  def get(sessionId: String, linkId: String): Future[Option[AccountLink]]

  def clearAndInsert(sessionId: String, accountLinks: Seq[AccountLink]): Future[Boolean]

  def remove(sessionId: String): Future[Boolean]
}

case class AccountLinksMongo(accountLinks: Seq[AccountLink], lastUpdated: LocalDateTime = LocalDateTime.now)

object AccountLinksMongo {
  implicit lazy val writes: OWrites[AccountLinksMongo] = {
    (
      (__ \ "accountLinks").write[Seq[AccountLink]] and
        (__ \ "lastUpdated").write(MongoJavatimeFormats.localDateTimeWrites)
      ) (unlift(AccountLinksMongo.unapply))
  }
  implicit lazy val reads: Reads[AccountLinksMongo] = {
    (
      (__ \ "accountLinks").read[Seq[AccountLink]] and
        (__ \ "lastUpdated").read(MongoJavatimeFormats.localDateTimeReads)
      ) (AccountLinksMongo.apply _)
  }

  implicit val format: Format[AccountLinksMongo] = Format(reads, writes)
}



