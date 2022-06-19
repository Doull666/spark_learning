package com.ll.sparksql.test

import org.apache.spark.sql.{DataFrame, SaveMode, SparkSession}

import java.util.Properties

/**
 * @Author lin_li
 * @Date 2022/4/26 15:15
 */
object Demo02 {
  def main(args: Array[String]): Unit = {
    //创建Sparksql入口，SparkSession
    val spark: SparkSession = SparkSession.builder()
      .appName("demo02")
      .master("local[*]")
      .enableHiveSupport()
      .getOrCreate()

    //导入隐士转换
    import spark.implicits._

    //通过jdbc读取mysql中的数据
//    val conn = new Properties()
//    conn.put("user","root")
//    conn.put("password","666666")
//
    val df: DataFrame = spark.read.json("src/main/resources/person.json")
//
////    val df: DataFrame = spark.read.jdbc("jdbc:mysql://hadoop701:3306/test", "people", conn)
//
//   df.write.mode(SaveMode.Append).jdbc("jdbc:mysql://hadoop701:3306/test","people", conn)


//    df.show()

    df.createOrReplaceTempView("people")



    spark.sql("insert into table demo select name from people").show()


    //断开连接
    spark.stop()
  }
}
