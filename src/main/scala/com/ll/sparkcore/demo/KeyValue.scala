package com.ll.sparkcore.demo

import org.apache.spark.rdd.RDD
import org.apache.spark.{HashPartitioner, SPARK_BRANCH, SparkConf, SparkContext}

object KeyValue {
  def main(args: Array[String]): Unit = {
    //创建SparkConf
    val conf: SparkConf = new SparkConf().setAppName("key_value").setMaster("local[*]")

    //创建SparkContext
    val sc: SparkContext = new SparkContext(conf)

    //    val rdd: RDD[(Int, String)] = sc.makeRDD(Array((1,"aaa"),(2,"bbb"),(3,"ccc")),3)
    val rdd1: RDD[(Int, String)] = sc.makeRDD(Array((1, "a"),(1,"fads"), (2, "b"), (3, "c")))
    val rdd2: RDD[(Int, Int)] = sc.makeRDD(Array((1, 4), (2, 5), (4, 6)))
//    rdd.mapPartitionsWithIndex((index,datas)=>datas.map((index,_))).collect().foreach(println)
//    println("==============================================================")

    //partitionBy,对RDD重新分区
//    val partitionRDD: RDD[(Int, String)] = rdd.partitionBy(new HashPartitioner(2))
//    val indexRDD: RDD[(Int, (Int, String))] = partitionRDD.mapPartitionsWithIndex(
//      (index, datas) => datas.map((index, _))
//    )

    //reduceByKey
//    val redceByKeyRdd: RDD[(String, Int)] = rdd.reduceByKey((v1, v2) => v1 + v2)

    //groupByKey
//    val groupByKeyRDD: RDD[(String, Iterable[Int])] = rdd.groupByKey()

    //aggregateByKey
//    rdd.aggregateByKey(0)(math.max(_,_),_+_).collect().foreach(println)

    //foldByKey
//    rdd.foldByKey(0)(_+_).collect().foreach(println)

    //sortByKey
//    rdd.sortByKey(false).collect().foreach(println)

    //mapValues
//    rdd.mapValues(_*2).collect().foreach(println)

    //join
//    rdd1.join(rdd2).collect().foreach(println)

    //cogroup
    rdd1.cogroup(rdd2).collect().foreach(println)

//    groupByKeyRDD.collect().foreach(println)


    //停止连接
    sc.stop()

  }
}
