package com.ll.sparksql.sparkfunction.udaf.avgudaf

import org.apache.spark.sql.Row
import org.apache.spark.sql.expressions.{MutableAggregationBuffer, UserDefinedAggregateFunction}
import org.apache.spark.sql.types._

/**
 * @Author lin_li
 * @Date 2022/3/2 10:42
 */
class MyAvg extends UserDefinedAggregateFunction {
  //1.输入数据类型
  override def inputSchema: StructType = StructType(StructField("input", LongType) :: Nil)

  //2.中间缓存数据类型
  override def bufferSchema: StructType = StructType(StructField("total", LongType) :: StructField("count", IntegerType) :: Nil)

  //3.输出数据类型
  override def dataType: DataType = DoubleType

  //4.函数稳定性参数：对于相同的数据输入，总是有相同的输出
  override def deterministic: Boolean = true

  //5.初始化缓冲数据
  override def initialize(buffer: MutableAggregationBuffer): Unit = {
    buffer(0) = 0L
    buffer(1) = 0
  }

  //6.分区内累加数据
  override def update(buffer: MutableAggregationBuffer, input: Row): Unit = {
    if (!input.isNullAt(0)) {
      buffer(0) = buffer.getLong(0) + input.getLong(0)
      buffer(1) = buffer.getInt(1) + 1
    }
  }

  //7.分区间累加数据
  override def merge(buffer1: MutableAggregationBuffer, buffer2: Row): Unit = {
    buffer1(0) = buffer1.getLong(0) + buffer2.getLong(0)
    buffer1(1) = buffer1.getInt(1) + buffer2.getInt(1)
  }

  //8.最终计算
  override def evaluate(buffer: Row): Double = {
    buffer.getLong(0).toDouble / buffer.getInt(1)
  }
}
