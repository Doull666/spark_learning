import org.apache.spark.{SparkConf, SparkContext}

/**
 * @Author lin_li
 * @Date 2022/8/6 13:14
 */
object Demo {
  def main(args: Array[String]): Unit = {
    val conf: SparkConf = new SparkConf()
      .setMaster("local[*]")
      .setAppName("WC")

    //Spark core的程序入口
    val sc: SparkContext = new SparkContext(conf)

    sc.textFile("/tmp/lin_li/input")
      .flatMap(_.split(" ")).map((_, 1)).reduceByKey(_ + _).saveAsTextFile("/tmp/lin_li/output")

    //断开连接
    sc.stop()

  }
}
