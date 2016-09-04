
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
  )

lazy val crossJVM = crosslib.jvm
lazy val crossJS = crosslib.js
