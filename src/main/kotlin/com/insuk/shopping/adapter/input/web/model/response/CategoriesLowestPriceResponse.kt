package com.insuk.shopping.adapter.input.web.model.response

import java.math.BigDecimal

data class CategoriesLowestPriceResponse(
    val contents: List<CategoriesLowestPriceContent>,
) {
    val totalPrice: BigDecimal
        get() = contents.sumOf { it.price }

    data class CategoriesLowestPriceContent(
        val categoryName: String,
        val brandName: String,
        val price: BigDecimal,
    )
}
