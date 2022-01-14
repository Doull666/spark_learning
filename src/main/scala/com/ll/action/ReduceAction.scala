package com.ll.action

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @Author lin_li
 * @Date 2022/1/13 22:33
 */
object ReduceAction {
  def main(args: Array[String]): Unit = {
    val conf: SparkConf = new SparkConf().setAppName("action").setMaster("local[*]")
    val sc = new SparkContext(conf)

//    val rdd: RDD[(String, Int)] = sc.makeRDD(List(("a", 2), ("b", 4), ("a", 6)))

    val rdd: RDD[Int] = sc.makeRDD(List(1, 2, 5, 4))
    println(rdd.takeOrdered(3).mkString(","))

    sc.stop()

  }
}
