package com.ll.test

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @Author lin_li
 * @Date 2022/1/13 19:13
 *
 * 作业：自定义一个分区，将key相同的数据放进一个分区
 */
object AdsTop3Test03 {
  def main(args: Array[String]): Unit = {
    val conf: SparkConf = new SparkConf().setAppName("AdsTop3").setMaster("local[*]")
    val sc = new SparkContext(conf)

    val dataRDD: RDD[String] = sc.textFile("input/agent.log")

    val mapRDD: RDD[(String, Int)] = dataRDD.map {
      line => {
        val arr: Array[String] = line.split(" ")
        (arr(1) + "_" + arr(4), 1)
      }
    }


    sc.stop()
  }
}
