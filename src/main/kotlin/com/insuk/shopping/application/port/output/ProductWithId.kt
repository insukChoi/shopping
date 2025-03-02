package com.insuk.shopping.application.port.output

import java.math.BigDecimal
import java.time.LocalDateTime

data class ProductWithId(
    val id: Long,
    val price: BigDecimal,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)
