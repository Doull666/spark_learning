package com.ll.test

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @Author lin_li
 * @Date 2022/1/13 16:01
 */
object AdsTop3Test01 {
  def main(args: Array[String]): Unit = {
    val conf: SparkConf = new SparkConf().setAppName("AdsTop3").setMaster("local[*]")
    val sc = new SparkContext(conf)

    //从文件中读取每一行数据
    val txtRDD: RDD[String] = sc.textFile("input/agent.log")

    //对每一行数据进行转换，（省份_广告，1）
    val mapRDD: RDD[(String, Int)] = txtRDD.map {
      line =>
        val arr: Array[String] = line.split(" ")
        ((arr(1) + "_" + arr(4), 1))
    }

    //对数据进行求和，(省份_广告,sum)
    val sortRDD: RDD[(String, Int)] = mapRDD.reduceByKey(_ + _).sortByKey(false)

    val groupRDD: RDD[(String, Iterable[(String, Int)])] = sortRDD.map {
      case (key, sum) => {
        val arr: Array[String] = key.split("_")
        (arr(0), (arr(1), sum))
      }
    }.groupByKey()

    val resultRDD: RDD[(String, List[(String, Int)])] = groupRDD.mapValues {
      datas => {
        datas.toList.sortWith(
          (l, r) => {
            l._2 > r._2
          }
        ).take(3)
      }
    }

    resultRDD.collect().foreach(println)

    sc.stop()
  }
}
