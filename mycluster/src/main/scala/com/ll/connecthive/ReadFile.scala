package com.ll.connecthive

import com.alibaba.fastjson.JSON
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, Dataset, SaveMode, SparkSession}
import org.apache.spark.{SparkConf, SparkContext}


/**
 * @Author lin_li
 * @Date 2022/8/6 13:14
 */
object ReadFile {
  def main(args: Array[String]): Unit = {
    //创建sparksql的入口sparksession，连接到集群读取hdfs上文件
    val spark: SparkSession = SparkSession.builder()
      .appName("readfile")
      .master("local[*]")
      .enableHiveSupport()
      .getOrCreate()

    val df: DataFrame = spark.read.json("/user/ll/people.json")

    import spark.implicits._
    val ds: Dataset[People] = Seq(People("{\"name\":\"alice\"}", 33)).toDS()

    //spark 自定义 函数
    spark.udf.register("isJson",
      (column: String) => {
        try {
          JSON.parse(column)
          column
        } catch {
          case e:Exception => null
        }
      }
    )


    ds.select("name").write.mode(SaveMode.Overwrite).json("/user/ll/demo")

    //断开连接
    spark.stop()

  }
}
