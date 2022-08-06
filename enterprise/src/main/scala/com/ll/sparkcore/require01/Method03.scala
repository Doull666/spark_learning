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


//用户访问动作表
case class UserVisitAction(date: String, //用户点击行为的日期
                           user_id: Long, //用户的ID
                           session_id: String, //Session的ID
                           page_id: Long, //某个页面的ID
                           action_time: String, //动作的时间点
                           search_keyword: String, //用户搜索的关键词
                           click_category_id: Long, //某一个商品品类的ID
                           click_product_id: Long, //某一个商品的ID
                           order_category_ids: String, //一次订单中所有品类的ID集合
                           order_product_ids: String, //一次订单中所有商品的ID集合
                           pay_category_ids: String, //一次支付中所有品类的ID集合
                           pay_product_ids: String, //一次支付中所有商品的ID集合
                           city_id: Long) //城市 id
// 输出结果表
case class CategoryCountInfo(categoryId: String, //品类id
                             var clickCount: Long, //点击次数
                             var orderCount: Long, //订单次数
                             var payCount: Long) //支付次数
