package com.test.core.services

import com.test.core.api.{Genre, GenreResponse, InventoryResponse, Show}
import org.joda.time.DateTime
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}

import scala.io.BufferedSource
import scala.util.matching.Regex

/**
  * class for accessing data of inventory.
  *
  * @param shows list of serialized shows. If you want to pass source and don't create shows by youself you should
  *              use InventoryService.apply.
  */
class InventoryService(shows: List[Show]) {

  def getInventoryResponse(queryDate: DateTime, showDate: DateTime): InventoryResponse = {
    val res = shows
      .map(show => show.getPerformanceResponse(queryDate, showDate))
      .flatMap(_.toOption)
      .groupBy(_.genre)
      .map(kv => GenreResponse(kv._1, kv._2))
      .toList
    InventoryResponse(res)
  }


}

object InventoryService {

  val regex: Regex = """\"(.*)\",(.*),\"(.*)\"""".r
  val formatter: DateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd")

  def apply(csvSource: BufferedSource): InventoryService = {
    val res = csvSource
      .getLines()
      .filter(_.nonEmpty)
      .map(cols => {
        val res = regex.findAllMatchIn(cols).next()
        val title = res.group(1)
        val date = formatter.parseDateTime(res.group(2))
        val genre = Genre.withName(res.group(3).toLowerCase)
        Show(title, date, genre)
      }).toList
    new InventoryService(res)
  }
}
