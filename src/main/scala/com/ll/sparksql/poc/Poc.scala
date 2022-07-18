package com.ll.sparksql.poc

import org.apache.spark.sql.SparkSession

import scala.io.{BufferedSource, Source}

/**
 * @Author lin_li
 * @Date 2022/7/18 20:50
 */
object Poc {
  def main(args: Array[String]): Unit = {
    //kerberos认证
    initKerberos()

    //Sparksql入口，SparkSession
    val spark: SparkSession = SparkSession.builder()
      .appName("poc")
      .master("local[*]")
      .enableHiveSupport()
      .getOrCreate()

    //按行读取文件
    val source: BufferedSource = Source.fromFile("src/main/resources/table.txt")
    val lines: Iterator[String] = source.getLines()
    for (tableName <- lines.toList) {
      spark.sql(
        """
          |select
          |eid
          |
          |from
          |test.demo01 a left join
          |db_qkgp.'{tableName}' as b on a.eid=b.eid;
          |""".stripMargin)
    }


    spark.sql(
      """
        |insert overwrite table
        |""".stripMargin)


  }
}
