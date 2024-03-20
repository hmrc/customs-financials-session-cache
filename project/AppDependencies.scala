import play.core.PlayVersion.current
import sbt.*

object AppDependencies {

  private val bootstrapVersion = "8.5.0"

  val compile: Seq[ModuleID] = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc" %% "bootstrap-backend-play-30" % bootstrapVersion,
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-30" % "1.8.0"
  )

  val test: Seq[ModuleID] = Seq(
    "org.scalatest" %% "scalatest" % "3.2.16" % Test,
    "com.typesafe.play" %% "play-test" % "2.9.2" % Test,
    "com.vladsch.flexmark" % "flexmark-all" % "0.64.8" % "test",
    "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.1" % "test",
    "org.mockito" %% "mockito-scala-scalatest" % "1.17.14" % "test",
    "uk.gov.hmrc" %% "bootstrap-test-play-30" % bootstrapVersion % "test"
  )
}
