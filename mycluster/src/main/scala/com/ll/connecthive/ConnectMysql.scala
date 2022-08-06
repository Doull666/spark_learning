package com.ll.connecthive

import org.apache.spark.sql.{Dataset, SaveMode, SparkSession}

import java.util.Properties

/**
 * @Author lin_li
 * @Date 2022/8/6 19:06
 */
object ConnectMysql {
  def main(args: Array[String]): Unit = {
    //创建SparkSession的程序入口
    val spark: SparkSession = SparkSession.builder()
      .appName("connectmysql")
      .master("local[*]")
      .getOrCreate()

    //mysql的配置信息
    val properties = new Properties()
    properties.put("user","root")
    properties.put("password","666666")

    import spark.implicits._
    val ds: Dataset[People] = Seq(People("darling", 22)).toDS()

    //通过jdbc连接mysql读取数据
//    spark.read.jdbc("jdbc:mysql://hadoop701:3306/test","people",properties).show(false)

    //通过jdbc写数据到mysql中
    ds.write.mode(SaveMode.Append).jdbc("jdbc:mysql://hadoop701:3306/test","person",properties)

    //断开连接
    spark.stop()
  }
}
