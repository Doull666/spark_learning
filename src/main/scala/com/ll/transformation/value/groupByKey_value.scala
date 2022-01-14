package com.ll.transformation.value

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @Author lin_li
 * @Date 2022/1/5 21:34
 */
object groupByKey_value {
  def main(args: Array[String]): Unit = {
    val conf: SparkConf = new SparkConf().setAppName("map").setMaster("local[*]")
    val sc = new SparkContext(conf)

    val rdd: RDD[String] = sc.textFile("input")

    val groupRDD: RDD[(String, Iterable[(String, Int)])] = rdd.flatMap(_.split(" ")).map((_, 1)).groupBy(_._1)

    groupRDD.map{
      case(key,list)=>(key,list.size)
    }.collect().foreach(println)


    sc.stop()
  }
}
