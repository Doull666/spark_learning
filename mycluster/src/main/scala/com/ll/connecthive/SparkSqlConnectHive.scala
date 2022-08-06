package com.ll.connecthive

import org.apache.spark.sql.SparkSession

/**
 * @Author lin_li
 * @Date 2022/7/20 21:15
 */
object SparkSqlConnectHive {
  def main(args: Array[String]): Unit = {
    //创建sparksql的入口
    val spark: SparkSession = SparkSession.builder()
      .appName("poc")
      //设置spark任务提交模式为yarn-client模式
      .master("local[*]")
      .enableHiveSupport()
      .getOrCreate()

    spark.sql("select * from default.demo").show()

    //断开连接
    spark.stop()
  }
}
