package com.ll.sparkcore.require01

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @Author lin_li
 * @Date 2022/2/7 11:37
 *
 *       需求：Top10热门商品
 *      1.按照每个品类的点击、下单、支付的量来统计热门品类
 *
 */
object Method01 {
  def main(args: Array[String]): Unit = {
    val conf: SparkConf = new SparkConf().setAppName("Top10").setMaster("local[*]")
    val sc = new SparkContext(conf)

    // 按行读取文件数据，2019-07-17_95_26070e87-1ad7-49a3-8fb3-cc741facaddf_37_2019-07-17 00:00:02_手机_-1_-1_null_null_null_null_3
    val dataRDD: RDD[String] = sc.textFile("input/user_visit_action1.txt")

    /*//按照品类的点击来进行排序
    val clickRDD: RDD[(String, Int)] = dataRDD.map {
      line => {
        val arr: Array[String] = line.split("_")
        (arr(6), 1)
      }
    }
    val sumRDD: RDD[(String, Int)] = clickRDD.reduceByKey(_ + _)
    val sortRDD: RDD[(String, Int)] = sumRDD.sortBy(_._2, false)*/

    /*//按照品类的下单进行排序
    val mapRDD: RDD[String] = dataRDD.map {
      line => {
        val arr: Array[String] = line.split("_")
        arr(8)
      }
    }
    val flatMapRDD: RDD[String] = mapRDD.flatMap(_.split(","))
    val mapRDD1: RDD[(String, Int)] = flatMapRDD.map((_, 1))
    val sumRDD: RDD[(String, Int)] = mapRDD1.reduceByKey(_ + _)
    val sortRDD: RDD[(String, Int)] = sumRDD.sortBy(_._2, false)*/

    /*//按照品类的支付进行排序
    val mapRDD1: RDD[String] = dataRDD.map {
      line => {
        val arr: Array[String] = line.split("_")
        arr(10)
      }
    }
    val flatMapRDD: RDD[String] = mapRDD1.flatMap(_.split(","))
    val resultRDD: RDD[(String, Int)] = flatMapRDD.map((_, 1)).reduceByKey(_ + _).sortBy(_._2, false)*/

    /*//按照综合排名进行排序,使用union函数将所有RDD汇总
    val mapRDD1: RDD[(String, Double)] = dataRDD.map {
      line => {
        val arr: Array[String] = line.split("_")
        (arr(6), 1 * 0.2)
      }
    }.reduceByKey(_ + _)

    val mapRDD2: RDD[(String, Double)] = dataRDD.map {
      line => {
        val arr: Array[String] = line.split("_")
        arr(8)
      }
    }.flatMap(_.split(",")).map((_, 1 * 0.3)).reduceByKey(_ + _)

    val mapRDD3: RDD[(String, Double)] = dataRDD.map {
      line => {
        val arr: Array[String] = line.split("_")
        arr(10)
      }
    }.flatMap(_.split(",")).map((_, 1 * 0.5)).reduceByKey(_ + _).sortBy(_._2)

    val unionRDD: RDD[(String, Double)] = mapRDD1.union(mapRDD2).union(mapRDD3)
    val reduceRDD: RDD[(String, Double)] = unionRDD.reduceByKey(_ + _)
    val resultRDD: Array[(String, Double)] = reduceRDD.sortBy(_._2, false).take(10)
*/

    //先按照点击数排名，靠前的就排名高；如果点击数相同，再比较下单数；下单数再相同，就比较支付数
    val mapRDD1: RDD[(String, Int)] = dataRDD.map {
      line => {
        val arr: Array[String] = line.split("_")
        (arr(6), 1)
      }
    }.reduceByKey(_ + _)

    val mapRDD2: RDD[(String, Int)] = dataRDD.map {
      line => {
        val arr: Array[String] = line.split("_")
        arr(8)
      }
    }.flatMap(_.split(",")).map((_, 1)).reduceByKey(_ + _)

    val mapRDD3: RDD[(String, Int)] = dataRDD.map {
      line => {
        val arr: Array[String] = line.split("_")
        arr(10)
      }
    }.flatMap(_.split(",")).map((_, 1)).reduceByKey(_ + _).sortBy(_._2)

    val unionRDD: RDD[(String, Int)] = mapRDD1.union(mapRDD2).union(mapRDD3)

    val reduceRDD: RDD[(String, String)] = unionRDD.map {
      case (k, v) => {
        (k, v + "")
      }
    }.reduceByKey(_ + "_" + _)

    val sortRDD: RDD[(String, String)] = reduceRDD.map {
      case (k, v) => {
        (v, k)
      }
    }.sortByKey(false)

    val resultRDD: Array[(String, String)] = sortRDD.take(10).map {
      case (k, v) => {
        (v, k)
      }
    }

    resultRDD.foreach(println)


    sc.stop()
  }
}
