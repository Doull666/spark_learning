package com.ll.sparksql.sparkfunction.udaf.sumudaf

import org.apache.spark.sql.{DataFrame, SparkSession}

object SumUdaf {
  def main(args: Array[String]): Unit = {
    //创建SparkSession
    val spark: SparkSession = SparkSession.builder()
      .master("local[*]")
      .appName("sum")
      .getOrCreate()

    //读取json文件中的数据
    val df: DataFrame = spark.read.json("src/main/resources/person.json")

    //注册udaf函数
    spark.udf.register("mysum",new MySum)

    //创建临时表
    df.createOrReplaceTempView("people")

    //使用udaf函数
    spark.sql("select mysum(age) from people").show()

    //关闭连接
    spark.stop()
  }
}
