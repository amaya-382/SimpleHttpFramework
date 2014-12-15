import AssemblyKeys._

name := "Simple Http Server"

version := "0.1.0"

scalaVersion := "2.11.4"

jarName in assembly := "simpleHttpServer.jar"

resolvers ++= Seq()

libraryDependencies ++= Seq(
  "org.json4s" %% "json4s-native" % "3.2.11"
)

assemblySettings
