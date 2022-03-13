package com.ll.sparksql.sparkfunction.udaf

import java.text.DecimalFormat

/**
 * @Author lin_li
 * @Date 2022/3/11 18:18
 */
case class CityRatio(name: String, ratio: Double) {
  private val format = new DecimalFormat("0.00%")

  override def toString: String = {
    s"$name ${format.format(ratio)}"
  }
}
