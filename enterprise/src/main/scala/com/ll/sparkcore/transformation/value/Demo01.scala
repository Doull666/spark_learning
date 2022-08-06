package com.ll.sparkcore.transformation.value

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @Author lin_li
 * @Date 2022/4/26 12:15
 */
object Demo01 {
  def main(args: Array[String]): Unit = {
    //创建sparkcore的程序入口sparkcontext
    val sc: SparkContext = new SparkContext(new SparkConf().setAppName("demo01").setMaster("local[*]"))

    val lineRDD: RDD[String] = sc.textFile("")

    //rdd->df,简单转为复杂
//    val ppRDD=lineRDD.map{
//      line=>{
//        val arr: Array[String] = line.split(",")
////        People(arr(0).trim,arr(1).trim.toInt)
//      }
//    }.toDF

    //断开连接
    sc.stop()
  }
}
