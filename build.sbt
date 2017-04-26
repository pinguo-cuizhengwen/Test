import com.typesafe.sbt.packager.MappingsHelper.contentOf
import sbt.Keys._

name := "Test"

version := "1.0"

scalaVersion := "2.11.8"

resolvers += "Artifactory" at "http://54.222.244.187:8081/artifactory/bigdata/"

resolvers += "ali-Artifactory" at "http://maven.aliyun.com/nexus/content/groups/public"

publishTo := Some("Artifactory Realm" at "http://54.222.244.187:8081/artifactory/bigdata;build.timestamp=" + new java.util.Date().getTime)

credentials += Credentials(Path.userHome / ".sbt" / "credentials")


enablePlugins(JavaAppPackaging)

organization := "us.pinguo.bigdata" // 组织名称

mappings in Universal ++= contentOf(baseDirectory.value / "../script")
mappings in Universal ++= contentOf(baseDirectory.value / "src/main/script")

libraryDependencies += "com.amazonaws" % "amazon-kinesis-client" % "1.7.2"
libraryDependencies +=  "us.pinguo.bigdata" %% "spark-framework" % "0.2-SNAPSHOT"