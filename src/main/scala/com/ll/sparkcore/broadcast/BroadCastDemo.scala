package com.ll.sparkcore.broadcast

import org.apache.spark.broadcast.Broadcast
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object BroadCastDemo {
  def main(args: Array[String]): Unit = {
    val conf: SparkConf = new SparkConf().setAppName("broadcast").setMaster("local[*]")
    val sc = new SparkContext(conf)

    val rdd: RDD[String] = sc.makeRDD(List("WARN:Class Not Find", "INFO:Class Not Find", "DEBUG:Class Not Find"))
    val list =  List("WARN", "INFO")
    //声明广播变量
    val bc: Broadcast[List[String]] = sc.broadcast(list)

    list.foreach{
      word=>{
        rdd.filter(_.contains(word))
      }.collect().foreach(println)
    }


    sc.stop()

  }
}
