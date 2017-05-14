
name := "pepoffsets"

version := "1.0"

scalaVersion := "2.10.3"

javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

libraryDependencies ++= Seq(
  "org.coursera" % "metrics-datadog" % "1.1.6"
)
  .map(_.exclude("ch.qos.logback","logback-classic"))
  .map(_.exclude("ch.qos.logback","logback-classic"))

val meta = """META.INF(.)*""".r

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard
  case meta(_) => MergeStrategy.discard // invalid signature file digest for manifest main attributes from spark.sql
  case _ => MergeStrategy.first
}

assemblyExcludedJars in assembly := {
  val cp = (fullClasspath in assembly).value
  cp filter {_.data.getName == "koma-0.3.0.jar"}
}

parallelExecution in Test := false

updateOptions := updateOptions.value.withCachedResolution(true)



    