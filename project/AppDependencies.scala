import sbt.*

object AppDependencies {

  val bootstrapVersion     = "9.11.0"
  private val mongoVersion = "2.6.0"

  val compile: Seq[ModuleID] = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc"       %% "bootstrap-backend-play-30" % bootstrapVersion,
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-30"        % mongoVersion
  )

  val test: Seq[ModuleID] = Seq(
    "org.scalatestplus" %% "mockito-4-11"           % "3.2.18.0"       % Test,
    "uk.gov.hmrc"       %% "bootstrap-test-play-30" % bootstrapVersion % Test
  )
}
