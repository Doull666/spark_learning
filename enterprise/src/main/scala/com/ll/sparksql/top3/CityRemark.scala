package com.ll.sparksql.top3

import java.text.DecimalFormat

/**
 * @Author lin_li
 * @Date 2022/3/25 12:10
 */
case class CityRemark(cityName:String,cityRatio:Double){
  private val formatter = new DecimalFormat("0.00%")

  override def toString: String = {
    s"${cityName}:${formatter.format(cityRatio)}"
  }
}
