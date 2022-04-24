package com.ll.sparkcore.demo

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object CheckPoint {
  def main(args: Array[String]): Unit = {
    //设置hadoop集群的用户名
    System.setProperty("HADOOP_USER_NAME","hdfs")

    //创建sparkcore入库,sparkcontext
    val sc: SparkContext = new SparkContext(new SparkConf().setAppName("checkpoint").setMaster("local[*]"))

    //设置checkpoint路径
    sc.setCheckpointDir("hdfs://hadoop701:8020/checkpoint")

    val lineRDD: RDD[String] = sc.textFile("src/main/resources/person.txt")

    val flatRDD: RDD[String] = lineRDD.flatMap(_.split(" "))

    val mapRDD: RDD[(String, Long)] = flatRDD.map {
      word => {
        (word, System.currentTimeMillis())
      }
    }

    //数据检查点
    mapRDD.cache()
    mapRDD.checkpoint()

    mapRDD.collect().foreach(println)
//    println("----------------------------------------")
//    mapRDD.collect().foreach(println)
//    println("-----------------------------------------")
//    mapRDD.collect().foreach(println)


    Thread.sleep(1000000000)
    //断开连接
    sc.stop()
  }
}
