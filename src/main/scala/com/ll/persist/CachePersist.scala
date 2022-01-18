package com.ll.persist

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object CachePersist {
  def main(args: Array[String]): Unit = {
    val conf: SparkConf = new SparkConf().setAppName("cache").setMaster("local[*]")
    val sc = new SparkContext(conf)

    val lineRDD: RDD[String] = sc.textFile("input/demo.txt")

    val wordRDD: RDD[String] = lineRDD.flatMap(_.split(" "))

    val mapRDD: RDD[(String, Int)] = wordRDD.map {
      word => {
        println("***************")
        (word, 1)
      }
    }


    mapRDD.collect()

    println("--------------------------------")
    mapRDD.collect()


    sc.stop()
  }
}
