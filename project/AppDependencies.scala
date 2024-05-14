import sbt.*

object AppDependencies {

  private val bootstrapVersion = "8.6.0"
  private val mongoVersion = "1.9.0"

  val compile: Seq[ModuleID] = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc" %% "bootstrap-backend-play-30" % bootstrapVersion,
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-30" % mongoVersion
  )

  val test: Seq[ModuleID] = Seq(
    "org.mockito" %% "mockito-scala-scalatest" % "1.17.31" % Test,
    "uk.gov.hmrc" %% "bootstrap-test-play-30" % bootstrapVersion % Test
  )
}
