package com.test.cli

import java.io.File

import com.test.cli.config.Config
import com.test.core.api.{Genre, PerformanceStatus}
import com.test.core.services.InventoryService
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}
import org.json4s._
import org.json4s.jackson.Serialization.write
import scopt.OptionParser
import org.json4s.ext.EnumNameSerializer

import scala.io.Source

object Main extends App {
  implicit val formats = DefaultFormats + new EnumNameSerializer(Genre) + new EnumNameSerializer(PerformanceStatus)
  val formatter: DateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd")

  val parser = new OptionParser[Config]("otravo-test-cli") {
    head("otravo test CLI", "1.0")

    opt[File]('i', "input").required().valueName("<input>")
      .action((input, conf) => conf.copy(input = input))
      .text("An input file consisting of the show inventory.")

    opt[String]('q', "query-date").required().valueName("<value>")
      .action((queryDate, conf) => conf.copy(queryDate = formatter.parseDateTime(queryDate)))
      .text("The reference data that determines the inventory state.")

    opt[String]('s', "show-date").required().valueName("<value>")
      .action((showDate, conf) => conf.copy(showDate = formatter.parseDateTime(showDate)))
      .text("The date for which you want to know how many tickets are left.")
  }

  parser.parse(args, Config()) match {
    case Some(config) =>
      val inventory = new InventoryService(Source.fromFile(config.input))
      val response = inventory.getInventoryResponse(config.queryDate, config.showDate)
      println(write(response))

    case None =>
      println("please, pass proper params")
  }

}
