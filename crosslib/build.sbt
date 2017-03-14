
scalaVersion in ThisBuild := "2.11.8"

lazy val root = project.in(file(".")).
  aggregate(crossJS, crossJVM).
  settings(
    publish := {},
    publishLocal := {}
  )

lazy val crosslib = crossProject.in(file(".")).
  settings(
    name := "crosslib",
    organization := "com.kindone",
    version := "0.1-SNAPSHOT"
  ).
  jvmSettings(
    // Add JVM-specific settings here
  ).
  jsSettings(
    // Add JS-specific settings here
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "0.9.1"
    ),
    jsDependencies ++= Seq(
      "org.webjars.npm" % "js-sha1" % "0.4.0" / "build/sha1.min.js"
    )
  )

lazy val crossJVM = crosslib.jvm
lazy val crossJS = crosslib.js
