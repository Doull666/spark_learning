package com.ll.sparksql.test

import com.typesafe.config.{Config, ConfigFactory}

object Demo01 {
  def main(args: Array[String]): Unit = {
    val config: Config = ConfigFactory.load()
    val table_in: String = config.getString("table_name_in")
    val table_out: String = config.getString("table_name_out")

    println(table_in)
    println(table_out)

  }
}
