package com.insuk.shopping.adapter.input.web.model.response

import java.math.BigDecimal

data class BrandsLowestPriceResponse(
    val lowestPrice: BrandsLowestPriceContent,
) {
    data class BrandsLowestPriceContent(
        val brandName: String,
        val categories: List<BrandsLowestPriceCategory>,
    ) {
        val totalPrice: BigDecimal
            get() = categories.sumOf { it.price }

        data class BrandsLowestPriceCategory(
            val categoryName: String,
            val price: BigDecimal,
        )
    }
}
