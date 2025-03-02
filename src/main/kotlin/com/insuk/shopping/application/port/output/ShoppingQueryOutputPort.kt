package com.insuk.shopping.application.port.output

import com.insuk.shopping.application.domain.model.Product
import com.insuk.shopping.application.domain.model.ProductOfBrandLowest
import com.insuk.shopping.application.domain.model.ProductOfLowestAndHighestPriceBrands
import java.math.BigDecimal

interface ShoppingQueryOutputPort {
    fun getCategoryMinPrices(): List<Pair<Long, BigDecimal>>

    fun getProductByCategoryAndLowestPrice(
        categoryId: Long,
        lowestPrice: BigDecimal,
    ): Product

    fun getLowestBrandIdByCategories(): Long

    fun getProductOfLowestBrandByBrandId(brandId: Long): ProductOfBrandLowest

    fun getCategoryByCategoryName(categoryName: String): CategoryWIthId?

    fun getBrandByBrandName(brandName: String): BrandWithId?

    fun getLowestAndHighestPriceBrands(categoryId: Long): ProductOfLowestAndHighestPriceBrands
}
