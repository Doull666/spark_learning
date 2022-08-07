package com.ll.connecthive

import java.text.DecimalFormat

/**
 * @Author lin_li
 * @Date 2022/8/7 18:10
 */
case class CityRemark(cityName: String, cityRatio: Double){
  private val format = new DecimalFormat("0.00%")

  override def toString: String = {
    s"$cityName:${format.format(cityRatio)}"
  }
}
