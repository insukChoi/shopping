package com.insuk.shopping.adapter.input.web.model.response

import java.math.BigDecimal

data class LowestAndHighestBrandWithPriceResponse(
    val categoryName: String,
    val lowestPrice: BrandWithPriceContent?,
    val highestPrice: BrandWithPriceContent?,
) {
    data class BrandWithPriceContent(
        val brandName: String,
        val price: BigDecimal,
    )
}
