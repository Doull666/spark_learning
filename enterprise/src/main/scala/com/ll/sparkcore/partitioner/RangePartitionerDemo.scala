package com.ll.sparkcore.partitioner

import org.apache.spark.rdd.RDD
import org.apache.spark.{HashPartitioner, RangePartitioner, SparkConf, SparkContext}

/**
 * @Author lin_li
 * @Date 2022/1/19 11:48
 */
object RangePartitionerDemo {
  def main(args: Array[String]): Unit = {
    val conf: SparkConf = new SparkConf().setAppName("rangePartitioner").setMaster("local[*]")
    val sc = new SparkContext(conf)
    val rdd: RDD[(String, Int)] = sc.makeRDD(List(("a", 1), ("b", 5), ("c", 5), ("d", 2),("e",6)), 4)
    println(rdd.partitioner)
    rdd.mapPartitionsWithIndex{
      (index,items)=>items.map((index,_))
    }.collect().foreach(println)

    println("----------------------------------------------------")

    val hash: RDD[(String, Int)] = rdd.partitionBy(new HashPartitioner(2))
    println(hash.partitioner)
    hash.mapPartitionsWithIndex{
      (index,items)=>items.map((index,_))
    }.collect().foreach(println)
    println("----------------------------------------------------------")

    val range: RDD[(String, Int)] = rdd.partitionBy(new RangePartitioner(2, rdd))
    println(range.partitioner)
    range.mapPartitionsWithIndex{
      (index,items)=>items.map((index,_))
    }.collect().foreach(println)


    sc.stop()
  }
}
