import sbt._

import Keys._

object DroolsBuild extends Build {
  override val settings = super.settings ++ Seq (
    name := "TestDrools",
    version := "0.1",
    scalaVersion := "2.11.0"
  )

  lazy val root = Project(
    id="drools",
    base=file("."),
    settings=Project.defaultSettings ++ Seq(
      resolvers += "Maven Central Server" at "http://repo1.maven.org/maven2",
      resolvers += "JBoss" at "http://repository.jboss.org/nexus/content/repositories/releases/",

      libraryDependencies += "com.github.nscala-time" %% "nscala-time" % "1.2.0",
      libraryDependencies += "org.drools" % "drools-compiler" % "6.1.0.CR1",
      libraryDependencies += "org.drools" % "drools-core" % "6.1.0.CR1",
      libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.2.0" % "test"
    ))

}
