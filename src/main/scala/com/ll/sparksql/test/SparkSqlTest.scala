package com.ll.sparksql.test

import org.apache.spark.sql.{DataFrame, SparkSession}

/**
 * @Author lin_li
 * @Date 2022/2/22 14:42
 */
object SparkSqlTest {
  def main(args: Array[String]): Unit = {
    //1.创建一个SparkSession对象
    val spark: SparkSession = SparkSession.builder().appName("SparkSql").master("local[*]").getOrCreate()

    //2.创建一个DataFrame
    val df: DataFrame = spark.read.json("src/main/resources/person.json")

    //3.打印数据
    df.show()

    //4.创建视图
    df.createOrReplaceTempView("people")

    //5.使用sql风格
    spark.sql("select name from people").show()

    println("----------------------------")
    //6.使用DSL
    df.select("name").show()


    //4.关闭连接
    spark.stop()
  }
}
