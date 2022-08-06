package com.ll.sparkcore.stage

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}



object StageDemo {
  def main(args: Array[String]): Unit = {
    val conf: SparkConf = new SparkConf().setAppName("stage").setMaster("local[*]")
    val sc = new SparkContext(conf)

    val lineRDD: RDD[String] = sc.textFile("input/demo.txt",6)

    val resultRDD: RDD[(String, Int)] = lineRDD.flatMap(_.split(" ")).map((_, 1)).reduceByKey(_ + _,2)

    resultRDD.collect()

    resultRDD.saveAsTextFile("output")

    Thread.sleep(1111111100)

    sc.stop()
  }
}
