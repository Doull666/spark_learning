package com.ll.sparkcore.demo

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object Value {
  def main(args: Array[String]): Unit = {
    //创建sparkconf
    val conf: SparkConf = new SparkConf().setAppName("demo").setMaster("local[*]")

    //创建sparkcontext
    val sc: SparkContext = new SparkContext(conf)

    //创建rdd
    val dataRDD: RDD[Int] = sc.makeRDD(List(2, 1, 3, 4, 6, 5))
    //    val dataRDD: RDD[List[Int]] = sc.makeRDD(List(List(1, 2), List(3, 4), List(5, 6), List(7)), 2)
    //    val dataRDD: RDD[String] = sc.makeRDD(List("hello", "hive", "hadoop", "spark", "scala","hello","hive","scala"))


    //调用transformation算子中的单value算子的map算子
    //    val mapRDD: RDD[Int] = dataRDD.map(x => x * 2)

    //调用mapPatitions算子
    //    val mapPartitionsRDD: RDD[Int] = dataRDD.mapPartitions(datas => datas.map(_ * 2))


    //调用mapPartitionsWithIndex
    //    val mapIndexRDD: RDD[(Int, Int)] = dataRDD.mapPartitionsWithIndex((index, items) => {
    //      items.map(item => (index, item))
    //    })


    //flatmap
    //    val flatRDD: RDD[Int] = dataRDD.flatMap(list => list)

    //glom
    //    val glomRdd: RDD[Int] = dataRDD.glom().map(_.max)

    //groupBy
    //    val groupRDD: RDD[(Char, Iterable[String])] = dataRDD.groupBy(word => word.charAt(0))

    //filter
    //    val filterRDD: RDD[String] = dataRDD.filter(word => word.charAt(0) == 'h')

    //distinct
//    val mapIndexRDD: RDD[(Int, Int)] = dataRDD.mapPartitionsWithIndex((index, datas) => datas.map((index, _)))
//    mapIndexRDD.foreach(println)
//    println("====================================")
//    val coaRDD: RDD[(Int, Int)] = dataRDD.coalesce(6,true).mapPartitionsWithIndex((index, datas) => datas.map((index, _)))
//    coaRDD.foreach(println)

    //sortBy
    val sortRDD: RDD[Int] = dataRDD.sortBy(num => num)

    //打印
    sortRDD.collect().foreach(println)


    //关闭连接
    sc.stop()
  }
}
