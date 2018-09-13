package com.test.core.api

import com.test.core.api.Genre.Genre
import com.test.core.api.PerformanceStatus.PerformanceStatus
import com.test.core.api.ShowStatus._
import com.test.core.config.Config._
import org.joda.time.{DateTime, Duration, Period}

import scala.util.{Failure, Success, Try}

/**
  * This case class represents one show. If in the future, we decide to store data about performance of the show, we
  * should add field List[Performance] to this class and move getPerformanceResponse() into new class with all related
  * methods.
  *
  * @param title      Title of the show.
  * @param openingDay This datetime represents date of first performance of the show.
  * @param genre      Genre of the show.
  */
case class Show(title: String, openingDay: DateTime, genre: Genre) {

  /**
    * Main method for gathering performance data from show.
    *
    * @param queryDate The reference data that determines the inventory state.
    * @param showDate  The date for which you want to know how many tickets are left.
    * @return Will return Success(performance response) if performance exist. If performance doesn't exist,
    *         will return failure(NoSuchElement)
    */
  def getPerformanceResponse(queryDate: DateTime,
                             showDate: DateTime): Try[PerformanceResponse] = {
    val showStatus = getShowStatus(showDate)
    if (showStatus == ShowStatus.notExist) {
      Failure(new NoSuchElementException("performance doesn't exist"))
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
    case PerformanceStatus.saleNotStarted => getCapacity(showStatus)
    case PerformanceStatus.openForSale =>
      getCapacity(showStatus) - (getSailsDays(showDate, queryDate) * getTicketsPerDay(showStatus))
  }


  private def getTicketsAvailable(showStatus: ShowStatus,
                                  performanceStatus: PerformanceStatus): Int = performanceStatus match {
    case PerformanceStatus.soldOut => 0
    case PerformanceStatus.inThePast => 0
    case PerformanceStatus.saleNotStarted => 0
    case PerformanceStatus.openForSale => getTicketsPerDay(showStatus)
  }

  //get state of showing of this show. if showing is not exit throws NoSuchElementException.
  private def getPerformanceStatus(queryDate: DateTime,
                                   showDate: DateTime,
                                   showStatus: ShowStatus): PerformanceStatus = {
    if (queryDate.isBefore(showDate.minus(sellStartsUntilShowHappen))) {
      PerformanceStatus.saleNotStarted
    } else if (queryDate.isAfter(showDate)) {
      PerformanceStatus.inThePast
    } else if (!queryDate.isBefore(showDate.minus(sellStartsUntilShowHappen))
      && !queryDate.isAfter(showDate.minus(sellStartsUntilShowHappen).plus(getSellDuration(showStatus)))) {
      PerformanceStatus.openForSale
    }
    else PerformanceStatus.soldOut
  }

  //get state of this show at specific query date. Need for understanding current state of show in it's lifecycle.
  private def getShowStatus(queryDate: DateTime): ShowStatus = queryDate match {
    case qDate if qDate.isBefore(openingDay) => ShowStatus.notExist
    case qDate if !qDate.isAfter(openingDay.plus(bigTheaterDuration)) => ShowStatus.bigTheater
    case qDate if !qDate.isAfter(openingDay.plus(bigTheaterDuration).plus(smallTheaterDuration)) =>
      ShowStatus.smallTheater
    case qDate if !qDate.isAfter(openingDay.plus(bigTheaterDuration).plus(smallTheaterDuration)
      .plus(smallTheaterWithDiscountDuration)) => ShowStatus.smallTheaterWithDiscount
    case _ => ShowStatus.notExist
  }

  private def getTicketsPerDay(status: ShowStatus): Int = status match {
    case st if st == ShowStatus.bigTheater => bigTheaterTicketsPerDay
    case st if st == ShowStatus.smallTheater || status == ShowStatus.smallTheaterWithDiscount =>
      smallTheaterTicketsPerDay
  }

  private def getSailsDays(showDate: DateTime, queryDate: DateTime): Int = {
    new Period(showDate.minus(sellStartsUntilShowHappen), queryDate).getDays
  }

  private def getCapacity(status: ShowStatus): Int = status match {
    case st if st == ShowStatus.bigTheater => bigTheaterCapacty
    case st if st == ShowStatus.smallTheater || status == ShowStatus.smallTheaterWithDiscount => smallTheaterCapacity
  }

  private def divRoundUp(x: Int, y: Int): Int = math.ceil(x.toDouble / y.toDouble).toInt

  private def getSellDuration(showStatus: ShowStatus): Duration = showStatus match {
    case ShowStatus.bigTheater => Duration.standardDays(divRoundUp(bigTheaterCapacty, bigTheaterTicketsPerDay))
    case ShowStatus.smallTheater => Duration.standardDays(divRoundUp(smallTheaterCapacity, smallTheaterTicketsPerDay))
    case ShowStatus.smallTheaterWithDiscount =>
      Duration.standardDays(divRoundUp(smallTheaterCapacity, smallTheaterTicketsPerDay))
  }
}
