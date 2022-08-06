package com.ll.sparksql.connectmysql

import java.util.Properties

import org.apache.spark.sql.{DataFrame, SaveMode, SparkSession}

/**
 * @Author lin_li
 * @Date 2022/3/4 10:55
 */
object SparkSqlMysql {
  def main(args: Array[String]): Unit = {
    val spark: SparkSession = SparkSession.builder()
      .appName("mysql")
      .master("local[*]")
      .getOrCreate()

    //2.Mysql 参数
    val properties = new Properties()
    properties.put("user","root")
    properties.put("password","bertadata")

    //加载mysql数据创建df
    val df: DataFrame = spark.read.jdbc("jdbc:mysql://192.168.28.105:3306/test", "test", properties)

    //打印数据
    df.show()

    //将数据写入mysql
    df.write.mode(SaveMode.Append).jdbc("jdbc:mysql://192.168.28.105:3306/test", "test02", properties)

    //关闭数据
    spark.stop()
  }
}
