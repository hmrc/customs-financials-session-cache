/*
 * Copyright 2021 HM Revenue & Customs
 *
 */

package config

import com.google.inject.AbstractModule
import repositories.{DefaultSessionCacheRepository, SessionCacheRepository}

class Module extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[SessionCacheRepository]).to(classOf[DefaultSessionCacheRepository]).asEagerSingleton()
  }

}
