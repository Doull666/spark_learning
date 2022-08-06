package com.ll.sparkcore.savefile

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @Author lin_li
 * @Date 2022/4/23 20:37
 */
object SaveText {
  def main(args: Array[String]): Unit = {
    //创建sparkcore的入口SparkContext
    val sc: SparkContext = new SparkContext(new SparkConf().setAppName("save").setMaster("local[*]"))

    //从本地读取文件
    val fileRDD: RDD[String] = sc.textFile("hdfs://hadoop701:8020/user/ll/input")

    //保存文件
    fileRDD.saveAsTextFile("hdfs://hadoop701:8020/user/ll/output")


    //断开连接
    sc.stop()
  }
}
