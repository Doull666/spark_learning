package com.ll.sparktest

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @Author lin_li
 * @Date 2022/1/4 21:41
 */
object test {
  def main(args: Array[String]): Unit = {
    val conf: SparkConf = new SparkConf().setAppName("WordCount").setMaster("local[2]")
    val sc = new SparkContext(conf)

    val lineRDD: RDD[String] = sc.textFile("input")

    val flatRDD: RDD[String] = lineRDD.flatMap(_.split(" "))

    val mapRDD: RDD[(String, Int)] = flatRDD.map((_, 1))

    val resultRDD: RDD[(String, Int)] = mapRDD.reduceByKey((_ + _))

    resultRDD.collect().foreach(println)

    sc.stop()

  }
}
