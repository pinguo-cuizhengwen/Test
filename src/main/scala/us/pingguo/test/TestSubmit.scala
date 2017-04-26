package us.pingguo.test

import com.typesafe.config.ConfigFactory
import org.apache.spark.sql.SparkSession
import us.pinguo.bigdata.spark.SparkJob
import scala.collection.Map

/**
  * Created by pinguo on 17/4/26.
  */
object TestSubmit extends SparkJob {
  def main(args: Array[String]): Unit = {
    val config = ConfigFactory.load("url.properties")
    val esNodes = config.getString("es.nodes")
    val esRemote = config.getBoolean("es.remote")
    val sparkConf = createSparkConf("photo-tagging", config)
    sparkConf.set("spark.task.maxFailures", "12")
    val esConf: Map[String, String] = createESConf(esNodes, esRemote)
    val ss = SparkSession.builder().config(sparkConf).getOrCreate()
    ss.read.json(config.getString("s3.path")).take(100).foreach(println)
  }

  def createESConf(esNodes: String, isDebug: Boolean = false): Map[String, String] = {
    val conf = Map(
      "es.nodes" -> esNodes,
      "es.write.operation" -> "upsert",
      "es.output.json" -> "true",
      "es.http.timeout" -> "30s",
      "es.action.heart.beat.lead" -> "3m",
      "es.mapping.id" -> "pid"
    )
    if (isDebug) {
      conf ++ Map(
        "es.nodes.discovery" -> "false",
        "es.nodes.wan.only" -> "true",
        "es.scroll.limit" -> "100"
      )
    } else {
      conf
    }
  }
}
