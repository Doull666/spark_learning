package com.ll.sparkcore.savaandread

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @Author lin_li
 * @Date 2022/1/20 19:58
 */
/*
object SaveSeq {
  def main(args: Array[String]): Unit = {
    val conf: SparkConf = new SparkConf().setAppName("text").setMaster("local[*]")
    val sc = new SparkContext(conf)

    val rdd: RDD[(String, Int)] = sc.makeRDD(List(("a", 1), ("b", 2), ("c", 3)))
//    rdd.saveAsSequenceFile("output")

    sc.sequenceFile[String,Int]("output").collect().foreach(println)


    sc.stop()
  }
}
*/
