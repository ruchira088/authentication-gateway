import sbt._

object Dependencies
{
  val ScalaVersion = "2.13.6"
  val Http4sVersion = "0.23.1"
  val CirceVersion = "0.14.1"

  lazy val http4sDsl = "org.http4s" %% "http4s-dsl" % Http4sVersion

  lazy val http4sBlazeServer = "org.http4s" %% "http4s-blaze-server" % Http4sVersion

  lazy val http4sBlazeClient = "org.http4s" %% "http4s-blaze-client" % Http4sVersion

  lazy val http4sCirce = "org.http4s" %% "http4s-circe" % Http4sVersion

  lazy val circeGeneric = "io.circe" %% "circe-generic" % CirceVersion

  lazy val circeParser = "io.circe" %% "circe-parser" % CirceVersion

  lazy val circeLiteral = "io.circe" %% "circe-literal" % CirceVersion

  lazy val jodaTime = "joda-time" % "joda-time" % "2.10.10"

  lazy val jbcrypt = "org.mindrot" % "jbcrypt" % "0.4"

  lazy val pureconfig = "com.github.pureconfig" %% "pureconfig" % "0.16.0"

  lazy val logbackClassic = "ch.qos.logback" % "logback-classic" % "1.2.5"

  lazy val kindProjector = "org.typelevel" %% "kind-projector" % "0.13.0" cross CrossVersion.full

  lazy val scalaTypedHoles = "com.github.cb372" % "scala-typed-holes" % "0.1.9" cross CrossVersion.full

  lazy val betterMonadicFor = "com.olegpy" %% "better-monadic-for" % "0.3.1"

  lazy val scalaMock = "org.scalamock" %% "scalamock" % "5.1.0"

  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.2.9"

  lazy val pegdown = "org.pegdown" % "pegdown" % "1.6.0"
}
