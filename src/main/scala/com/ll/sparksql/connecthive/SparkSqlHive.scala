package com.ll.sparksql.connecthive

import org.apache.spark.sql.SparkSession

/**
 * @Author lin_li
 * @Date 2022/3/4 14:24
 */
object SparkSqlHive {
  def main(args: Array[String]): Unit = {
    val spark: SparkSession = SparkSession.builder()
      .appName("hive")
      .master("local[*]")
      .enableHiveSupport()
      .getOrCreate()

    spark.sql("show databases").show()
    spark.sql("create table demo(id int)")
    spark.sql("insert into table demo values(1001)")
    spark.sql("select * from demo").show()

    //關閉鏈接
    spark.stop()
  }
}
