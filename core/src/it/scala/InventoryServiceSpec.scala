package com.test.core

import com.test.core.api.Genre
import com.test.core.services.InventoryService
import org.joda.time.DateTime
import org.scalatest.{Matchers, WordSpec}

import scala.io.{BufferedSource, Source}

class InventoryServiceSpec extends WordSpec with Matchers {

  val csv: BufferedSource = Source.fromResource("shows.csv")
  val inventory = InventoryService(csv)

  "Get inventory response" should {
    "return correct response" in {
      val res = inventory.getInventoryResponse(DateTime.parse("2018-06-06"), DateTime.parse("2018-06-26"))
      res.inventory should not be empty
      res.inventory.find(_.genre.equals(Genre.drama)).head.shows.length shouldBe 28
      res.inventory.find(_.genre.equals(Genre.comedy)).head.shows.length shouldBe 5
      res.inventory.find(_.genre.equals(Genre.musical)).head.shows.length shouldBe 9
    }
  }
}
