name := "otravo-test"

version := "0.1"


lazy val versions = new Object {
  val scala = "2.12.6"
  val jodaTime = "2.7"
  val scalaTest = "3.0.5"
  val json4s = "3.5.2"
}

lazy val commonSettings = Seq(

  scalaVersion := versions.scala,
  libraryDependencies ++= Seq(
    "joda-time" % "joda-time" % versions.jodaTime
  )
)



lazy val core = (project in file("core"))
  .settings(
    commonSettings,
    libraryDependencies ++= Seq(
      "org.json4s" %% "json4s-native" % versions.json4s,
      "org.scalatest" %% "scalatest" % versions.scalaTest % "test"
    )
  )

lazy val cli = (project in file("cli"))
  .settings(
    commonSettings,
    mainClass := Some("com.test.cli.Main")
  ).dependsOn(core)