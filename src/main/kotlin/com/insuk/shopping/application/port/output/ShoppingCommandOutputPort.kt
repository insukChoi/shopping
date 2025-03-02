package com.insuk.shopping.application.port.output

import com.insuk.shopping.application.domain.model.Brand
import com.insuk.shopping.application.domain.model.Category
import com.insuk.shopping.application.domain.model.ProductOnly
import java.math.BigDecimal

typealias BrandId = Long
typealias CategoryId = Long
typealias ProductId = Long

interface ShoppingCommandOutputPort {
    fun addBrand(brandName: String): Pair<BrandId, Brand>
    fun addCategory(categoryName: String): Pair<CategoryId, Category>
    fun addProduct(price: BigDecimal, brandId: BrandId, categoryId: CategoryId): Pair<ProductId, ProductOnly>
    fun modifyProduct(price: BigDecimal, brandId: BrandId, categoryId: CategoryId): Pair<ProductId?, ProductOnly?>
    fun removeProduct(brandId: BrandId, categoryId: CategoryId): Boolean
}
