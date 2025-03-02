package com.insuk.shopping.adapter.input.web.model.request

import java.math.BigDecimal

data class ProductRequest(
    val brandName: String,
    val categoryName: String,
    val price: BigDecimal,
)

data class ProductDeleteRequest(
    val brandName: String,
    val categoryName: String,
)
