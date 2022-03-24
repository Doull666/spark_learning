package com.ll.sparksql.sparkfunction.udaf.sumudaf

import org.apache.spark.sql.Row
import org.apache.spark.sql.expressions.{MutableAggregationBuffer, UserDefinedAggregateFunction}
import org.apache.spark.sql.types.{DataType, IntegerType, StructField, StructType}

case class MySum() extends UserDefinedAggregateFunction {
  //输入数据类型
  override def inputSchema: StructType = StructType(StructField("input", IntegerType) :: Nil)

  //缓存数据类型
  override def bufferSchema: StructType = StructType(StructField("sum", IntegerType) :: Nil)

  //输出数据类型
  override def dataType: DataType = IntegerType

  //稳定输出
  override def deterministic: Boolean = true

  //初始化缓冲区
  override def initialize(buffer: MutableAggregationBuffer): Unit = {
    buffer(0) = 0
  }

  //同一分区数据合并
  override def update(buffer: MutableAggregationBuffer, input: Row): Unit = {
    if(input.isNullAt(0)){
      println("有一个人age数据为空")
    }else{
      buffer(0) = buffer.getInt(0) + input.getInt(0)
    }

  }

  //分区间数据合并
  override def merge(buffer1: MutableAggregationBuffer, buffer2: Row): Unit = {
    buffer1(0) = buffer1.getInt(0) + buffer2.getInt(0)
  }

  //最终计算
  override def evaluate(buffer: Row): Int = {
    buffer.getInt(0)
  }
}
