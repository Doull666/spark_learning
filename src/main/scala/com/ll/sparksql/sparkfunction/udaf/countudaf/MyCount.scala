package com.ll.sparksql.sparkfunction.udaf.countudaf

import org.apache.spark.sql.Row
import org.apache.spark.sql.expressions.{MutableAggregationBuffer, UserDefinedAggregateFunction}
import org.apache.spark.sql.types.{DataType, IntegerType, StringType, StructField, StructType}

case class MyCount() extends UserDefinedAggregateFunction {
  //输入数据类型
  override def inputSchema: StructType = StructType(StructField("line", StringType) :: Nil)

  //缓存数据类型
  override def bufferSchema: StructType = StructType(StructField("count", IntegerType) :: Nil)

  //输出数据类型
  override def dataType: DataType = IntegerType

  //是否输出稳定
  override def deterministic: Boolean = true

  //初始化缓冲数据
  override def initialize(buffer: MutableAggregationBuffer): Unit = {
    buffer(0) = 0
  }

  //分区内数据合并
  override def update(buffer: MutableAggregationBuffer, input: Row): Unit = {
    buffer(0) = buffer.getInt(0) + 1
  }

  //分区间相加
  override def merge(buffer1: MutableAggregationBuffer, buffer2: Row): Unit = {
    buffer1(0)=buffer1.getInt(0)+buffer2.getInt(0)
  }

  //最终结果
  override def evaluate(buffer: Row): Int = {
    buffer.getInt(0)
  }
}
