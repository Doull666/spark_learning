package com.ll.test

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @Author lin_li
 * @Date 2022/1/13 17:17
 */
object AdsTop3Test02 {
  def main(args: Array[String]): Unit = {
    val conf: SparkConf = new SparkConf().setAppName("AdsTop3").setMaster("local[*]")
    val sc = new SparkContext(conf)

    val rdd: RDD[String] = sc.textFile("input/agent.log")

    val mapRDD: RDD[(String, Int)] = rdd.map {
      line => {
        val arr: Array[String] = line.split(" ")
        (arr(1) + "_" + arr(4), 1)
      }
    }

    val reduceRDD: RDD[(String, Int)] = mapRDD.reduceByKey(_ + _)

    val groupRDD: RDD[(String, Iterable[(String, Int)])] = reduceRDD.map {
      case (prvAndAdv, sum) => {
        val arr: Array[String] = prvAndAdv.split("_")
        (arr(0), (arr(1), sum))
      }
    }.groupByKey()

    val resultRDD: RDD[(String, List[(String, Int)])] = groupRDD.mapValues {
      datas => {
        datas.toList.sortWith(
          (left, right) => {
            left._2 > right._2
          }
        ).take(3)
      }
    }

    resultRDD.collect().foreach(println)

    sc.stop()
  }
}
