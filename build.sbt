name := """watson-language-translator-play-api"""
organization := "com.ibm"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.2"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.0.0" % Test

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.ibm.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.ibm.binders._"

libraryDependencies += ws

resolvers += "Maven central" at "http://repo1.maven.org/maven2/"
libraryDependencies += "com.ibm.watson.developer_cloud" % "java-sdk" % "3.3.1"