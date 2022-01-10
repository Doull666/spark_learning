package com.ll.sparktest

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @Author lin_li
 * @Date 2022/1/5 16:55
 */
object spark_test02 {
  def main(args: Array[String]): Unit = {
    val conf: SparkConf = new SparkConf().setAppName("test01").setMaster("local[*]")
    val sc = new SparkContext(conf)

    val txtRDD: RDD[String] = sc.textFile("input")
    txtRDD.foreach(println)

    sc.stop()

  }

}
