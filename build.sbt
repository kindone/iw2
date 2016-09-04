import sbt.Project.projectToRef
import play.PlayImport.PlayKeys._

name := """Infinite Wall"""

version := "2.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

lazy val clients = Seq(scalajs)

lazy val scalaV = "2.11.8"

lazy val server = (project in file("server")).settings(
  scalaVersion := scalaV,
  routesImport += "config.Routes._",
  scalaJSProjects := clients,
  pipelineStages := Seq(scalaJSProd, gzip),

  resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases",
  libraryDependencies ++= Seq(
    filters,
    jdbc,
    cache,
    ws,
    evolutions,
    specs2 % Test,
    "com.typesafe.play" %% "anorm" % "2.5.0",
    "com.vmunier" %% "play-scalajs-scripts" % "0.3.0",
    "com.typesafe.slick" %% "slick" % "3.0.2",
    "com.typesafe.play" %% "play-slick" % "1.0.1",
    "com.lihaoyi" %% "upickle" % "0.3.8",
    "org.webjars" %% "webjars-play" % "2.4.0",
    "org.webjars" % "codemirror" % "5.11",
    "org.webjars" % "font-awesome" % "4.4.0",
    "org.webjars" % "bootstrap" % "3.3.5",
    "org.webjars" % "react" % "0.14.7",
    "org.flywaydb" %% "flyway-play" % "2.2.1",
    "org.webjars" % "cryptojs" % "3.1.2",
    "org.mindrot" % "jbcrypt" % "0.3m"
)
 ).enablePlugins(PlayScala).
  aggregate(clients.map(projectToRef): _*).
  dependsOn(sharedJvm)

lazy val scalajs = (project in file("scalajs")).settings(
  scalaVersion := scalaV,
  persistLauncher := true,
  persistLauncher in Test := false,
  libraryDependencies ++= Seq(
    "org.scala-js" %%% "scalajs-dom" % "0.9.0",
    "com.lihaoyi" %%% "scalatags" % "0.5.5",
    "com.lihaoyi" %%% "scalarx" % "0.3.1",
    "be.doeraene" %%% "scalajs-jquery" % "0.9.0",
    "com.lihaoyi" %%% "upickle" % "0.3.8",
    "io.monix" %%% "minitest" % "0.22" % "test"
  ),
  jsDependencies ++= Seq(
    "org.webjars" % "bootstrap" % "3.3.6" / "bootstrap.min.js",
    "org.webjars" % "jquery" % "2.2.4" / "jquery.min.js",
    "org.webjars" % "showdown" % "0.3.1" / "compressed/showdown.js",
    "org.webjars" % "jquery-mousewheel" % "3.1.12" / "jquery.mousewheel.js" dependsOn "jquery.min.js",
    "org.webjars" % "velocity" % "1.1.0" / "velocity.min.js" dependsOn "jquery.min.js"
  )
).enablePlugins(ScalaJSPlugin, ScalaJSPlay).
  dependsOn(sharedJs)

val sharedJvmSettings = List(
  libraryDependencies ++= Seq(
    "com.lihaoyi" %% "upickle" % "0.3.8",
    "com.kindone" %% "crosslib" % "0.1-SNAPSHOT"
  )
)

lazy val shared = (crossProject.crossType(CrossType.Pure) in file("shared")).
  settings(scalaVersion := scalaV).
  jsConfigure(_ enablePlugins ScalaJSPlay).
  jvmSettings(sharedJvmSettings: _*).
  jsSettings(
    libraryDependencies ++= Seq(
      "com.lihaoyi" %%% "upickle" % "0.3.8",
      "com.kindone" %% "crosslib" % "0.1-SNAPSHOT"
    )
  )

lazy val crosslib = project.in(file("crosslib"))

scalaJSStage in Global := FastOptStage

lazy val sharedJvm = shared.jvm
lazy val sharedJs = shared.js



// loads the jvm project at sbt startup
onLoad in Global := (Command.process("project server", _: State)) compose (onLoad in Global).value
//
//seq(flywaySettings: _*)
//
//flywayUrl := "jdbc:h2:file:target/foobar"
//
//flywayUser := "SA"
//
//flywayLocations += "filesystem:conf/db/migration"
//
//resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator


fork in run := true
