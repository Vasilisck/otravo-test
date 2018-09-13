package com.test.core

import com.test.core.api.{Genre, PerformanceStatus, Show}
import com.test.core.config.Config._
import org.joda.time.DateTime
import org.scalatest.TryValues._
import org.scalatest.{Matchers, WordSpec}

class ShowSpec extends WordSpec with Matchers {

  val showPremiereDate: DateTime = DateTime.now()
  val showTitle = "show title"
  val showGenre: Genre.Value = Genre.drama
  val show: Show = Show(showTitle, showPremiereDate, showGenre)

  "Shows performance response status" should {
    "be 'sale not started' status" when {
      "query date is before 'sale starts' moment" in {
        val showDate = showPremiereDate
        val queryDate = showPremiereDate.minus(sellStartsUntilShowHappen).minusDays(1)
        val res = show.getPerformanceResponse(queryDate, showDate)
        res.isSuccess shouldBe true
        res.success.value.status shouldBe PerformanceStatus.saleNotStarted
      }
    }

    "be 'open for sale' status" when {
      "query date is exactly 'sale starts' moment" in {
        val showDate = showPremiereDate
        val queryDate = showPremiereDate.minus(sellStartsUntilShowHappen)
        val res = show.getPerformanceResponse(queryDate, showDate)
        res.isSuccess shouldBe true
        res.success.value.status shouldBe PerformanceStatus.openForSale
      }

      "query date is after 'sale starts' moment" in {
        val showDate = showPremiereDate
        val queryDate = showPremiereDate.minus(sellStartsUntilShowHappen).plusSeconds(1)
        val res = show.getPerformanceResponse(queryDate, showDate)
        res.isSuccess shouldBe true
        res.success.value.status shouldBe PerformanceStatus.openForSale
      }
    }

    //this test should be ignored if we start sell tickets right before performance
    "be 'sold out' status" when {
      "query date right before the show" in {
        val showDate = showPremiereDate
        val queryDate = showPremiereDate.minusSeconds(1)
        val res = show.getPerformanceResponse(queryDate, showDate)
        res.isSuccess shouldBe true
        res.success.value.status shouldBe PerformanceStatus.soldOut
      }

      "query date right at the show" in {
        val showDate = showPremiereDate
        val queryDate = showPremiereDate
        val res = show.getPerformanceResponse(queryDate, showDate)
        res.isSuccess shouldBe true
        res.success.value.status shouldBe PerformanceStatus.soldOut
      }
    }

    "be 'in the past" when {
      "query date right after the show" in {
        val showDate = showPremiereDate
        val queryDate = showPremiereDate.plusSeconds(1)
        val res = show.getPerformanceResponse(queryDate, showDate)
        res.isSuccess shouldBe true
        res.success.value.status shouldBe PerformanceStatus.inThePast
      }
    }
  }

  "show performance response title" should {
    "be same as show title" in {
      val showDate = showPremiereDate
      val queryDate = showPremiereDate
      val res = show.getPerformanceResponse(queryDate, showDate)
      res.isSuccess shouldBe true
      res.success.value.title shouldBe showTitle
    }
  }

  "show performance response genre" should {
    "be same as show genre" in {
      val showDate = showPremiereDate
      val queryDate = showPremiereDate
      val res = show.getPerformanceResponse(queryDate, showDate)
      res.isSuccess shouldBe true
      res.success.value.genre shouldBe showGenre
    }
  }

  "show performace response tickets available" should {
    "be zero" when {
      "sells not starts" in {
        val showDate = showPremiereDate
        val queryDate = showPremiereDate.minus(sellStartsUntilShowHappen).minusDays(1)
        val res = show.getPerformanceResponse(queryDate, showDate)
        res.isSuccess shouldBe true
        res.success.value.ticketsAvailable shouldBe 0
      }

      "sells ends" in {
        val showDate = showPremiereDate
        val queryDate = showPremiereDate
        val res = show.getPerformanceResponse(queryDate, showDate)
        res.isSuccess shouldBe true
        res.success.value.ticketsAvailable shouldBe 0
      }

      "show in the past" in {
        val showDate = showPremiereDate
        val queryDate = showPremiereDate.plusSeconds(1)
        val res = show.getPerformanceResponse(queryDate, showDate)
        res.isSuccess shouldBe true
        res.success.value.ticketsAvailable shouldBe 0
      }
    }

    "be equal maximum daily sells" when {
      "sells in progress in big theatre" in {
        val showDate = showPremiereDate
        val queryDate = showPremiereDate.minus(sellStartsUntilShowHappen)
        val res = show.getPerformanceResponse(queryDate, showDate)
        res.isSuccess shouldBe true
        res.success.value.ticketsAvailable shouldBe bigTheaterTicketsPerDay
      }

      "sells in progress in small theatre" in {
        val showDate = showPremiereDate.plus(bigTheaterDuration).plusDays(1)
        val queryDate = showDate.minus(sellStartsUntilShowHappen)
        val res = show.getPerformanceResponse(queryDate, showDate)
        res.isSuccess shouldBe true
        res.success.value.ticketsAvailable shouldBe smallTheaterTicketsPerDay
      }
    }
  }

  "show performance response tickets left" should {
    "be maximum theatre capacity" when {
      "tickets just start sells in big theatre" in {
        val showDate = showPremiereDate
        val queryDate = showPremiereDate.minus(sellStartsUntilShowHappen)
        val res = show.getPerformanceResponse(queryDate, showDate)
        res.isSuccess shouldBe true
        res.success.value.ticketsLeft shouldBe bigTheaterCapacty
      }

      "tickets just start sells in small theatre" in {
        val showDate = showPremiereDate.plus(bigTheaterDuration).plusDays(1)
        val queryDate = showDate.minus(sellStartsUntilShowHappen)
        val res = show.getPerformanceResponse(queryDate, showDate)
        res.isSuccess shouldBe true
        res.success.value.ticketsLeft shouldBe smallTheaterCapacity
      }
    }

    "be less on daily available count" when {
      "it's second selling day of big theatre" in {
        val showDate = showPremiereDate
        val queryDate = showPremiereDate.minus(sellStartsUntilShowHappen).plusDays(1)
        val res = show.getPerformanceResponse(queryDate, showDate)
        res.isSuccess shouldBe true
        res.success.value.ticketsLeft shouldBe bigTheaterCapacty - bigTheaterTicketsPerDay
      }

      "it's second selling day of small theatre" in {
        val showDate = showPremiereDate.plus(bigTheaterDuration).plusDays(1)
        val queryDate = showDate.minus(sellStartsUntilShowHappen).plusDays(1)
        val res = show.getPerformanceResponse(queryDate, showDate)
        res.isSuccess shouldBe true
        res.success.value.ticketsLeft shouldBe smallTheaterCapacity - smallTheaterTicketsPerDay
      }
    }

    "be zero" when {
      //should be ignored if we start sells tickets just before performance
      "show just about to begin" in{
        val showDate = showPremiereDate
        val queryDate = showPremiereDate.minusSeconds(1)
        val res = show.getPerformanceResponse(queryDate, showDate)
        res.isSuccess shouldBe true
        res.success.value.ticketsLeft shouldBe 0
      }

      "show begins" in{
        val showDate = showPremiereDate
        val queryDate = showPremiereDate
        val res = show.getPerformanceResponse(queryDate, showDate)
        res.isSuccess shouldBe true
        res.success.value.ticketsLeft shouldBe 0
      }

      "show ends" in{
        val showDate = showPremiereDate
        val queryDate = showPremiereDate.plusSeconds(1)
        val res = show.getPerformanceResponse(queryDate, showDate)
        res.isSuccess shouldBe true
        res.success.value.ticketsLeft shouldBe 0
      }
    }
  }

}
