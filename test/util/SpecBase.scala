/*
 * Copyright 2021 HM Revenue & Customs
 *
 */

package util

import org.mockito.scalatest.MockitoSugar
import org.scalatest.OptionValues
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest

class SpecBase extends AnyWordSpecLike with Matchers with MockitoSugar with OptionValues with ScalaFutures with IntegrationPatience {

  protected def applicationBuilder(): GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .overrides(
      ).configure("auditing.enabled" -> "false")
      .configure("metrics.enabled" -> "false")


  def fakeRequest(method: String = "", path: String = ""): FakeRequest[AnyContentAsEmpty.type] = FakeRequest(method, path)
}
