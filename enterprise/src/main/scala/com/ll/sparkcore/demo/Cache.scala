package com.ll.sparkcore.demo

import org.apache.spark.rdd.RDD
import org.apache.spark.storage.StorageLevel
import org.apache.spark.{SparkConf, SparkContext}

object Cache {
  def main(args: Array[String]): Unit = {
    //创建Sparkcore程序入口sparkcontext
    val sc: SparkContext = new SparkContext(new SparkConf().setAppName("cache").setMaster("local[*]"))

    val lineRDD: RDD[String] = sc.textFile("src/main/resources/person.txt")

    val wordRDD: RDD[String] = lineRDD.flatMap(_.split(" "))

    val mapRDD: RDD[(String, Int)] = wordRDD.map(
      word=>{
        println("************")
        (word,1)
      }
    )
    //打印缓存之前的血缘关系
    println(mapRDD.toDebugString)
//    mapRDD.cache()

    //更改储存级别
    mapRDD.persist(StorageLevel.MEMORY_AND_DISK_2)

    mapRDD.collect()
    println("===========================================")

    //打印缓存之后的血缘关系
    println(mapRDD.toDebugString)

    mapRDD.collect()


    //停止连接
    sc.stop()
  }
}
