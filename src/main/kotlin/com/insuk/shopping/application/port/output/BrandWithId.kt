package com.insuk.shopping.application.port.output

import java.time.LocalDateTime

data class BrandWithId(
    val id: Long,
    val name: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)
