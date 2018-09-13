package com.test.core.api

/**
  * Show status represents current status of specific show. Show lifecycle is in the same order
  * as this values.
  */
object ShowStatus extends Enumeration {
  type ShowStatus = Value

  val bigTheater = Value
  val smallTheater = Value
  val smallTheaterWithDiscount = Value
  val notExist = Value

}
