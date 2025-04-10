import scoverage.ScoverageKeys

val appName = "customs-financials-session-cache"

val silencerVersion = "1.7.16"
val scala3_3_5      = "3.3.5"

ThisBuild / majorVersion := 0
ThisBuild / scalaVersion := scala3_3_5

val scalaStyleConfigFile     = "scalastyle-config.xml"
val testScalaStyleConfigFile = "test-scalastyle-config.xml"
val testDirectory            = "test"

lazy val scalastyleSettings = Seq(
  scalastyleConfig := baseDirectory.value /
    "scalastyle-config.xml",
  (Test / scalastyleConfig) := baseDirectory.value / testDirectory /
    "test-scalastyle-config.xml"
)

lazy val it = project
  .enablePlugins(PlayScala)
  .dependsOn(microservice % "test->test")
  .settings(
    libraryDependencies ++= Seq("uk.gov.hmrc" %% "bootstrap-test-play-30" % AppDependencies.bootstrapVersion % Test)
  )

lazy val microservice = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala, SbtDistributablesPlugin)
  .settings(
    libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test,
    libraryDependencies ++= Seq(
      compilerPlugin(
        "com.github.ghik" % "silencer-plugin" % silencerVersion cross CrossVersion.for3Use2_13With("", ".12")
      ),
      "com.github.ghik" % "silencer-lib" % silencerVersion % Provided cross CrossVersion.for3Use2_13With("", ".12")
    ),
    scalafmtDetailedError := true,
    scalafmtPrintDiff := true,
    scalafmtFailOnErrors := true,
    ScoverageKeys.coverageExcludedFiles := "<empty>;Reverse.*;.*filters.*;.*handlers.*;.*components.*;" +
      ".*javascript.*;.*Routes.*;.*GuiceInjector;.*ControllerConfiguration",
    ScoverageKeys.coverageMinimumBranchTotal := 100,
    ScoverageKeys.coverageMinimumStmtTotal := 100,
    ScoverageKeys.coverageFailOnMinimum := false,
    ScoverageKeys.coverageHighlighting := true,
    scalacOptions := scalacOptions.value.diff(Seq("-Wunused:all")),
    scalacOptions += "-Wconf:msg=Flag.*repeatedly:s",
    Test / scalacOptions ++= Seq(
      "-Wunused:imports",
      "-Wunused:params",
      "-Wunused:implicits",
      "-Wunused:explicits",
      "-Wunused:privates"
    )
  )
  .settings(PlayKeys.playDefaultPort := 9840)
  .settings(scalastyleSettings)
  .settings(resolvers += Resolver.jcenterRepo)

addCommandAlias(
  "runAllChecks",
  ";clean;compile;coverage;test;it/test;scalafmtCheckAll;scalastyle;Test/scalastyle;coverageReport"
)
