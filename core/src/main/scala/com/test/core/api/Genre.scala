package com.test.core.api

object Genre extends Enumeration {
  type Genre = Value

  val musical = Value(70)
  val comedy = Value(50)
  val drama = Value(40)
}
