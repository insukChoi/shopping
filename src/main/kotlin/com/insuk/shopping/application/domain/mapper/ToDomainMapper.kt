package com.insuk.shopping.application.domain.mapper

import com.insuk.shopping.application.domain.model.Brand
import com.insuk.shopping.application.domain.model.Category
import com.insuk.shopping.application.domain.model.Product
import com.insuk.shopping.application.port.output.BrandWithId
import com.insuk.shopping.application.port.output.CategoryWIthId
import com.insuk.shopping.application.port.output.ProductWithId
import com.insuk.shopping.common.Transformer

internal fun BrandWithId.toDomain(): Brand =
    Transformer(
        inClass = BrandWithId::class,
        outClass = Brand::class,
    ).transform(this)

internal fun CategoryWIthId.toDomain(): Category =
    Transformer(
        inClass = CategoryWIthId::class,
        outClass = Category::class,
    ).transform(this)

internal fun ProductWithId.toDomain(
    brand: Brand,
    category: Category,
): Product {
    return Transformer(
        inClass = ProductWithId::class,
        outClass = Product::class,
        param = mapOf(
            "brand" to brand,
            "category" to category,
        ),
    ).transform(this)
}
