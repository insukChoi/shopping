package com.insuk.shopping.application.port.output

import java.math.BigDecimal

typealias BrandId = Long
typealias CategoryId = Long
typealias ProductId = Long

interface ShoppingCommandOutputPort {
    fun addBrand(brandName: String): BrandWithId
    fun addCategory(categoryName: String): CategoryWIthId
    fun addProduct(price: BigDecimal, brandId: BrandId, categoryId: CategoryId): ProductWithId
    fun modifyProduct(price: BigDecimal, brandId: BrandId, categoryId: CategoryId): ProductWithId?
    fun removeProduct(brandId: BrandId, categoryId: CategoryId): Boolean
}
