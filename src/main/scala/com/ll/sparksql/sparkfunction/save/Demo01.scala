package com.ll.sparksql.sparkfunction.save

import org.apache.spark.sql.{DataFrame, SaveMode, SparkSession}

/**
 * @Author lin_li
 * @Date 2022/3/3 16:08
 */
object Demo01 {
  def main(args: Array[String]): Unit = {
    val spark: SparkSession = SparkSession.builder().appName("save01").master("local[*]").getOrCreate()

    val df: DataFrame = spark.read.json("src/main/resources/person.json")

    df.show()

    df.write.mode(SaveMode.Append)json("src/main/resources/ddd.json")


    spark.stop()
  }
}
