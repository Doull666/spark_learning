package com.ll.connecthive

import org.apache.spark.sql.expressions.{MutableAggregationBuffer, UserDefinedAggregateFunction}
import org.apache.spark.sql.types._
import org.apache.spark.sql.{Row, types}


/**
 * @Author lin_li
 * @Date 2022/8/7 16:47
 */
class AreaClickUDAF extends UserDefinedAggregateFunction {
  //输入数据
  override def inputSchema: StructType = {
    StructType(StructField("city_name", StringType) :: Nil)
  }

  //缓存数据
  override def bufferSchema: StructType = {
    StructType(StructField("city_count", types.MapType(StringType, LongType)) :: StructField("total_count", LongType) :: Nil)
  }

  //输出数据
  override def dataType: DataType = StringType

  //稳定性
  override def deterministic: Boolean = true

  //数据初始化
  override def initialize(buffer: MutableAggregationBuffer): Unit = {
    buffer(0) = Map[String, Long]()
    buffer(1) = 0L
  }

  //分区内合并
  override def update(buffer: MutableAggregationBuffer, input: Row): Unit = {
    val cityName: String = input.getString(0)
    val map: Map[String, Long] = buffer.getAs[Map[String, Long]](0)
    buffer(0) = map + (cityName -> (map.getOrElse(cityName, 0L) + 1L))
    buffer(1) = buffer.getLong(1) + 1L
  }

  //分区间合并
  override def merge(buffer1: MutableAggregationBuffer, buffer2: Row): Unit = {
    val map1: Map[String, Long] = buffer1.getAs[Map[String, Long]](0)
    val map2: Map[String, Long] = buffer2.getAs[Map[String, Long]](0)

    //两个map相加最终将结果汇总至buffer1
    buffer1(0) = map1.foldLeft(map2) {
      case (map, (k, v)) =>
        map + (k -> (map.getOrElse(k, 0L) + v))
    }
    buffer1(1) = buffer1.getLong(1) + buffer2.getLong(1)
  }

  //最终输出
  override def evaluate(buffer: Row): Any = {
    val cityCountMap: Map[String, Long] = buffer.getAs[Map[String, Long]](0)
    val totalCount: Long = buffer.getLong(1)

    var citysRatio: List[CityRemark] = cityCountMap.toList.sortBy(_._2).take(2).map {
      case (cityName, count) => {
        CityRemark(cityName, count / totalCount.toDouble)
      }
    }

    //如果城市个数超过2才显示其他
    if (cityCountMap.size > 2) {
      citysRatio = citysRatio :+ CityRemark("其他", citysRatio.foldLeft(1D)(_ - _.cityRatio))
    }
    citysRatio.mkString(",")
  }

}
