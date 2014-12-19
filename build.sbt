name := "Simple Http Server"

version := "0.5.0"

scalaVersion := "2.11.4"

jarName := "simpleHttpServer.jar"

assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false)

resolvers ++= Seq()

libraryDependencies ++= Seq(
  "org.json4s" %% "json4s-native" % "3.2.11"
)
