package com.ll.sparkcore.demo

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object DoubleValue {
  def main(args: Array[String]): Unit = {
    //创建SparkConf
    val conf: SparkConf = new SparkConf().setAppName("double_value").setMaster("local[*]")

    //创建SparkContext
    val sc: SparkContext = new SparkContext(conf)

    val rdd1: RDD[Int] = sc.makeRDD(1 to 6,3)
    val rdd2: RDD[Int] = sc.makeRDD(7 to 12,3)

    //intersection
//    val interRDD: RDD[Int] = rdd1.intersection(rdd2)

    //union
//    val unionRDD: RDD[Int] = rdd1.union(rdd2)

    //subtract
//    val subRDD: RDD[Int] = rdd1.subtract(rdd2)

    //zip
    val zipRDD: RDD[(Int, Int)] = rdd1.zip(rdd2)

    //打印
    zipRDD.collect().foreach(println)

    //断开连接
    sc.stop()

  }
}
