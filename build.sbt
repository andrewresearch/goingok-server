name := "goingok-server"
version := "1.0.0"
scalaVersion := "2.12.2"
organization := "org.goingok"

enablePlugins(JavaAppPackaging)

//Scala library versions
val akkaVersion = "2.5.1"
val akkaStreamVersion = "2.5.1"
val akkaHttpVersion = "10.0.6"
val json4sVersion = "3.5.1"
val slickVersion = "3.2.0"
val slickpgVersion = "0.15.0-RC"
val slf4jVersion = "1.7.25"
//Java library versions
val googleClientApiVersion = "1.22.0"
val postgresDriverVersion = "42.0.0"
//val coreNlpVersion = "3.7.0"

//Akka
libraryDependencies ++= Seq(
  "com.typesafe.akka" % "akka-stream_2.12" % akkaStreamVersion,
  "com.typesafe.akka" % "akka-stream-testkit_2.12" % akkaStreamVersion,
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion
)
//Google dependencies
libraryDependencies += "com.google.api-client" % "google-api-client" % googleClientApiVersion
//Sessions
libraryDependencies ++= Seq(
  "com.softwaremill.akka-http-session" %% "core" % "0.4.0",
  "com.softwaremill.akka-http-session" %% "jwt"  % "0.4.0"
)
//NLP dependencies
libraryDependencies ++= Seq(
  //"edu.stanford.nlp" % "stanford-corenlp" % coreNlpVersion,
  //"edu.stanford.nlp" % "stanford-corenlp" % coreNlpVersion  classifier "models-english" //,
  //"com.google.protobuf" % "protobuf-java" % protoBufVers,
  //("org.languagetool" % "language-en" % languageToolVers)
  //  .exclude("commons-logging", "commons-logging")
)
//Slick
libraryDependencies ++= Seq(
  "com.typesafe.slick" %% "slick" % slickVersion,
  "com.typesafe.slick" %% "slick-hikaricp" % slickVersion,
  "org.postgresql" % "postgresql" % postgresDriverVersion,
  "com.github.tminglei" %% "slick-pg" % slickpgVersion,
  "com.github.tminglei" %% "slick-pg_json4s" % slickpgVersion
)
//General
libraryDependencies ++= Seq(
  "io.nlytx" %% "commons" % "0.1.1",
//  "au.edu.utscic" %% "cic-tap-nlp" % "0.1",
//  "au.edu.utscic" %% "cic-tap-aws" % "0.1",
//  "com.typesafe" % "config" % "1.3.1",
    "org.json4s" %% "json4s-jackson" % json4sVersion,
    "de.heikoseeberger" %% "akka-http-json4s" % "1.11.0",
//  "org.scalactic" %% "scalactic" % "3.0.0",
  "org.scalatest" %% "scalatest" % "3.0.0" % "test",
  "org.slf4j" % "jcl-over-slf4j" % slf4jVersion,
  "ch.qos.logback" % "logback-classic" % "1.1.7"
)

scalacOptions in (Compile, doc) ++= Seq("-doc-root-content", baseDirectory.value+"/src/main/scala/root-doc.md")
