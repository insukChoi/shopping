package com.insuk.shopping.application.domain.model

import java.math.BigDecimal
import java.time.LocalDateTime

data class Product(
    val price: BigDecimal,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val brand: Brand,
    val category: Category,
)

data class ProductOfBrandLowest(
    val brandName: String,
    val categories: List<CategoryOfBrandLowest>,
) {
    data class CategoryOfBrandLowest(
        val categoryName: String,
        val price: BigDecimal,
    )
}

data class ProductOfLowestAndHighestPriceBrands(
    val lowestBrandWithPrice: BrandWithPrice?,
    val highestBrandWithPrice: BrandWithPrice?,
)
