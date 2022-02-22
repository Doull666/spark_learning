package com.ll.sparkcore.accumulator

import org.apache.spark.util.AccumulatorV2

import scala.collection.mutable

class MyAccumulator extends AccumulatorV2[String,mutable.Map[String,Int]]{
  //定义一个输出 集合
  var map=mutable.Map[String,Int]()

  //判断集合初始化状态
  override def isZero: Boolean = map.isEmpty

  //复制累加器
  override def copy(): AccumulatorV2[String, mutable.Map[String, Int]] = {
    new MyAccumulator
  }

  //重置累加器
  override def reset(): Unit = map.clear()

  override def add(v: String): Unit = {
    if(v.startsWith("H")){
      map(v)=map.getOrElse(v,0)+1
    }
  }

  //AccumulatorV2[累加器名，[统计的集合key,value]]
  override def merge(other: AccumulatorV2[String, mutable.Map[String, Int]]): Unit = {
    other.value.foreach{
      case (word,count)=>{
        map(word)=map.getOrElse(word,0)+count
      }
    }
  }

  //累加其结果
  override def value: mutable.Map[String, Int] = map
}
