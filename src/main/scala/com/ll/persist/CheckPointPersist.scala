package com.ll.persist

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @Author lin_li
 * @Date 2022/1/19 10:03
 */
object CheckPointPersist {
  def main(args: Array[String]): Unit = {
    val conf: SparkConf = new SparkConf().setAppName("checkpoint").setMaster("local[*]")
    val sc = new SparkContext(conf)
    sc.setCheckpointDir("./checkpoint")

    val lineRDD: RDD[String] = sc.textFile("input/demo.txt")
    val wordRDD: RDD[String] = lineRDD.flatMap(line => line.split(" "))
    val mapRDD: RDD[(String, Long)] = wordRDD.map((_, System.currentTimeMillis()))

    mapRDD.cache()
    mapRDD.checkpoint()

    mapRDD.collect().foreach(println)
    println("-------------------------------")
    mapRDD.collect().foreach(println)
    println("-------------------------------")
    mapRDD.collect().foreach(println)

    Thread.sleep(1000000000)
    sc.stop()
  }
}
