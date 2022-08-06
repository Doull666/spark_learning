package com.ll.sparkcore.require01

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable.ListBuffer

/**
 * @Author lin_li
 * @Date 2022/2/9 16:02
 */
object Method04 {
  def main(args: Array[String]): Unit = {
    val conf: SparkConf = new SparkConf().setAppName("top10").setMaster("local[*]")
    val sc = new SparkContext(conf)

    val dataRDD: RDD[String] = sc.textFile("input/user_visit_action1.txt")

    val mapRDD: RDD[UserVisitAction] = dataRDD.map {
      line => {
        val arr: Array[String] = line.split("_")
        UserVisitAction(
          arr(0),
          arr(1).toLong,
          arr(2),
          arr(3).toLong,
          arr(4),
          arr(5),
          arr(6).toLong,
          arr(7).toLong,
          arr(8),
          arr(9),
          arr(10),
          arr(11),
          arr(12).toLong
        )
      }
    }

    val flatMapRDD: RDD[(String, CategoryCountInfo)] = mapRDD.flatMap {
      action => {
        action match {
          case act: UserVisitAction => {
            if (act.click_category_id != -1) {
              List((act.click_category_id.toString, CategoryCountInfo(act.click_category_id.toString, 1, 0, 0)))
            } else if (act.order_category_ids != "null") {
              val ids: Array[String] = act.order_category_ids.split(",")
              val list = new ListBuffer[(String, CategoryCountInfo)]
              for (id <- ids) {
                list.append((id, CategoryCountInfo(id, 0, 1, 0)))
              }
              list
            } else if (act.pay_category_ids != "null") {
              val ids: Array[String] = act.pay_category_ids.split(",")
              val list = new ListBuffer[(String, CategoryCountInfo)]
              for (id <- ids) {
                list.append((id, CategoryCountInfo(id, 0, 0, 1)))
              }
              list
            } else {
              Nil
            }
          }
          case _ => Nil
        }
      }
    }


    //直接使用reduceByKey对数据进行计算
    val reduceRDD: RDD[CategoryCountInfo] = flatMapRDD.reduceByKey {
      (info1, info2) => {
        info1.clickCount += info2.clickCount
        info1.orderCount += info2.orderCount
        info1.payCount += info2.payCount
        info1
      }
    }.map(_._2)

    val sortRDD: RDD[CategoryCountInfo] = reduceRDD.sortBy(
      info => {
        (info.clickCount, info.orderCount, info.payCount)
      }
      , false)

    sortRDD.take(10).foreach(println)

    sc.stop()
  }
}
