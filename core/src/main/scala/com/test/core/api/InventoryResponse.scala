package com.test.core.api

import com.test.core.api.Genre.Genre

case class GenreResponse(genre: Genre, shows: List[PerformanceResponse])
case class InventoryResponse(inventory: List[GenreResponse])
