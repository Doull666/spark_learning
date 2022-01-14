package com.ll.transformation.value

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @Author lin_li
 * @Date 2022/1/5 21:34
 */
object glom_value {
  def main(args: Array[String]): Unit = {
    val conf: SparkConf = new SparkConf().setAppName("map").setMaster("local[*]")
    val sc = new SparkContext(conf)

    val mapRDD: RDD[Int] = sc.makeRDD(List(1, 2, 3, 4),2)
    mapRDD.glom().map(_.max).collect().foreach(println)
    sc.stop()


  }

}
