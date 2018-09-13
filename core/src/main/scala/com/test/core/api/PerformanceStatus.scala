package com.test.core.api

/**
  * Performance status represents current status of specific performance. Performance lifecycle is in the same order
  * as this values.
  */
object PerformanceStatus extends Enumeration {
  type PerformanceStatus = Value

  val saleNotStarted = Value("Sale not started")
  val openForSale = Value("Open for sale")
  val soldOut = Value("Sold out")
  val inThePast = Value("In the past")


}
