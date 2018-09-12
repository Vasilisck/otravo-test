package com.test.core.api

import com.test.core.api.Genre.Genre
import com.test.core.api.PerformanceStatus.PerformanceStatus

case class PerformanceResponse(title: String, ticketsLeft: Int, ticketsAvailable: Int, status: PerformanceStatus, genre: Genre)
