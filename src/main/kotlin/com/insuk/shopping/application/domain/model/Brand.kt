package com.insuk.shopping.application.domain.model

import java.math.BigDecimal
import java.time.LocalDateTime

data class Brand(
    val name: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)

data class BrandWithPrice(
    val brandName: String,
    val price: BigDecimal,
)
