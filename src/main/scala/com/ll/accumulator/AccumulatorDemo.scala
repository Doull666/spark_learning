package com.ll.accumulator

import org.apache.spark.rdd.RDD
import org.apache.spark.util.LongAccumulator
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @Author lin_li
 * @Date 2022/1/21 19:18
 */
object AccumulatorDemo {
  def main(args: Array[String]): Unit = {
    val conf: SparkConf = new SparkConf().setAppName("acc").setMaster("local[*]")
    val sc = new SparkContext(conf)

    val dataRDD: RDD[(String, Int)] = sc.makeRDD(List(("a", 1), ("a", 2), ("a", 3), ("a", 4)))

    val sum: LongAccumulator = sc.longAccumulator("sum")
    dataRDD.foreach {
      case (k, v) => {
        sum.add(v)
      }
    }
    println("-----------------------------------------------")
    println(sum.value)

    sc.stop()
  }
}
