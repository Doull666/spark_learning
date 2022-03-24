package com.ll.sparksql.top3

import org.apache.spark.sql.SparkSession

object Top3 {
  def main(args: Array[String]): Unit = {
    //验证kerbros
    initKerberos()

    //创建sparkSql入口，即sparkSession
    val spark: SparkSession = SparkSession.builder()
      .appName("top3")
      .master("local[*]")
      .enableHiveSupport()
      .getOrCreate()

      //1.首先计算各区域各商品点击汇总
    spark.sql(
      """
        |WITH a AS (SELECT click_product_id,city_id FROM user_visit_action),
        |    b AS (SELECT product_id,product_name from product_info),
        |    c AS (SELECT city_id,area FROM city_info)
        |SELECT
        |a.click_product_id,
        |c.area,
        |b.product_name
        |FROM a
        |LEFT JOIN b
        |ON a.click_product_id=b.product_id
        |LEFT JOIN C
        |ON a.city_id=c.city_id
        |""".stripMargin).createOrReplaceTempView("mid1")

    spark.sql("select * mid1 limit 10").show(false)

    //断开连接
    spark.stop()
  }
}
