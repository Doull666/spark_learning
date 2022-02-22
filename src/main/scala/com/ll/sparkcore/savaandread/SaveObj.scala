package com.ll.sparkcore.savaandread

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @Author lin_li
 * @Date 2022/1/20 19:58
 */
object SaveObj {
  def main(args: Array[String]): Unit = {
    val conf: SparkConf = new SparkConf().setAppName("text").setMaster("local[*]")
    val sc = new SparkContext(conf)

    val rdd: RDD[Int] = sc.makeRDD(List(1, 2, 3, 4))
    rdd.saveAsObjectFile("output")
    sc.objectFile[Int]("output").collect().foreach(println)


    sc.stop()
  }
}
