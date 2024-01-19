import uk.gov.hmrc.DefaultBuildSettings.integrationTestSettings
import scoverage.ScoverageKeys
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin.publishingSettings
import uk.gov.hmrc.DefaultBuildSettings.targetJvm

val appName = "customs-financials-session-cache"
val silencerVersion = "1.17.13"
val scalaStyleConfigFile = "scalastyle-config.xml"
val testScalaStyleConfigFile = "test-scalastyle-config.xml"
val testDirectory = "test"

lazy val scalastyleSettings = Seq(scalastyleConfig := baseDirectory.value /
  "scalastyle-config.xml", (scalastyleConfig in Test) := baseDirectory.value / testDirectory
  / "test-scalastyle-config.xml")

lazy val microservice = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala, SbtAutoBuildPlugin, SbtGitVersioning, SbtDistributablesPlugin)
  .settings(
    majorVersion                     := 0,
    scalaVersion                     := "2.13.8",
    targetJvm                        := "jvm-11",
    libraryDependencies              ++= AppDependencies.compile ++ AppDependencies.test,

    scalacOptions ++= Seq("-Wunused:imports", "-Wunused:params", "-Wunused:patvars",
      "-Wunused:implicits", "-Wunused:explicits", "-Wunused:privates"),

    Test / scalacOptions ++= Seq("-Wunused:imports", "-Wunused:params", "-Wunused:patvars",
      "-Wunused:implicits","-Wunused:explicits", "-Wunused:privates"),

    libraryDependencies ++= Seq(
      compilerPlugin("com.github.ghik" % "silencer-plugin" % silencerVersion cross CrossVersion.full),
      "com.github.ghik" % "silencer-lib" % silencerVersion % Provided cross CrossVersion.full
    ),
    ScoverageKeys.coverageExcludedFiles := "<empty>;Reverse.*;.*filters.*;.*handlers.*;.*components.*;" +
      ".*javascript.*;.*Routes.*;.*GuiceInjector;" +
      ".*ControllerConfiguration;.*AppConfig",
    ScoverageKeys.coverageMinimum := 100,
    ScoverageKeys.coverageFailOnMinimum := true,
    ScoverageKeys.coverageHighlighting := true
  )
  .settings(PlayKeys.playDefaultPort:= 9840)
  .settings(publishingSettings: _*)
  .configs(IntegrationTest)
  .settings(scalastyleSettings)
  .settings(integrationTestSettings(): _*)
  .settings(resolvers += Resolver.jcenterRepo)

