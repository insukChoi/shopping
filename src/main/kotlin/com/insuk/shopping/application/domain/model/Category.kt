package com.insuk.shopping.application.domain.model

import java.time.LocalDateTime

data class Category(
    val name: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)
