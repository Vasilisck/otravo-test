package com.test.core.api

import com.test.core.api.Genre.Genre
import com.test.core.api.PerformanceStatus.PerformanceStatus
import com.test.core.api.ShowStatus._
import com.test.core.config.Config._
import org.joda.time.{DateTime, Duration, Period}

import scala.util.{Failure, Success, Try}

//This case class represents one show. If in the future, if we decide to store data about performance of show, we should add
//field List[Performance] to this class and move getPerformanceResponse() into new class with all related methods.
case class Show(title: String, openingDay: DateTime, genre: Genre) {

  //main method for gathering performance data from show. Will return Success(performance response) if performance exist.
  //if performance not-exist will return failure(NoSuchElement)
  def getPerformanceResponse(queryDate: DateTime,
                            showDate: DateTime): Try[PerformanceResponse] = {
    val showStatus = getShowStatus(showDate)
    if (showStatus.equals(ShowStatus.notExist)) {
      Failure(new NoSuchElementException("performance not exists"))
    } else {
      val performanceStatus = getPerformanceStatus(queryDate, showDate, showStatus)
      val ticketsLeft = getTicketsLeft(queryDate, showDate, showStatus, performanceStatus)
      val ticketsAvailable = getTicketsAvailable(showStatus, performanceStatus)
      Success(PerformanceResponse(title, ticketsLeft, ticketsAvailable, performanceStatus, genre))
    }
  }

  private def getTicketsLeft(queryDate: DateTime,
                             showDate: DateTime,
                             showStatus: ShowStatus,
                             performanceStatus: PerformanceStatus): Int = performanceStatus match {
    case PerformanceStatus.soldOut => 0
    case PerformanceStatus.inThePast => 0
    case PerformanceStatus.saleNotStarted if showStatus.equals(ShowStatus.bigTheater) => bigTheaterCapacty
    case PerformanceStatus.saleNotStarted if showStatus.equals(ShowStatus.smallTheater)
      || showStatus.equals(ShowStatus.smallTheaterWithDiscount) => smallTheaterCapacity

    case PerformanceStatus.openForSale if showStatus.equals(ShowStatus.bigTheater) =>
      bigTheaterCapacty - new Period(showDate.minus(sellStartsUntilShowHappen), queryDate).getDays * bigTheaterTicketsPerDay

    case PerformanceStatus.openForSale if showStatus.equals(ShowStatus.smallTheater) || showStatus.equals(ShowStatus.smallTheaterWithDiscount) =>
      smallTheaterCapacity - new Period(showDate.minus(sellStartsUntilShowHappen), queryDate).getDays * smallTheaterTicketsPerDay
  }


  private def getTicketsAvailable(showStatus: ShowStatus,
                                  performanceStatus: PerformanceStatus): Int = performanceStatus match {
    case PerformanceStatus.soldOut => 0
    case PerformanceStatus.inThePast => 0
    case PerformanceStatus.saleNotStarted => 0
    case PerformanceStatus.openForSale if showStatus.equals(ShowStatus.bigTheater) => bigTheaterTicketsPerDay
    case PerformanceStatus.openForSale if showStatus.equals(ShowStatus.smallTheater) => smallTheaterTicketsPerDay
    case PerformanceStatus.openForSale if showStatus.equals(ShowStatus.smallTheaterWithDiscount) => smallTheaterTicketsPerDay
  }

  //get state of showing of this show. if showing is not exit throws NoSuchElementException.
  private def getPerformanceStatus(queryDate: DateTime, showDate: DateTime, showStatus: ShowStatus): PerformanceStatus = {
    if (queryDate.isBefore(showDate.minus(sellStartsUntilShowHappen))) PerformanceStatus.saleNotStarted
    else if (queryDate.isAfter(showDate)) PerformanceStatus.inThePast
    else if (!queryDate.isBefore(showDate.minus(sellStartsUntilShowHappen))
      && !queryDate.isAfter(showDate
      .minus(sellStartsUntilShowHappen)
      .plus(getSellDuration(showStatus))
    )) PerformanceStatus.openForSale
    else PerformanceStatus.soldOut
  }

  //get state of this show at specific query date. Need for understanding current state of show in it's lifecycle.
  private def getShowStatus(queryDate: DateTime): ShowStatus = queryDate match {

    case qDate if !qDate.isBefore(openingDay) && !qDate.isAfter(openingDay.plus(bigTheaterDuration)) =>
      ShowStatus.bigTheater

    case qDate if qDate.isAfter(openingDay.plus(bigTheaterDuration))
      && !qDate.isAfter(openingDay
      .plus(bigTheaterDuration)
      .plus(smallTheaterDuration)) =>
      ShowStatus.smallTheater

    case qDate if qDate.isAfter(openingDay
      .plus(bigTheaterDuration)
      .plus(smallTheaterDuration))
      && !qDate.isAfter(openingDay
      .plus(bigTheaterDuration)
      .plus(smallTheaterDuration)
      .plus(smallTheaterWithDiscountDuration)) =>
      ShowStatus.smallTheaterWithDiscount

    case _ => ShowStatus.notExist
  }

  private def getSellDuration(showStatus: ShowStatus): Duration = showStatus match {
    case ShowStatus.bigTheater => Duration.standardDays(bigTheaterCapacty / bigTheaterTicketsPerDay)
    case ShowStatus.smallTheater => Duration.standardDays(smallTheaterCapacity / smallTheaterTicketsPerDay)
    case ShowStatus.smallTheaterWithDiscount => Duration.standardDays(smallTheaterCapacity / smallTheaterTicketsPerDay)
  }
}
