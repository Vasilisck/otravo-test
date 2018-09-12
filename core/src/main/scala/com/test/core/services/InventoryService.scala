package com.test.core.services

import com.test.core.api.{Genre, InventoryResponse, Show}
import org.joda.time.DateTime
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}

import scala.io.BufferedSource
import scala.util.matching.Regex

class InventoryService(shows: List[Show]) {

  def this(csvSource: BufferedSource)(implicit formatter: DateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd")) = {
    this(csvSource
      .getLines()
      .filter(_.nonEmpty)
      //.map(line => line.split(",").map(_.trim))
      .map(cols => {
      val regex: Regex = """\"(.*)\",(.*),\"(.*)\"""".r
      val res = regex.findAllMatchIn(cols).next()

      val title = res.group(1)
      val date = formatter.parseDateTime(res.group(2))
      val genre = Genre.withName(res.group(3).toLowerCase)
      Show(title, date, genre)
    }).toList)
  }

  def getInventoryResponse(queryDate: DateTime, showDate: DateTime): InventoryResponse = {
    val res = shows
      .map(show => show.getPerformanceResponse(queryDate, showDate))
      .flatMap(_.toOption)
      .groupBy(_.genre)
      .toList
    InventoryResponse(res)
  }


}
