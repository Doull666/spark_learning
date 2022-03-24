package com.ll.transaction

import org.apache.spark.sql.SparkSession

object spark_graph_process {
  def main(args: Array[String]): Unit = {
    //创建sparkSession
    val spark: SparkSession = SparkSession.builder()
      .appName("transaction")
      .master("local[*]")
      .enableHiveSupport()
      .getOrCreate()

  }

  
}
