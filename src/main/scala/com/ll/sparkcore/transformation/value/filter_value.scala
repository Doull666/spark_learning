package com.ll.sparkcore.transformation.value

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @Author lin_li
 * @Date 2022/1/5 21:34
 */
object filter_value {
  def main(args: Array[String]): Unit = {
    val conf: SparkConf = new SparkConf().setAppName("map").setMaster("local[*]")
    val sc = new SparkContext(conf)

    val mapRDD: RDD[Int] = sc.makeRDD(List(1, 2, 3, 4),2)
    mapRDD.filter(_%2==0).collect().foreach(println)
    sc.stop()


  }

}
