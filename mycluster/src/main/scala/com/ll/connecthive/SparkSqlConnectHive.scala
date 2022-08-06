package com.ll.connecthive

import org.apache.spark.sql.SparkSession

/**
 * @Author lin_li
 * @Date 2022/7/20 21:15
 */
object SparkSqlConnectHive {
  def main(args: Array[String]): Unit = {
    System.setProperty("HADOOP_USER_NAME", "root")
    System.setProperty("mapreduce.app-submission.cross-platform","true")
    //创建sparksql的入口
    val spark: SparkSession = SparkSession.builder()
      .appName("poc")
      //设置spark任务提交模式为yarn-client模式
      .master("yarn")
      .config("hive.metastore.uris","thrift://hadoop701:9083")
//      设置resourcemanager的ip
//      .config("yarn.resourcemanager.hostname","hadoop703")
      // 设置executor的个数
      .config("spark.executor.instance","4")
//       设置executor的内存大小
      .config("spark.executor.memory", "2G")
//      .config("spark.driver.port",4040)
//      .config("spark.port.maxRetries",999)
      // 设置提交任务的yarn队列
//      .config("spark.yarn.queue","spark")
      // 设置driver的ip地址
//      .config("spark.driver.host","hadoop701")
      .config("yarn.nodemanager.vmem-check-enabled","false")
      .config("yarn.nodemanager.pmem-check-enabled","false")
//      .config("mapreduce.app-submission.cross-platform","true")
//      .config("spark.dynamicAllocation.enabled","false")
//      .config("yarn.nodemanager.vmem-pmem-ratio",2)
//      .config("spark.yarn.archive", "hdfs:///user/ll/spark-jars.zip")
      .enableHiveSupport()
      .getOrCreate()

    spark.sql("select * from default.demo").show()

spark.sql(
  """
    |
    |""".stripMargin)





    //断开连接
    spark.stop()
  }
}
