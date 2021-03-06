lazy val commonSettings = Seq(
  version := "0.1",
  scalaVersion := "2.12.8",
  organization := "advanced-software-insights",
  name := "basic-analytics"
)

lazy val root = (project in file("."))
  .configs(IntegrationTest)
  .settings(
    commonSettings,
    Defaults.itSettings,
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % "it,test",
    libraryDependencies += "com.spotify" % "docker-client" % "8.11.2" % "it,test",
    libraryDependencies += "org.apache.httpcomponents" % "httpclient" % "4.5.5" % "it,test",
    libraryDependencies += "org.mockito" % "mockito-core" % "3.0.0" % "it,test",
    libraryDependencies += "com.typesafe.akka" %% "akka-http"   % "10.1.1",
    libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.5.23",
    libraryDependencies += "joda-time" % "joda-time" % "2.10.3",
    libraryDependencies += "com.h2database" % "h2" % "1.4.199",
    libraryDependencies += "io.gatling" % "gatling-test-framework" % "3.1.3",
    libraryDependencies += "io.gatling.highcharts" % "gatling-charts-highcharts" % "3.1.3",
      testFrameworks += new TestFramework("io.gatling.GatlingFramework"),
    IntegrationTest / parallelExecution := false
  )