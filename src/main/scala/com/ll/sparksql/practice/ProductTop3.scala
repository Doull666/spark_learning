package com.ll.sparksql.practice

import com.ll.sparksql.sparkfunction.udaf.CityRatioUDAF
import org.apache.spark.sql.SparkSession
import scala.collection.mutable

/**
 * @Author lin_li
 * @Date 2022/3/11 16:31
 */
object ProductTop3 {
  def main(args: Array[String]): Unit = {
    //kerberos验证
    initKerberos()

    //创建sparksession对象
    val spark: SparkSession = SparkSession.builder()
      .appName("top3")
      .master("local[*]")
//      .config("kerberos","src/main/resources/supergroup.keytab")
      .enableHiveSupport()
      .getOrCreate()

    //注冊udaf函數
    spark.udf.register("city_remark",new CityRatioUDAF)

    spark.sql("use test")

    //读取hive中的数据创建df
    spark.sql("""
                |select
                |    c.*,
                |    v.click_product_id,
                |    p.product_name
                |from test.user_visit_action v join test.city_info c join test.product_info p on v.city_id=c.city_id and v.click_product_id=p.product_id
                |where click_product_id>-1
            """.stripMargin).createOrReplaceTempView("t1")


    spark.sql(
      """
        |select
        |    t1.area,
        |    t1.product_name,
        |    count(*) click_count,
        |    city_remark(t1.city_name)
        |from t1
        |group by t1.area, t1.product_name
            """.stripMargin).createOrReplaceTempView("t2")

    // 3. 对每个区域内产品的点击量进行倒序排列
    spark.sql(
      """
        |select
        |    *,
        |    rank() over(partition by t2.area order by t2.click_count desc) rank
        |from t2
            """.stripMargin).createOrReplaceTempView("t3")

    // 4. 每个区域取top3

    spark.sql(
      """
        |select
        |    *
        |from t3
        |where rank<=3
            """.stripMargin).show(10,false)


    //关闭连接
    spark.stop()
  }
}
