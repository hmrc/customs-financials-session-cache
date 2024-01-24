import scoverage.ScoverageKeys
import uk.gov.hmrc.DefaultBuildSettings.targetJvm
import uk.gov.hmrc.DefaultBuildSettings.itSettings

val appName = "customs-financials-session-cache"
val silencerVersion = "1.17.13"
val bootstrapVersion = "7.22.0"
val scalaStyleConfigFile = "scalastyle-config.xml"
val testScalaStyleConfigFile = "test-scalastyle-config.xml"
val testDirectory = "test"
val scala2_13_8 = "2.13.8"

lazy val scalastyleSettings = Seq(scalastyleConfig := baseDirectory.value /
  "scalastyle-config.xml", (Test / scalastyleConfig) := baseDirectory.value / testDirectory
  / "test-scalastyle-config.xml")

lazy val it = project
  .enablePlugins(PlayScala)
  .dependsOn(microservice % "test->test")
  .settings(itSettings)
  .settings(libraryDependencies ++= Seq("uk.gov.hmrc" %% "bootstrap-test-play-28" % bootstrapVersion % Test))

ThisBuild / majorVersion := 0
ThisBuild / scalaVersion := scala2_13_8

lazy val microservice = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala, SbtAutoBuildPlugin, SbtGitVersioning, SbtDistributablesPlugin)
  .settings(
    targetJvm := "jvm-11",
    libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test,

    scalacOptions ++= Seq("-Wunused:imports", "-Wunused:params", "-Wunused:patvars",
      "-Wunused:implicits", "-Wunused:explicits", "-Wunused:privates"),

    Test / scalacOptions ++= Seq("-Wunused:imports", "-Wunused:params", "-Wunused:patvars",
      "-Wunused:implicits", "-Wunused:explicits", "-Wunused:privates"),

    libraryDependencies ++= Seq(
      compilerPlugin("com.github.ghik" % "silencer-plugin" % silencerVersion cross CrossVersion.full),
      "com.github.ghik" % "silencer-lib" % silencerVersion % Provided cross CrossVersion.full
    ),
    ScoverageKeys.coverageExcludedFiles := "<empty>;Reverse.*;.*filters.*;.*handlers.*;.*components.*;" +
      ".*javascript.*;.*Routes.*;.*GuiceInjector;.*ControllerConfiguration",
    ScoverageKeys.coverageMinimumBranchTotal := 100,
    ScoverageKeys.coverageMinimumStmtTotal := 100,
    ScoverageKeys.coverageFailOnMinimum := true,
    ScoverageKeys.coverageHighlighting := true,
    scalacOptions ++= Seq(
      "-P:silencer:pathFilters=routes",
      "-Wunused:imports",
      "-Wunused:params",
      "-Wunused:patvars",
      "-Wunused:implicits",
      "-Wunused:explicits",
      "-Wunused:privates"),
    Test / scalacOptions ++= Seq(
      "-Wunused:imports",
      "-Wunused:params",
      "-Wunused:patvars",
      "-Wunused:implicits",
      "-Wunused:explicits",
      "-Wunused:privates")
  )
  .settings(PlayKeys.playDefaultPort := 9840)
  .configs(IntegrationTest)
  .settings(scalastyleSettings)
  .settings(resolvers += Resolver.jcenterRepo)
