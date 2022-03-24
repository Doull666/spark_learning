package com.ll.sparksql.sparkfunction.udaf.countudaf

import org.apache.spark.sql.{DataFrame, SparkSession}

object CountUdaf {
  def main(args: Array[String]): Unit = {
    //创建SparkSession
    val spark: SparkSession = SparkSession.builder()
      .appName("count")
      .master("local[*]")
      .getOrCreate()

    //读取json文件创建df
    val df: DataFrame = spark.read.json("src/main/resources/person.json")

    //注册udaf
    spark.udf.register("mycount",new MyCount)

    //创建一张临时表
    df.createOrReplaceTempView("people")

    //计算数据条数
    spark.sql("select mycount(name) from people").show()

    //断开连接
    spark.stop()
  }
}
