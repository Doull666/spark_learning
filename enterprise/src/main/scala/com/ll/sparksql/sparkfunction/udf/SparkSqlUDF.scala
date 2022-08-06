package com.ll.sparksql.sparkfunction.udf

import org.apache.spark.sql.{DataFrame, SparkSession}

/**
 * @Author lin_li
 * @Date 2022/3/2 10:29
 */
object SparkSqlUDF {
  def main(args: Array[String]): Unit = {
    val session: SparkSession = SparkSession.builder().appName("SparkSqlUDF").master("local[*]").getOrCreate()

    val df: DataFrame = session.read.json("src/main/resources/person.json")

    //4.自定义udf
    session.udf.register("addName",(x:String)=>s"Name:$x")

    //5.创建临时表
    df.createOrReplaceTempView("people")

    //6.使用udf函数
    session.sql("select addName(name) from people").show()

    //7. 关闭连接
    session.stop()
  }
}
