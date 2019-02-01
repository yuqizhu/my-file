import sbtassembly.AssemblyPlugin.autoImport.{assemblyJarName, assemblyOption}


name := "KafkaRawZone"

version := "0.1"

scalaVersion := "2.11.12"

val sparkVersion = "2.3.2"

dependencyOverrides += "com.fasterxml.jackson.core" % "jackson-core" % "2.8.7"
dependencyOverrides += "com.fasterxml.jackson.core" % "jackson-databind" % "2.8.7"
dependencyOverrides += "com.fasterxml.jackson.module" % "jackson-module-scala_2.11" % "2.8.7"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion % "provided",
  "org.apache.spark" %% "spark-sql" % sparkVersion % "provided",
  "org.apache.spark" %% "spark-streaming" % sparkVersion % "provided",
  "org.apache.spark" %% "spark-streaming-kafka-0-10" % sparkVersion excludeAll(
    ExclusionRule(organization = "org.spark-project.spark", name = "unused"),
    ExclusionRule(organization = "org.apache.spark", name = "spark-streaming"),
    ExclusionRule(organization = "org.apache.hadoop"),
  ),
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.0",
  "net.logstash.logback" % "logstash-logback-encoder" % "5.2",
  // https://mvnrepository.com/artifact/com.microsoft.azure/azure-storage-blob
  "com.microsoft.azure" % "azure-storage-blob" % "10.2.0",
  // https://mvnrepository.com/artifact/org.jmockit/jmockit
  "org.jmockit" % "jmockit" % "1.8" % Test

)


assemblyMergeStrategy in assembly := {
  case PathList(ps@_*) if ps.last endsWith ".properties" => MergeStrategy.first
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}

lazy val commonSettings = Seq(
  version := "0.1-SNAPSHOT",
  organization := "com.yungoal",
  scalaVersion := "2.11.12",
  test in assembly := {}
)

lazy val app = (project in file("app")).
  settings(commonSettings: _*).
  settings(
    mainClass in assembly := Some("com.yungoal.cep.MessageToBlob"),
    // more settings here ...
    assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false),
    assemblyJarName in assembly := s"${name.value}-${version.value}.jar"
    //    libraryDependencies ++= depends

  )
