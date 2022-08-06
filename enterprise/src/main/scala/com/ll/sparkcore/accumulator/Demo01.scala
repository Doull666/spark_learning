package com.ll.sparkcore.accumulator

import org.apache.spark.rdd.RDD
import org.apache.spark.util.LongAccumulator
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @Author lin_li
 * @Date 2022/4/24 10:03
 */
object Demo01 {
  def main(args: Array[String]): Unit = {
    //创建sparkcore程序入口sparkcontext
    val sc: SparkContext = new SparkContext(new SparkConf().setAppName("acc").setMaster("local[*]"))

    val dataRDD: RDD[(String, Int)] = sc.makeRDD(List(("a", 1), ("a", 2), ("a", 3), ("a", 4)))

    //累加器可避免shuffle过程
    //定义累加器
    val sum: LongAccumulator = sc.longAccumulator("sum")
    dataRDD.foreach{
      case (k,v)=>{
        //使用累加器
        sum.add(v)
      }
    }

    println(sum.value)

    //断开连接
    sc.stop()
  }
}
