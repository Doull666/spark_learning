package com.ll.sparksql.sparkfunction.udaf

import org.apache.spark.sql.Row
import org.apache.spark.sql.expressions.{MutableAggregationBuffer, UserDefinedAggregateFunction}
import org.apache.spark.sql.types.{DataType, LongType, MapType, StringType, StructField, StructType}

/**
 * @Author lin_li
 * @Date 2022/3/11 17:32
 */
case class CityRatioUDAF extends UserDefinedAggregateFunction {
  //输入参数类型
  override def inputSchema: StructType = StructType(StructField("city", StringType) :: Nil)

  //缓冲数据类型
  override def bufferSchema: StructType = StructType(StructField("city_count", MapType(StringType, LongType)) :: StructField("total_count", LongType) :: Nil)

  //输出数据类型
  override def dataType: DataType = StringType

  //稳定性
  override def deterministic: Boolean = true

  //初始化缓冲区
  override def initialize(buffer: MutableAggregationBuffer): Unit = {
    buffer(0) = Map[String, Long]
    buffer(1) = 0L
  }

  //分区内累加数据
  override def update(buffer: MutableAggregationBuffer, input: Row): Unit = {
    //获取缓冲数据的Map
    val cityToCount: collection.Map[String, Long] = buffer.getMap[String, Long](0)

    //判断当前传入的城市是否为Null
    if (!input.isNullAt(0)) {
      //获取城市名称
      val city: String = input.getString(0)
      buffer(0) = cityToCount + (city -> (cityToCount.getOrElse(city, 0L) + 1L))
    }
    //总数自增
    buffer(1) = buffer.getLong(1) + 1L
  }

  //分区间累加数据
  override def merge(buffer1: MutableAggregationBuffer, buffer2: Row): Unit = {
    val map1: collection.Map[String, Long] = buffer1.getMap[String, Long](0)
    val map2: collection.Map[String, Long] = buffer2.getMap[String, Long](0)

    buffer1(0) = map1.foldLeft(map2) { case (map, (city, count)) =>
      map + (city -> (map.getOrElse(city, 0L) + count))
    }

    //总数累加
    buffer1(1) = buffer1.getLong(1) + buffer2.getLong(1)
  }

  //最终计算
  override def evaluate(buffer: Row): String = {
    //取出缓冲数据
    val cityToCount: collection.Map[String, Long] = buffer.getMap[String, Long](0)
    val totalCount: Long = buffer.getLong(1)

    //计算Top2
    val top2CityCount: List[(String, Long)] = cityToCount.toList.sortWith(_._2 > _._2).take(2)

    //定义其他占比
    var otherRaio = 1D

    //计算Top2城市占比
    val ratios: List[CityRatio] = top2CityCount.map {
      //
      case (city, count) => {
        //计算当前城市占比
        val ratio: Double = count.toDouble / totalCount

        //计算其他城市占比
        otherRaio -= ratio

        CityRatio(city, ratio)
      }
    }
    //添加其他城市及占比
    val cityRatioList: List[CityRatio] = ratios :+ CityRatio("其他", otherRaio)

    //将集合转为string
    cityRatioList.mkString(",")
  }
}
