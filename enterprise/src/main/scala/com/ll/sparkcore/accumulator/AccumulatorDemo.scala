package com.ll.sparkcore.accumulator

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

    val rdd: RDD[String] = sc.makeRDD(List("Hello", "Hello", "Hello", "Hello", "Hello", "Spark", "Hive"))

    //创建累加器
    val acc = new MyAccumulator
    sc.register(acc,"demo01")

    rdd.foreach{
      word=>{
        acc.add(word)
      }
    }

    println(acc.value)
    sc.stop()
  }
}
