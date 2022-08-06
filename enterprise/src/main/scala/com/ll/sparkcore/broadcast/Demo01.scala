package com.ll.sparkcore.broadcast

import org.apache.spark.broadcast.Broadcast
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @Author lin_li
 * @Date 2022/4/24 14:29
 */
object Demo01 {
  def main(args: Array[String]): Unit = {
    //创建Sparkcore程序入口sparkcontext
    val sc: SparkContext = new SparkContext(new SparkConf().setAppName("broadcast").setMaster("local[*]"))

    val rdd: RDD[String] = sc.makeRDD(List("WARN:Class Not Find", "INFO:Class Not Find", "DEBUG:Class Not Find"))
    val broad: Broadcast[String] = sc.broadcast("WARN")

    rdd.filter(_.contains(broad.value)).foreach(println)

    //关闭连接
    sc.stop()
  }
}
