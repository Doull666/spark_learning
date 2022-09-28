import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @Author lin_li
 * @Date 2022/9/15 16:10
 *
 *       测试 rdd 分区规则
 */
object Demo01 {
  def main(args: Array[String]): Unit = {
    //创建sparkcore的程序入口
    val conf: SparkConf = new SparkConf()
      .setAppName("partitioner")
      .setMaster("local[*]")

    val sc: SparkContext = new SparkContext(conf)

    sc.setCheckpointDir("checkpoint")

    val dataRDD: RDD[(String, Int)] = sc.textFile("input").flatMap(_.split(" ")).map(
      word=>{
        println("*********************")
        (word,1)
      }
    )




    dataRDD.cache()

    dataRDD.checkpoint()

    dataRDD.collect()








    //断开连接
    sc.stop()

  }
}
