name := "Test"

version := "1.0"

scalaVersion := "2.11.8"

resolvers += "Artifactory" at "http://54.222.244.187:8081/artifactory/bigdata/"

resolvers += "ali-Artifactory" at "http://maven.aliyun.com/nexus/content/groups/public"

publishTo := Some("Artifactory Realm" at "http://54.222.244.187:8081/artifactory/bigdata;build.timestamp=" + new java.util.Date().getTime)

credentials += Credentials(Path.userHome / ".sbt" / "credentials")


organization := "us.pinguo.bigdata" // 组织名称


libraryDependencies += "com.amazonaws" % "amazon-kinesis-client" % "1.7.2"
libraryDependencies +=  "us.pinguo.bigdata" %% "spark-framework" % "0.2-SNAPSHOT"