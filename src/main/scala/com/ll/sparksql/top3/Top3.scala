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
        |WITH a AS (SELECT click_product_id,city_id FROM test.user_visit_action),
        |    b AS (SELECT product_id,product_name from test.product_info),
        |    c AS (SELECT city_id,city_name,area FROM test.city_info)
        |SELECT
        |a.click_product_id,
        |c.area,
        |c.city_name,
        |b.product_name
        |FROM a
        |LEFT JOIN b
        |ON a.click_product_id=b.product_id
        |LEFT JOIN C
        |ON a.city_id=c.city_id
        |where length(b.product_name)>0
        |""".stripMargin).createOrReplaceTempView("mid1")

    //注册udaf函数
    spark.udf.register("cityratio",new AreaClickUDAF)

    //在聚合函数中使用自定义函数
    spark.sql(
      """
        |SELECT
        |area,
        |product_name,
        |count(*) cnt,
        |cityratio(city_name)
        |FROM mid1
        |GROUP  BY area, product_name
        |""".stripMargin).createOrReplaceTempView("mid2")

    spark.sql("select * from mid2 limit10").show(false)

    //断开连接
    spark.stop()
  }
}
