lazy val commonSettings = Seq(
  version := "0.1",
  scalaVersion := "2.12.6",
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
    libraryDependencies += "com.typesafe.akka" %% "akka-http"   % "10.1.1",
    libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.5.11",
    libraryDependencies += "org.apache.httpcomponents" % "httpclient" % "4.5.5" % "it,test",
    libraryDependencies += "joda-time" % "joda-time" % "2.10.3",
    libraryDependencies += "org.mockito" % "mockito-all" % "1.9.5" % Test,
    libraryDependencies += "com.h2database" % "h2" % "1.4.199"
  )