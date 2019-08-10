
name := "template"
scalaVersion := "2.12.8"
val akkaVersion = "2.5.19"

enablePlugins(JavaAppPackaging)
enablePlugins(DockerPlugin)
enablePlugins(AshScriptPlugin)

libraryDependencies ++= Seq(
  "net.debasishg" %% "redisclient" % "3.4",
  "com.typesafe.akka" %% "akka-http" % "10.1.5",
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster-sharding" % akkaVersion,
  "com.typesafe.akka" %% "akka-persistence" % "2.5.4",
  "com.github.dnvriend" %% "akka-persistence-jdbc" % "3.4.0",
  "org.flywaydb" % "flyway-core" % "4.2.0",
  "com.typesafe.slick" %% "slick" % "3.2.3",
  "com.github.tminglei" %% "slick-pg" % "0.16.1",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.2.3",
  "org.postgresql" % "postgresql" % "42.2.1",
  "tyrex" % "tyrex" % "1.0.1",
  "org.typelevel" %% "cats-core" % "1.4.0",
  "org.typelevel" %% "cats-free" % "1.4.0",
  "com.github.kxbmap" %% "configs" % "0.4.4",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.7",
//  "io.spray" %% "spray-json" % "1.3.5",
  "com.outworkers" %% "phantom-jdk8" % "2.24.2",
  "com.outworkers" %% "phantom-dsl" % "2.24.2",
  "com.outworkers" %% "phantom-connectors" % "2.24.2",
  "ai.bale.server" %% "commons-kafka" % "1.0.100",
  "im.actor" %% "akka-scalapb-serialization" % "0.1.19",
  "com.thesamet.scalapb" %% "scalapb-runtime" % scalapb.compiler.Version.scalapbVersion % "protobuf",
  "io.grpc" % "grpc-netty" % scalapb.compiler.Version.grpcJavaVersion,
  "com.thesamet.scalapb" %% "scalapb-runtime-grpc" % scalapb.compiler.Version.scalapbVersion,
  "ai.bale" %% "rabbit9-core" % "1.2.12",
  "biz.paluch.logging" % "logstash-gelf" % "1.8.0",
  "org.scalacheck" %% "scalacheck" % "1.13.4" % "test",
  "org.scalatest" %% "scalatest" % "3.0.3" % "test",
  "com.typesafe.akka" %% "akka-http-testkit" % "10.1.5",
  "org.bouncycastle" % "bcprov-jdk15on" % "1.56",
  "it.sauronsoftware.cron4j" % "cron4j" % "2.2.5",
  "commons-validator" % "commons-validator" % "1.6"


)


PB.targets in Compile := Seq(
  scalapb.gen() -> (sourceManaged in Compile).value
)

PB.targets in Compile := Seq(
  scalapb.gen(grpc = false) -> (sourceManaged in Compile).value
)

fork in run := false

dockerBaseImage := "openjdk:8-jre-alpine"
daemonUserUid in Docker := None
daemonUser in Docker := "daemon"
packageName in Docker := "mehdimousavi1995/template"
version in Docker := (version in ThisBuild).value
dockerExposedPorts := Seq(80)
dockerUpdateLatest := true
logBuffered in Test := false
fork in run := false
