package com.ll.sparkcore.demo

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object Action {
  def main(args: Array[String]): Unit = {
    //创建sparkcore的入库，sparkContext
    val sc: SparkContext = new SparkContext(new SparkConf().setAppName("action").setMaster("local[*]"))

    val dataRDD: RDD[Int] = sc.makeRDD(1 to 4, 2)
    dataRDD.mapPartitionsWithIndex((index, items) => items.map((index, _))).collect().foreach(println)

    println("=====================-------------------------------------------")
    //    println(dataRDD.reduce(_ + _))

    //    println(dataRDD.collect())

    //    println(dataRDD.count())
    //    println(dataRDD.first())
    //    dataRDD.take(3).foreach(println)
    //    dataRDD.takeOrdered(2)

    //    println(dataRDD.aggregate(10)(_ + _, _ + _))
    //    println(dataRDD.fold(10)(_ + _))
    //    println(dataRDD.cout
    dataRDD.collect().foreach(println)
    //断开连接
    sc.stop()
  }
}
