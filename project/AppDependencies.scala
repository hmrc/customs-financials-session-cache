import play.core.PlayVersion.current
import sbt._

object AppDependencies {

  private val bootstrapVersion = "7.19.0"

  val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc" %% "bootstrap-backend-play-28" % bootstrapVersion,
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-28" % "1.3.0"
  )

  val test: Seq[ModuleID] = Seq(
    "org.scalatest" %% "scalatest" % "3.2.16" % Test,
    "com.typesafe.play" %% "play-test" % current % Test,
    "com.vladsch.flexmark" % "flexmark-all" % "0.64.8" % "test",
    "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % "test",
    "org.mockito" %% "mockito-scala-scalatest" % "1.17.14" % "test",
    "uk.gov.hmrc" %% "bootstrap-test-play-28" % bootstrapVersion % "test"
  )
}
