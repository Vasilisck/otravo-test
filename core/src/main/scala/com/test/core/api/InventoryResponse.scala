package com.test.core.api

import com.test.core.api.Genre.Genre

case class InventoryResponse(inventory: List[(Genre, List[PerformanceResponse])])
