name := "Coolon"

organization := "jp.applicative"

version := "0.0.1"

scalaVersion := "2.10.3"

libraryDependencies ++= Seq(
  "org.scalatest" % "scalatest_2.10" % "2.0" % "test" withSources() withJavadoc(),
  "org.scalacheck" %% "scalacheck" % "1.10.0" % "test" withSources() withJavadoc(),
  "io.netty" % "netty-all" % "4.0.12.Final" withSources() withJavadoc(),
  "com.github.philcali" %% "cronish" % "0.1.3" withSources() withJavadoc()
)

initialCommands := "import jp.applicative.coolon._"

