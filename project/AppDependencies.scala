import play.core.PlayVersion.current
import play.sbt.PlayImport._
import sbt.Keys.libraryDependencies
import sbt._

object AppDependencies {

  val compile = Seq(
    "uk.gov.hmrc" %% "bootstrap-backend-play-28" % "5.3.0",
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-28" % "0.50.0"
  )

  val test = Seq(
    "org.scalatest" %% "scalatest" % "3.2.9" % Test,
    "com.typesafe.play" %% "play-test" % current % Test,
    "com.vladsch.flexmark" % "flexmark-all" % "0.36.8" % "test",
    "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % "test",
    "org.mockito" %% "mockito-scala-scalatest" % "1.16.37" % "test"
  )
}
