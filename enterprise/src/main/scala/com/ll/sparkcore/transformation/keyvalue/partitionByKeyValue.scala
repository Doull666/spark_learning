package com.ll.sparkcore.transformation.keyvalue

import org.apache.spark.rdd.RDD
import org.apache.spark.{Partitioner, SparkConf, SparkContext}

/**
 * @Author lin_li
 * @Date 2022/1/10 10:55
 */
object partitionByKeyValue {
  def main(args: Array[String]): Unit = {
    val conf: SparkConf = new SparkConf().setAppName("keyvalue").setMaster("local[*]")
    val sc = new SparkContext(conf)

    val rdd: RDD[(Int, String)] = sc.makeRDD(Array((1,"aaa"),(2,"bbb"),(3,"ccc")),3)
//    rdd.mapPartitionsWithIndex{
//      (index,datas)=>datas.map((index,_))
//    }.collect().foreach(println)

    val parRDD: RDD[(Int, String)] = rdd.partitionBy(new org.apache.spark.HashPartitioner(2))
//    parRDD.mapPartitionsWithIndex{
//            (index,datas)=>datas.map((index,_))
//          }.collect().foreach(println)

    val myRDD: RDD[(Int, String)] = parRDD.partitionBy(new MyPartitioner(2))
    myRDD.mapPartitionsWithIndex{
                  (index,datas)=>datas.map((index,_))
                }.collect().foreach(println)


      sc.stop()
  }
}
class  MyPartitioner(parititions:Int) extends Partitioner{
  override def numPartitions: Int = parititions

  override def getPartition(key: Any): Int = {
    if(key.isInstanceOf[Int]){
      val keyInt: Int = key.asInstanceOf[Int]
      if (keyInt % 2 == 0)
        0
      else
        1
    }else{
      0
    }
  }
}