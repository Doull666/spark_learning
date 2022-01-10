package com.ll.sparktest

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @Author lin_li
 * @Date 2022/1/5 16:55
 */
object spark_test01 {
  def main(args: Array[String]): Unit = {
    val conf: SparkConf = new SparkConf().setAppName("test01").setMaster("local[2]")
    val sc = new SparkContext(conf)

//    val arrayRDD: RDD[Int] = sc.parallelize(Array(1, 2, 3, 4, 5))
//    arrayRDD.foreach(println)

    val mRDD: RDD[Int] = sc.makeRDD(Array(1, 2, 3, 4))
    mRDD.saveAsTextFile("output")


    sc.stop()

  }

}
