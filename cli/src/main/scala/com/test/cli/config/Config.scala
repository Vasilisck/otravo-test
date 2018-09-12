package com.test.cli.config

import java.io.File

import org.joda.time.DateTime

case class Config(input: File = new File("."),
                  queryDate: DateTime = DateTime.now(),
                  showDate: DateTime = DateTime.now())