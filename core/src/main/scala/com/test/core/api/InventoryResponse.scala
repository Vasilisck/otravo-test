package com.test.core.api

import com.test.core.api.Genre.Genre
import com.test.core.api.PerformanceStatus.PerformanceStatus


/**
  * Case class represents specific performance data of specific show.
  *
  * @param title Name of the show.
  * @param ticketsLeft Number of doesn't sold tickets.
  * @param ticketsAvailable Number of available today tickets.
  * @param status Current status of the performance. For additional info look at PerformanceStatus object.
  * @param genre Genre of the performance.
  */
case class PerformanceResponse(title: String, ticketsLeft: Int, ticketsAvailable: Int, status: PerformanceStatus, genre: Genre)

/**
  * Case class represents all performance of specific genre.
  *
  * @param genre Genre of the performance.
  * @param shows List of PerformanceResponses.
  */
case class GenreResponse(genre: Genre, shows: List[PerformanceResponse])

/**
  * Case class represents all performance selected, grouped by Genre.
  *
  * @param inventory List of GenreResponses.
  */
case class InventoryResponse(inventory: List[GenreResponse])
