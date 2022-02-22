package com.ll.sparkcore.transformation.doublevalue

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @Author lin_li
 * @Date 2022/2/7 17:16
 */
object UnionValue {
  def main(args: Array[String]): Unit = {
    val conf: SparkConf = new SparkConf().setAppName("union").setMaster("local[*]")
    val sc = new SparkContext(conf)

    val map1: RDD[Int] = sc.makeRDD(1 to 4)
    val map2: RDD[Int] = sc.makeRDD(4 to 8)

    val map3: RDD[Int] = map1.union(map2)

    map3.collect().foreach(println)

    sc.stop()
  }
}
