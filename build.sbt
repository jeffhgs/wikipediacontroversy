
version := "1.0"

name := "wikiedits"

scalaVersion := "2.11.8"

lazy val sparkVersion = "2.4.0"

libraryDependencies += "org.apache.spark" %% "spark-core" % sparkVersion % "provided"
libraryDependencies += "org.apache.spark" %% "spark-sql" % sparkVersion % "provided"

libraryDependencies += "org.apache.commons" % "commons-compress" % "1.18"
libraryDependencies += "com.upplication" % "s3fs" % "2.2.2"

assemblyMergeStrategy in assembly := {
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}
