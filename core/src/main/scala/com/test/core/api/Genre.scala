package com.test.core.api

/**
  * This enum represents show genre. Value passed inside every genre represent cost of tickets for this genre.
  */
object Genre extends Enumeration {
  type Genre = Value

  val musical = Value(70)
  val comedy = Value(50)
  val drama = Value(40)
}
