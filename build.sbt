import scoverage.ScoverageKeys
import uk.gov.hmrc.DefaultBuildSettings.targetJvm
import uk.gov.hmrc.DefaultBuildSettings.itSettings

val appName = "customs-financials-session-cache"

val silencerVersion = "1.17.16"
val bootstrapVersion = "8.5.0"
val scala2_13_12 = "2.13.12"

ThisBuild / majorVersion := 0
ThisBuild / scalaVersion := scala2_13_12

val scalaStyleConfigFile = "scalastyle-config.xml"
val testScalaStyleConfigFile = "test-scalastyle-config.xml"
val testDirectory = "test"

lazy val scalastyleSettings = Seq(scalastyleConfig := baseDirectory.value /
  "scalastyle-config.xml", (Test / scalastyleConfig) := baseDirectory.value / testDirectory
  / "test-scalastyle-config.xml")

lazy val it = project
  .enablePlugins(PlayScala)
  .dependsOn(microservice % "test->test")
  .settings(libraryDependencies ++= Seq("uk.gov.hmrc" %% "bootstrap-test-play-30" % bootstrapVersion % Test))

ThisBuild / majorVersion := 0
ThisBuild / scalaVersion := scala2_13_12

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
  .settings(scalastyleSettings)
  .settings(resolvers += Resolver.jcenterRepo)
