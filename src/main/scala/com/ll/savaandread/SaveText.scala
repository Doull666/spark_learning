package com.ll.savaandread

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @Author lin_li
 * @Date 2022/1/20 19:58
 */
object SaveText {
  def main(args: Array[String]): Unit = {
    val conf: SparkConf = new SparkConf().setAppName("text").setMaster("local[*]")
    val sc = new SparkContext(conf)

    val lineRDD: RDD[String] = sc.textFile("input/demo.txt")

    import scala.util.parsing.json.JSON
    val resultRDD: RDD[Option[Any]] = lineRDD.map(JSON.parseFull)

    resultRDD.collect().foreach(println)


    sc.stop()
  }
}
