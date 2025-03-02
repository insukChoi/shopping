package com.insuk.shopping.application.port.output

import java.time.LocalDateTime

data class CategoryWIthId(
    val id: Long,
    val name: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)
