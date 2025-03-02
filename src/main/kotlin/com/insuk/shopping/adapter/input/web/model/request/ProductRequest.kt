package com.insuk.shopping.adapter.input.web.model.request

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import java.math.BigDecimal

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class ProductRequest(
    val brandName: String,
    val categoryName: String,
    val price: BigDecimal,
)

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class ProductDeleteRequest(
    val brandName: String,
    val categoryName: String,
)
