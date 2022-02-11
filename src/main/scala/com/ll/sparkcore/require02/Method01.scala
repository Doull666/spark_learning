package com.ll.sparkcore.require02

import org.apache.spark.broadcast.Broadcast
import org.apache.spark.rdd.RDD
import org.apache.spark.util.AccumulatorV2
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.{immutable, mutable}

/**
 * @Author lin_li
 * @Date 2022/2/10 10:02
 */
object Method05 {
  def main(args: Array[String]): Unit = {
    val conf: SparkConf = new SparkConf().setAppName("Top10").setMaster("local[*]")
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

    //创建累加器
    val acc = new CategoryCountAccumulator

    //注册累加器
    sc.register(acc)

    //使用累加器
    actionRDD.foreach {
      action => {
        acc.add(action)
      }
    }

    //获取累加器的值
    val accValue: mutable.Map[(String, String), Long] = acc.value

    //分组
    val groupValue: Map[String, mutable.Map[(String, String), Long]] = accValue.groupBy(_._1._1)

    //变换结构
    val mapValues: immutable.Iterable[CategoryCountInfo] = groupValue.map {
      case (id, map) => {
        val click: Long = map.getOrElse((id, "click"), 0L)
        val order: Long = map.getOrElse((id, "order"), 0L)
        val pay: Long = map.getOrElse((id, "pay"), 0L)

        CategoryCountInfo(id, click, order, pay)
      }
    }

    val top10Info: List[CategoryCountInfo] = mapValues.toList.sortWith {
      (left, right) => {
        if (left.clickCount > right.clickCount) {
          true
        } else if (left.clickCount == right.clickCount) {
          if (left.orderCount > right.orderCount) {
            true
          } else if (left.orderCount == right.orderCount) {
            left.payCount > right.payCount
          } else {
            false
          }
        } else {
          false
        }
      }
    }.take(10)

    //---------------------需求4----------------------
    //变换结构提取处top10的品类id
    val idsList: List[String] = top10Info.map(_.categoryId)

    //把idsList放入广播变量
    val broadCaseIds: Broadcast[List[String]] = sc.broadcast(idsList)

    //过滤出top10的品类id
    val filterRDD: RDD[UserVisitAction] = actionRDD.filter {
      action => {
        if (action.click_category_id != -1) {
          broadCaseIds.value.contains(action.click_category_id.toString)
        } else {
          false
        }
      }
    }

    val idAndSessionToOneRDD: RDD[(String, Int)] = filterRDD.map {
      action => {
        (action.click_category_id + "--" + action.session_id, 1)
      }
    }

    //聚合
    val idAndSessionToSumRDD: RDD[(String, Int)] = idAndSessionToOneRDD.reduceByKey(_ + _)

    //变换结构
    val idToSessionAndSumRDD: RDD[(String, (String, Int))] = idAndSessionToSumRDD.map {
      case (idAndSession, sum) => {
        val keys: Array[String] = idAndSession.split("--")
        (keys(0), (keys(1), sum))
      }
    }

    val idToSessionAndSumGroupRDD: RDD[(String, Iterable[(String, Int)])] = idToSessionAndSumRDD.groupByKey()

    idToSessionAndSumGroupRDD.mapValues {
      datas => {
        datas.toList.sortWith {
          (left, right) => {
            left._2 > right._2
          }
        }.take(10)
      }
    }.foreach(println)

    sc.stop()
  }
}

//自定义累加器
class CategoryCountAccumulator extends AccumulatorV2[UserVisitAction, mutable.Map[(String, String), Long]] {
  var map = mutable.Map[(String, String), Long]()

  override def isZero: Boolean = map.isEmpty

  override def copy(): AccumulatorV2[UserVisitAction, mutable.Map[(String, String), Long]] = {
    new CategoryCountAccumulator()
  }

  override def reset(): Unit = map.clear()

  override def add(action: UserVisitAction): Unit = {
    if (action.click_category_id != -1) {
      val key = (action.click_category_id.toString, "click")
      map(key) = map.getOrElse(key, 0L) + 1L
    } else if (action.order_category_ids != "null") {
      val ids: Array[String] = action.order_category_ids.split(",")
      for (id <- ids) {
        val key = (id, "order")
        map(key) = map.getOrElse(key, 0L) + 1L
      }
    } else if (action.pay_category_ids != "null") {
      val ids: Array[String] = action.pay_category_ids.split(",")
      for (id <- ids) {
        val key = (id, "pay")
        map(key) = map.getOrElse(key, 0L) + 1L
      }
    }
  }

  override def merge(other: AccumulatorV2[UserVisitAction, mutable.Map[(String, String), Long]]): Unit = {
    other.value.foreach {
      case (key, count) => {
        map(key) = map.getOrElse(key, 0L) + count
      }
    }
  }

  override def value: mutable.Map[(String, String), Long] = map
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
