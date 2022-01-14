package com.ll.transformation.keyvalue

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @Author lin_li
 * @Date 2022/1/10 10:55
 */
object foldByKeyValue {
  def main(args: Array[String]): Unit = {
    val conf: SparkConf = new SparkConf().setAppName("keyvalue").setMaster("local[*]")
    val sc = new SparkContext(conf)

    val rdd: RDD[(String, Int)] = sc.makeRDD(List(("a", 1), ("b", 5), ("a", 5), ("b", 2), ("c", 10), ("c", 6)), 3)
//        rdd.mapPartitionsWithIndex{
//          (index,datas)=>datas.map((index,_))
//        }.collect().foreach(println)

    rdd.foldByKey(0)(_+_).mapPartitionsWithIndex{
      (index,datas)=>datas.map((index,_))
    }.collect().foreach(println)



    sc.stop()
  }
}