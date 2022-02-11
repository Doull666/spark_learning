package com.ll.sparkcore.require01

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable.ListBuffer

/**
 * @Author lin_li
 * @Date 2022/2/8 10:18
 */
object Method02 {
  def main(args: Array[String]): Unit = {
    val conf: SparkConf = new SparkConf().setAppName("top10").setMaster("local[*]")
    val sc = new SparkContext(conf)

    //读取数据
    val dataRDD: RDD[String] = sc.textFile("input/user_visit_action1.txt")

    val mapRDD1: RDD[String] = dataRDD.map {
      line => {
        val arr: Array[String] = line.split("_")
        (arr(6) + "_click" + "|" + arr(8) + "_order" + "|" + arr(10) + "_pay")
      }
    }

    val flatMapRDD: RDD[String] = mapRDD1.flatMap(_.split("\\|"))
    val mapRDD2: RDD[(String, String)] = flatMapRDD.map {
      line => {
        val arr: Array[String] = line.split("_")
        (arr(1), arr(0))
      }
    }


    //(pay,15,1,20,6,4)
    val mapRDD3: RDD[String] = mapRDD2.map {
      case (k, v) => {
        val arr: Array[String] = v.split(",")
        var demo = ""
        for (elem <- arr) {
          demo += elem + "_" + k+"|"
        }
        demo
      }
    }
    //15_order
    val flatMapRDD2: RDD[String] = mapRDD3.flatMap(_.split("\\|"))

    val reduceRDD: RDD[(String, Int)] = flatMapRDD2.map((_, 1)).reduceByKey(_ + _)
    val value: RDD[(String, String)] = reduceRDD.map {
      case (k, v) => {
        val arr: Array[String] = k.split("_")
        (arr(0), arr(1) + "_" + v)
      }
    }

    val value1: RDD[(String, Iterable[String])] = value.groupByKey()
    val value2: RDD[(String, List[String])] = value1.mapValues {
      datas => {
        datas.toList.sortWith(
          (left, right) => {
            left < right
          }
        )
      }
    }

    val value3: RDD[(String, ListBuffer[Int])] = value2.map {
      case (k, list) => {
        val l = new ListBuffer[Int]
        for (elem <- list) {
          val arr: Array[String] = elem.split("_")
          l.append(arr(1).toInt)
        }
        (k, l)
      }
    }



    value3.collect().foreach(println)


    sc.stop()
  }
}
