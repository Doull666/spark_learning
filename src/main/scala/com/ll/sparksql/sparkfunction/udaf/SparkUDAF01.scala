package com.ll.sparksql.sparkfunction.udaf

import org.apache.spark.sql.{DataFrame, SparkSession}

/**
 * @Author lin_li
 * @Date 2022/3/2 11:35
 */
object SparkUDAF01 {
  def main(args: Array[String]): Unit = {
    val session: SparkSession = SparkSession.builder().appName("SparkSqlUDF").master("local[*]").getOrCreate()

    val df: DataFrame = session.read.json("src/main/resources/person.json")

    //注册udaf 函数
    session.udf.register("MyAvg",new MyAvg)

    //创建临时表
    df.createOrReplaceTempView("people")

    //sql 编程风格使用自定义 udaf 函数
    session.sql("select MyAvg(age) from people").show()

    //关闭连接
    session.stop()
  }
}
