package com.insuk.shopping.application.port.output

import com.insuk.shopping.application.domain.model.*
import java.math.BigDecimal

interface ShoppingQueryOutputPort {
    fun getCategoryMinPrices(): List<Pair<Long, BigDecimal>>

    fun getProductByCategoryAndLowestPrice(
        categoryId: Long,
        lowestPrice: BigDecimal,
    ): Product

    fun getLowestBrandIdByCategories(): Long

    fun getProductOfLowestBrandByBrandId(brandId: Long): ProductOfBrandLowest

    fun getCategoryByCategoryName(categoryName: String): Pair<CategoryId?, Category?>

    fun getBrandByBrandName(brandName: String): Pair<BrandId?, Brand?>

    fun getLowestAndHighestPriceBrands(categoryId: Long): ProductOfLowestAndHighestPriceBrands
}
