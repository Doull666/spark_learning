package com.ll.keyvalue

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @Author lin_li
 * @Date 2022/1/10 10:55
 */
object mapValuesValue {
  def main(args: Array[String]): Unit = {
    val conf: SparkConf = new SparkConf().setAppName("keyvalue").setMaster("local[*]")
    val sc = new SparkContext(conf)

    val rdd: RDD[(Int, String)] = sc.makeRDD(Array((3, "aa"), (6, "cc"), (2, "bb"), (1, "dd")), 3)


    rdd.mapValues(_ + "|||").collect().foreach(println)

    sc.stop()
  }
}