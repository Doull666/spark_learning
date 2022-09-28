package com.ll.connecthive

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.storage.StorageLevel

/**
 * @Author lin_li
 * @Date 2022/8/7 13:30
 */
object Top3 {
  def main(args: Array[String]): Unit = {
    //设置 spark.local.dir
    val conf = new SparkConf()
    conf.set("spark.local.dir", "mycluster/local")

    //连接 Sparksql 的入口
    val spark: SparkSession = SparkSession.builder()
      .appName("top3")
      .master("local[*]")
      .config(conf)
      .enableHiveSupport()
      .getOrCreate()

    val sc: SparkContext = spark.sparkContext

    //设置 checkpoint 检查点
    spark.sparkContext.setCheckpointDir("/user/ll/checkpoint")

    //注册自定义的udaf函数
    spark.udf.register("city_remark", new AreaClickUDAF)

    val df: DataFrame = spark.table("user_visit_action").selectExpr("city_id", "click_product_id")
      .filter("click_product_id != -1")
    df.persist(StorageLevel.MEMORY_AND_DISK_SER).createOrReplaceTempView("click_area")

    val nameDF: DataFrame = spark.sql(
      """
        |SELECT
        |c.product_name,
        |b.city_name ,
        |b.area
        |FROM  click_area a
        |left join
        |city_info b
        |on a.city_id =b.city_id
        |left join
        |product_info c
        |on a.click_product_id=c.product_id
        |""".stripMargin)

    nameDF.persist(StorageLevel.MEMORY_AND_DISK_SER).createOrReplaceTempView("click_city_area")

    spark.sql(
      """
        |select
        |area,
        |product_name,
        |count(1) click_count,
        |city_remark(city_name) as city_info
        |from click_city_area
        |group by area,product_name
        |""".stripMargin).persist(StorageLevel.MEMORY_AND_DISK_SER).createOrReplaceTempView("t2")

    spark.sql(
      """
        |with a as (select area,product_name,click_count,city_info,row_number() over(partition by area order by click_count desc) rk from t2)
        |select
        |area,
        |product_name,
        |click_count,
        |city_info
        |from
        |a
        |where rk<=3
        |""".stripMargin).persist(StorageLevel.MEMORY_AND_DISK_SER).show(100, false)


    sc.textFile("/tmp/lin_li/input").flatMap(_.split(" ")).map((_,1)).reduceByKey(_+_).collect()

    //断开连接
    spark.stop()
  }
}
