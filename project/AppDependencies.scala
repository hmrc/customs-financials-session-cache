import play.core.PlayVersion.current
import sbt._

object AppDependencies {

  val bootstrapVersion = "7.15.0"

  val compile = Seq(
    "uk.gov.hmrc" %% "bootstrap-backend-play-28" % bootstrapVersion,
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-28" % "1.2.0"
  )

  val test = Seq(
    "org.scalatest" %% "scalatest" % "3.2.9" % Test,
    "com.typesafe.play" %% "play-test" % current % Test,
    "com.vladsch.flexmark" % "flexmark-all" % "0.36.8" % "test",
    "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % "test",
    "org.mockito" %% "mockito-scala-scalatest" % "1.16.46" % "test",
    "uk.gov.hmrc" %% "bootstrap-test-play-28" % bootstrapVersion % "test"
  )
}
