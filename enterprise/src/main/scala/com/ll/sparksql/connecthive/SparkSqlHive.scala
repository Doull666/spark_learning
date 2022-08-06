package com.ll.sparksql.connecthive

import org.apache.spark.sql.SparkSession

/**
 * @Author lin_li
 * @Date 2022/3/4 14:24
 */
object SparkSqlHive {
  def main(args: Array[String]): Unit = {
    //验证kerberos认证
//    initKerberos()

    val spark: SparkSession = SparkSession.builder()
      .appName("hive")
      .master("local[*]")
      .enableHiveSupport()
      .getOrCreate()

    spark.sql("select * from db_qkgp.std_enterprise limit 10").show(10, false)

    //關閉鏈接
    spark.stop()
  }
}
