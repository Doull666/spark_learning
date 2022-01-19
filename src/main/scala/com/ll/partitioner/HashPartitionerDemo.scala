package com.ll.partitioner

import org.apache.spark.rdd.RDD
import org.apache.spark.{HashPartitioner, SparkConf, SparkContext}

/**
 * @Author lin_li
 * @Date 2022/1/19 11:27
 */
object HashPartitionerDemo {
  def main(args: Array[String]): Unit = {
    val conf: SparkConf = new SparkConf().setAppName("hashPartitioner").setMaster("local[*]")
    val sc = new SparkContext(conf)

    val pairRDD: RDD[(Int, Int)] = sc.makeRDD(List((1, 1), (2, 2), (3, 3)))
    val redceRDD: RDD[(Int, Int)] = pairRDD.reduceByKey(_ + _)
    println(redceRDD.partitioner)
    redceRDD.mapPartitionsWithIndex{
      (index,t)=>(t.map((index,_)))
    }.collect().foreach(println)


    println("-------------------------------------------------------------")


    val partitionRDD: RDD[(Int, Int)] = pairRDD.partitionBy(new HashPartitioner(2))
    println(partitionRDD.partitioner)
    partitionRDD.mapPartitionsWithIndex{
      (index,t)=>t.map((index,_))
    }.collect().foreach(println)

    sc.stop()
  }
}
