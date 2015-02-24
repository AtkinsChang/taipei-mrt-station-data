lazy val default = Seq(
  organization := "edu.nccu.plsm.geo",
  scalaVersion := "2.11.5"
)

lazy val root = (project in file("."))
  .settings(default: _*)
  .settings(
    name := "mrt-data",
    libraryDependencies ++= Seq(
      "edu.nccu.plsm.geo" %% "datum-convert" % "0.0.2-SNAPSHOT",
      "org.jsoup" % "jsoup" % "1.8.1",
      "com.fasterxml.jackson.core" % "jackson-databind" % "2.5.1",
      "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.5.1",
      "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",
      "ch.qos.logback" % "logback-core" % "1.1.2",
      "ch.qos.logback" % "logback-classic" % "1.1.2"
    )
  )
