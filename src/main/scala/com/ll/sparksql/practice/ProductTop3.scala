package com.ll.sparksql.practice

import com.ll.sparksql.sparkfunction.udaf.CityRatioUDAF
import org.apache.spark.sql.SparkSession

/**
 * @Author lin_li
 * @Date 2022/3/11 16:31
 */
object ProductTop3 {
  def main(args: Array[String]): Unit = {
    //创建sparksession对象
    val spark: SparkSession = SparkSession.builder()
      .appName("Top3")
      .master("local[*]")
      .enableHiveSupport()
      .getOrCreate()

    //注冊udaf函數
    spark.udf.register("cityRatio",new CityRatioUDAF)

    //读取hive中的数据创建df
    spark.sql("with ua as(select click_product_id,city_id from test.user_visit_action where click_product_id>-1)\nselect\nci.area as area,\npi.product_name as product_name,\nci.city_name as city_name\nfrom ua\njoin test.product_info pi\non ua.click_product_id=pi.product_id\njoin test.city_info ci\non ua.city_id=ci.city_id")
        .createOrReplaceTempView("t1")

    //计算各大区对于各个商品的点击总数以及各城市的占比
    spark.sql("select\narea,\nproduct_name,\ncount(*) click_count,cityRatio(city_name) city_ratio\nfrom t1\ngroup by area,product_name").show(10,false)

    //关闭连接
    spark.stop()
  }
}
