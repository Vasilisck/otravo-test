package com.test.core

import com.test.core.api.Genre
import com.test.core.services.InventoryService
import org.joda.time.DateTime
import org.scalatest.{Matchers, WordSpec}

import scala.io.{BufferedSource, Source}

class InventoryServiceSpec extends WordSpec with Matchers {

  val csv: BufferedSource = Source.fromResource("shows.csv")
  val inventory = new InventoryService(csv)

  "Get inventory response" should {
    "return correct response" in {
      val res = inventory.getInventoryResponse(DateTime.parse("2018-06-06"), DateTime.parse("2018-06-26"))
      res.inventory should not be empty
      res.inventory.find(_._1.equals(Genre.drama)).head._2.length shouldBe 28
      res.inventory.find(_._1.equals(Genre.comedy)).head._2.length shouldBe 5
      res.inventory.find(_._1.equals(Genre.musical)).head._2.length shouldBe 9
    }
  }
}
