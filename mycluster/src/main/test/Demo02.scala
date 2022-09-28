import org.apache.spark.SparkContext
import org.apache.spark.sql.SparkSession

/**
 * @Author lin_li
 * @Date 2022/9/21 16:01
 */
object Demo02 {
  def main(args: Array[String]): Unit = {
    val spark: SparkSession = SparkSession.builder()
      .appName("tiaoyou")
      .master("local[*]")
      .getOrCreate()

    val sc: SparkContext = spark.sparkContext

    spark.sql(
      """
        |with a as (select dt,user_id,age,row_number() over(partition by user_id order by dt) rk from user_age group by dt,user_id,age),
        |     b as (select user_id,age,date_sub(dt,rk) grp from a),
        |     c as (select user_id,age,count(1) cnt from b group by user_id,age,grp),
        |     d as (select user_id,age from c where cnt>=2 group by user_id,age)
        |     select
        |     count(1) as all_cnt,
        |     avg(age) as avg_age
        |     from d
        |""".stripMargin).show(false)


    spark.stop()
  }
}
