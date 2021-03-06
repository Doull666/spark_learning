package com.ll.sparkcore.require01

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable.ListBuffer

/**
 * @Author lin_li
 * @Date 2022/2/9 14:19
 */
object Method03 {
  def main(args: Array[String]): Unit = {
    val conf: SparkConf = new SparkConf().setAppName("top10").setMaster("local[*]")
    val sc = new SparkContext(conf)

    val dataRDD: RDD[String] = sc.textFile("input/user_visit_action1.txt")

    val actionRDD: RDD[UserVisitAction] = dataRDD.map {
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

    val infoRDD: RDD[CategoryCountInfo] = actionRDD.flatMap {
      action => {
        action match {
          case act: UserVisitAction => {
            if (act.click_category_id != -1) {
              List(CategoryCountInfo(act.click_category_id.toString, 1, 0, 0))
            } else if (act.order_category_ids != "null") {
              val ids: Array[String] = act.order_category_ids.split(",")
              val list = new ListBuffer[CategoryCountInfo]
              for (id <- ids) {
                list.append(CategoryCountInfo(id, 0, 1, 0))
              }
              list
            } else if (act.pay_category_ids != "null") {
              val ids: Array[String] = act.pay_category_ids.split(",")
              val list = new ListBuffer[CategoryCountInfo]
              for (id <- ids) {
                list.append(CategoryCountInfo(id, 0, 0, 1))
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

    val groupRDD: RDD[(String, Iterable[CategoryCountInfo])] = infoRDD.groupBy(_.categoryId)

    val mapRDD: RDD[CategoryCountInfo] = groupRDD.mapValues {
      datas => {
        datas.reduce {
          (info1, info2) => {
            info1.clickCount = info1.clickCount + info2.clickCount
            info1.orderCount = info1.orderCount + info2.orderCount
            info1.payCount = info1.payCount + info2.payCount
            info1
          }
        }
      }
    }.map(_._2)

    val sortRDD: RDD[CategoryCountInfo] = mapRDD.sortBy(
      data => {
        (data.clickCount, data.orderCount, data.payCount)
      }
      , false)

    sortRDD.take(10).foreach(println)

    sc.stop()
  }
}


//?????????????????????
case class UserVisitAction(date: String, //???????????????????????????
                           user_id: Long, //?????????ID
                           session_id: String, //Session???ID
                           page_id: Long, //???????????????ID
                           action_time: String, //??????????????????
                           search_keyword: String, //????????????????????????
                           click_category_id: Long, //????????????????????????ID
                           click_product_id: Long, //??????????????????ID
                           order_category_ids: String, //??????????????????????????????ID??????
                           order_product_ids: String, //??????????????????????????????ID??????
                           pay_category_ids: String, //??????????????????????????????ID??????
                           pay_product_ids: String, //??????????????????????????????ID??????
                           city_id: Long) //?????? id
// ???????????????
case class CategoryCountInfo(categoryId: String, //??????id
                             var clickCount: Long, //????????????
                             var orderCount: Long, //????????????
                             var payCount: Long) //????????????
