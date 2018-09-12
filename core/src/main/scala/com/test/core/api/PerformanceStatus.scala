package com.test.core.api

object PerformanceStatus extends Enumeration {
  type PerformanceStatus = Value

  val saleNotStarted = Value("Sale not started")
  val openForSale = Value("Open for sale")
  val soldOut = Value("Sold out")
  val inThePast = Value("In the past")


}
