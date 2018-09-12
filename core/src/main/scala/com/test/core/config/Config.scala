package com.test.core.config

import org.joda.time.Duration

object Config {

  val bigTheaterDuration: Duration = Duration.standardDays(60)
  val smallTheaterDuration: Duration = Duration.standardDays(20)
  val smallTheaterWithDiscountDuration: Duration = Duration.standardDays(20)

  val bigTheaterCapacty = 200
  val smallTheaterCapacity = 100

  val bigTheaterTicketsPerDay = 10
  val smallTheaterTicketsPerDay = 5

  val sellStartsUntilShowHappen: Duration = Duration.standardDays(25)

}
