package com.yungoal.cep

import scala.collection.JavaConversions.mapAsScalaMap
import scala.collection.JavaConversions._

import org.apache.spark.{SparkConf, SparkContext}

object WordCount {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()
      .setAppName("Word Count")
      .setMaster("local[*]")
    val sc = new SparkContext(conf)

    println("Hello world")
    printEvn("STORAGE_NAME")
    printEvn("spark.appMasterEnv.STORAGE_KEY")
    printEvn("JAVA_HOME")

    val environmentVars = System.getenv()
    for ((k,v) <- environmentVars) printf("key: %s, value: %s\n", k, v)



    sc.stop()
    println("OK")
  }

  def printEvn(key: String): Unit = {
    var storageName = sys.env.get(key)
    println(key + " " + storageName.getOrElse("NULL"))
  }
}

