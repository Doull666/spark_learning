package com.ll.sparksql.top3

import org.apache.spark.sql.Row
import org.apache.spark.sql.expressions.{MutableAggregationBuffer, UserDefinedAggregateFunction}
import org.apache.spark.sql.types.{DataType, LongType, MapType, StringType, StructField, StructType}

/**
 * @Author lin_li
 * @Date 2022/3/25 11:34
 */
class AreaClickUDAF extends UserDefinedAggregateFunction {
  //输入数据类型
  override def inputSchema: StructType = StructType(StructField("city_name", StringType) :: Nil)

  //缓冲数据类型
  override def bufferSchema: StructType = StructType(StructField("city_count", MapType(StringType, LongType)) :: StructField("total_count", LongType) :: Nil)

  //输出数据类型
  override def dataType: DataType = StringType

  //稳定性
  override def deterministic: Boolean = true

  //缓冲区初始化
  override def initialize(buffer: MutableAggregationBuffer): Unit = {
    buffer(0) = Map[String, Long]()
    buffer(1) = 0L
  }

  //分区内合并,input即为传入的参数，buffer 为存放缓存集合
  override def update(buffer: MutableAggregationBuffer, input: Row): Unit = {
    val city_name = input.getString(0)
    val map = buffer.getMap[String, Long](0)
    //存放地区点击数
    buffer(0) = map + (city_name -> (map.getOrElse(city_name, 0L) + 1L))
    //存放总点击商品数
    buffer(1) = buffer.getLong(1) + 1L
  }

  //分区间合并，buffer2是分区间的缓存数据，buffer1是准备存放合并后的缓存数据集合
  override def merge(buffer1: MutableAggregationBuffer, buffer2: Row): Unit = {
    val map1 = buffer1.getMap[String, Long](0)
    val map2 = buffer2.getMap[String, Long](0)
    buffer1(0) = map1.foldLeft(map2) {
      case (map, (k, v)) => map + (k -> (map.getOrElse(k, 0L) + v))
    }
    buffer1(1) = buffer1.getLong(1) + buffer2.getLong(1)
  }

  //最终结果输出
  override def evaluate(buffer: Row): String = {
    val cityCountMap = buffer.getMap[String, Long](0)
    val total_count = buffer.getLong(1)
    var citysRatio: List[CityRemark] = cityCountMap.toList.sortBy(_._2).take(2).map {
      case (cityName, count) => {
        CityRemark(cityName, count.toDouble / total_count)
      }
    }
    //如果城市的个数超过2才显示其他
    if (cityCountMap.size > 2) {
      citysRatio = citysRatio :+ CityRemark("其他", citysRatio.foldLeft(1D)(_ - _.cityRatio))
    }
    citysRatio.mkString(",")
  }
}
